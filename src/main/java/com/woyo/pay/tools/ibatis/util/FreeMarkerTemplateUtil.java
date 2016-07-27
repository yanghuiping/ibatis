package com.woyo.pay.tools.ibatis.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.util.Map;

import org.testng.log4testng.Logger;


import freemarker.template.Template;

public class FreeMarkerTemplateUtil {

//	private static Logger log = Logger.getLogger(FreeMarkerTemplateUtil.class);
	public static void templateParser(Reader templateReader ,Map<String,Object> content,String targetField) {
		try {
			Template template;
			template = new Template("", templateReader, null);
			File file = new File(targetField.substring(0,targetField.lastIndexOf(File.separator)>0 ?targetField.lastIndexOf(File.separator):targetField.length()));
			System.out.println(file.getAbsolutePath());
			if(!file.exists()) 
				file.mkdirs();
			System.out.println(file.exists());
			FileWriter out = new FileWriter(targetField);
			
			template.process(content, out);
		} catch (Exception e) {
//			log.error(e);
//			log.error("templateContent = " + templateReader + "\ncontent = " + content);
			e.printStackTrace();
		}
	}
}
