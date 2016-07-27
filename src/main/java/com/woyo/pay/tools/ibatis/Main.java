package com.woyo.pay.tools.ibatis;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.StringUtils;

import com.woyo.pay.schema.tools.Config;
import com.woyo.pay.schema.tools.Context;
import com.woyo.pay.schema.tools.Table;
import com.woyo.pay.tools.ibatis.util.FreeMarkerTemplateUtil;
import com.woyo.pay.tools.ibatis.util.JiBXUtil;

public class Main {

	public static void main(String[] args) throws Exception {

		ORMTools tools = new ORMTools();
		InputStream in = ORMTools.class
				.getResourceAsStream("/config/config.xml");
		String xml = tools.inputstream2String(in);
		Config config = JiBXUtil.xml2Object(Config.class, xml);
		List<Context> contexts = config.getContexts();
		for (Context context : contexts) {

			Connection conn = tools.getConnection(context.getJdbcConnection());
			DatabaseMetaData dbMetaData = conn.getMetaData();
			
			Statement st = conn.createStatement();
			List<Table> tables = context.getTables();
			// SqlMap配置文件
			Map<String, Object> sqlmapConfigModel = new HashMap<String, Object>();
			Map<String, String> sqlmapFilesModel = new HashMap<String, String>();
			for (Table table : tables) {
				String tableName = table.getTableName();
				String packageName = table.getPackageName();
				if (packageName == null)
					packageName = context.getJavaModelGenerator()
							.getTargetPackage();
				String className = StringUtils.capitalize(tools
						.toJavaStyle(tableName));
				ResultSet rs = st.executeQuery("SELECT * FROM " + tableName
						+ " limit 0,1");
				Map<String, Object> configModel = new HashMap<String, Object>();
				// java bean model
				Map<String, Object> javaBeanModel = new HashMap<String, Object>();
				
				ResultSet colRs = dbMetaData.getColumns(null, null, tableName,null);
				ResultSet tbRs = dbMetaData.getTables(null, null, tableName, null);
				if(tbRs.next()){
					String tbComment = tbRs.getString("REMARKS");
					javaBeanModel.put("tbComment", tbComment);
				}
				
				preparedData(tools, table, packageName, className, rs,
						configModel, javaBeanModel,colRs);

				rs.close();

				String javaModelFileName = context.getJavaModelGenerator()
						.getTargetProject()
						+ File.separator
						+ packageName.replace('.', File.separatorChar)
						+ File.separator + className + ".java";
				writeFile("/ftl/javaModel.ftl", javaModelFileName,
						javaBeanModel);

				String javaDtoFileName = context.getJavaModelGenerator()
						.getTargetProject()
						+ File.separator
						+ packageName.replace(".model", ".dto").replace('.',
								File.separatorChar)
						+ File.separator
						+ className + "DTO.java";
				writeFile("/ftl/javaDto.ftl", javaDtoFileName, javaBeanModel);

				String javaDaoFileName = context.getJavaModelGenerator()
						.getTargetProject()
						+ File.separator
						+ packageName.replace(".model", ".dao").replace('.',
								File.separatorChar)
						+ File.separator
						+ className + "DAO.java";
				writeFile("/ftl/javaDao.ftl", javaDaoFileName, javaBeanModel);

				String javaDaoImplFileName = context.getJavaModelGenerator()
						.getTargetProject()
						+ File.separator
						+ packageName.replace(".model", ".dao").concat(".impl")
								.replace('.', File.separatorChar)
						+ File.separator + className + "DAOImpl.java";
				writeFile("/ftl/javaDaoImpl.ftl", javaDaoImplFileName,
						javaBeanModel);

				String ibatisconfigFileName = context.getSqlmapGenerator()
						.getTargetProject()
						+ File.separator
						+ "ibatis"
						+ File.separator
						+ className.toLowerCase()
						+ File.separator
						+ className
						+ ".xml";
				writeFile("/ftl/iBatisConfig.ftl", ibatisconfigFileName,
						configModel);
				sqlmapFilesModel.put(tableName, "ibatis" + File.separator
						+ className.toLowerCase() + File.separator + className
						+ ".xml");

			}
			st.close();
			conn.close();
			sqlmapConfigModel.put("files", sqlmapFilesModel);

			String sqlmapConfigFileName = context.getSqlmapGenerator()
					.getTargetProject()
					+ File.separator + "changeit-sqlmap-config.xml";
			writeFile("/ftl/sqlmap-config.ftl", sqlmapConfigFileName,
					sqlmapConfigModel);

		}

	}

	private static void writeFile(String ftlFileName, String outputFileName,
			Map<String, Object> modelData) {
		InputStreamReader reader = new InputStreamReader(ORMTools.class
				.getResourceAsStream(ftlFileName));

		FreeMarkerTemplateUtil
				.templateParser(reader, modelData, outputFileName);
	}

	private static void preparedData(ORMTools tools, Table table,
			String packageName, String className, ResultSet rs,
			Map<String, Object> configModel, Map<String, Object> javaBeanModel,ResultSet colRs)
			throws SQLException {
		

		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();
		// ibatis config model

		javaBeanModel.put("packageName", packageName);
		javaBeanModel.put("description", table.getTableName());
		javaBeanModel.put("className", className);

		configModel.put("namespace", className.toLowerCase());
		configModel.put("classFullName", packageName + "." + className);
		configModel.put("className", className);
		configModel.put("tableName", table.getTableName().toUpperCase());

		Map<String, String> fieldsType = new HashMap<String, String>();
		Map<String, String> fieldsColumn = new HashMap<String, String>();
		Map<String, String> fieldsJDBCType = new HashMap<String, String>();
		Map<String, String> fieldsColumnComment = new HashMap<String, String>();
		for (int i = 1; i <= numberOfColumns; i++) {
			colRs.next();
			String columnComment = colRs.getString("REMARKS");
			
			String columnName = rsMetaData.getColumnName(i);
//			String columnComment = rsMetaData.getc
			String javaFieldName = tools.toJavaStyle(columnName);
			String columnType = rsMetaData.getColumnTypeName(i);
			fieldsColumnComment.put(javaFieldName, columnComment);
			fieldsType.put(javaFieldName, tools.getJavaType(columnType,
					rsMetaData.getPrecision(i), rsMetaData.getScale(i)));
			fieldsColumn.put(javaFieldName, columnName.toUpperCase());
			fieldsJDBCType.put(javaFieldName, tools.getJDBCType(columnType));
			
		}
		
		javaBeanModel.put("fields", fieldsType);
		javaBeanModel.put("comments",fieldsColumnComment);
		
		if (table.getGeneratedKey() != null) {
			configModel.put("sqlStatement", table.getGeneratedKey()
					.getSqlStatement());

		}
		configModel.put("jdbctype", fieldsJDBCType);
		configModel.put("primaryKey", table.getPrimaryKey().toUpperCase()
				.split(","));
		configModel.put("fields", fieldsColumn);
	}
}
