package com.woyo.pay.tools.ibatis;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.woyo.pay.schema.tools.JdbcConnection;


public class ORMTools {
	

	public String inputstream2String(InputStream in) throws Exception{
		List<String> lines = IOUtils.readLines(in);
		StringBuffer sb = new StringBuffer();
		for(String line:lines){
			sb.append(line);
		}
		return sb.toString();
	}
	
	public Connection getConnection(JdbcConnection config) throws SQLException, ClassNotFoundException{
	
		assert config != null;
		String driverClass = config.getDriverClass();
		String connectionUrl = config.getConnectionURL();
		String userId = config.getUserId();
		String passwd = config.getPassword();
		assert driverClass != null :"driverClass Can't be null";
		assert connectionUrl != null:"connectionUrl Can't be null";
		assert userId != null :"userId Can't be null";
		assert passwd != null :"Password Can't be null";
		Class.forName(driverClass);
		Properties props =new Properties();
		props.put("remarksReporting","true");
		props.put("user",userId);
		props.put("password",passwd);

		Connection conn = DriverManager.getConnection(connectionUrl,props);
		return conn;
	}
	
	public String toJavaStyle(String value){
		char[] old = value.toLowerCase().toCharArray();
		StringBuffer result = new StringBuffer();
		boolean flag = false;
		for(char b:old){
			if(b == '_') {
				flag = true;
				continue;
			}
			if(flag){
				flag = false;
				if(b >= 'a' && b <= 'z')
					b-= 32;
			}
			result.append(b);
		}
		return result.toString();
	}
	
	public static void main(String[] args) throws Exception{
		ORMTools tools = new ORMTools();
		System.out.println(tools.toJavaStyle("TEST_VALUE"));
	}

	public String getJavaType(String columnType,int precision,int scale) {
		if(columnType.equals("VARCHAR2")){
			return "String";
		}else if(columnType.equals("NUMBER")){
			if(scale == 0){
				if(precision <=5 )
					return "Integer";
				else
					return "Long";
			}else{
//				return "Double";
				return "Long";
			}
		}else if(columnType.equals("DATE")){
			return "java.util.Date";
		}else if(columnType.equals("TIMESTAMP")){
			return "java.util.Date";
		/**
		 * MYSQL兼容
		 */
		}else if(columnType.equalsIgnoreCase("bigint")){
			if(scale == 0){
				if(precision <=5 )
					return "Integer";
				else
					return "Long";
			}else{
				return "Double";
			}
		}else if(columnType.equalsIgnoreCase("datetime")){
			return "java.util.Date";
	
		}else if(columnType.equalsIgnoreCase("VARCHAR")){
			return "String";
		}else if(columnType.equalsIgnoreCase("INT")){
			return "Integer";
		}else if(columnType.equalsIgnoreCase("BIGINT")){
			return "java.math.BigDecimal";
		}else if(columnType.equalsIgnoreCase("INT UNSIGNED")){
			return "Integer";
		}else if(columnType.equalsIgnoreCase("SMALLINT UNSIGNED")){
			return "Integer";
		}else if(columnType.equalsIgnoreCase("MEDIUMINT UNSIGNED")){
			return "Integer";
		}else if(columnType.equalsIgnoreCase("TINYINT UNSIGNED")){
			return "Integer";
		}else if(columnType.equalsIgnoreCase("TINYINT")){
			return "Integer";	
		}else if(columnType.equalsIgnoreCase("DECIMAL")){
			return "Double";	
		}else if(columnType.equalsIgnoreCase("DECIMAL UNSIGNED")){
			return "Double";			
		}else if(columnType.equalsIgnoreCase("SMALLINT")){
			return "Integer";
		}else if(columnType.equalsIgnoreCase("CHAR")){
			return "String";
		}else if(columnType.equalsIgnoreCase("FLOAT")){
			return "Float";
		}else if(columnType.equalsIgnoreCase("DOUBLE")){
			return "Double";			
		}else if (columnType.equalsIgnoreCase("BLOB")) {
			return "java.sql.Blob";
		}
		
		else{
			throw new RuntimeException("Don't support [" + columnType + "]");
		}
	}
	
	public String getJDBCType(String columnType){
		if(columnType.equals("VARCHAR2")){
			return "VARCHAR";
		}else if(columnType.equals("NUMBER")){
			return "DECIMAL";
		}else if(columnType.equals("DATE")){
			return "TIMESTAMP";
		}else if(columnType.equals("TIMESTAMP")){
			return "TIMESTAMP";
		/**
		 * MYSQL兼容
		 */
		}else if(columnType.equalsIgnoreCase("bigint")){
			return "DECIMAL";
		}else if(columnType.equalsIgnoreCase("datetime")){
			return "TIMESTAMP";
	
		}else if(columnType.equalsIgnoreCase("VARCHAR")){
			return "VARCHAR";
		}else if(columnType.equalsIgnoreCase("INT")){
			return "DECIMAL";
		}else if(columnType.equalsIgnoreCase("INT UNSIGNED")){
			return "DECIMAL";
		}else if(columnType.equalsIgnoreCase("SMALLINT UNSIGNED")){
			return "DECIMAL";	
		}else if(columnType.equalsIgnoreCase("MEDIUMINT UNSIGNED")){
			return "DECIMAL";	
		}else if(columnType.equalsIgnoreCase("TINYINT UNSIGNED")){
			return "DECIMAL";
		}else if(columnType.equalsIgnoreCase("TINYINT")){
			return "DECIMAL";			
		}else if(columnType.equalsIgnoreCase("DECIMAL")){
			return "DECIMAL";	
		}else if(columnType.equalsIgnoreCase("DECIMAL UNSIGNED")){
			return "DECIMAL";			
		}else if(columnType.equalsIgnoreCase("SMALLINT")){
			return "DECIMAL";	
		}else if(columnType.equalsIgnoreCase("CHAR")){
			return "CHAR";
		}else if(columnType.equalsIgnoreCase("FLOAT")){
			return "FLOAT";
		}else if(columnType.equalsIgnoreCase("DOUBLE")){
			return "DECIMAL";			
		}else if (columnType.equalsIgnoreCase("BLOB")) {
			return "BLOB";
		}else{
			throw new RuntimeException("Don't support [" + columnType + "]");
		}
		
	}
}
