package com.woyo.pay.tools.ibatis.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;


public class JiBXUtil {

	//缓存已经创建好的JiBXContext
	public static final ConcurrentMap<String,IBindingFactory> cacheMap = new ConcurrentHashMap<String, IBindingFactory>();
//	public static final ConcurrentMap<String,IMarshallingContext> mashCacheMap = new ConcurrentHashMap<String, IMarshallingContext>();

	/**
	 * 将xml转换为对象.
	 * @param <T> 要转换的类型
	 * @param type 要转换的类型
	 * @param xml 需要转换的xml
	 * @return 转换好的结果
	 * @throws JiBXException
	 */
	public static <T> T xml2Object(Class<T> type,String xml) throws JiBXException
	{
		IUnmarshallingContext context = getJiBXUnmarshallingContext(type);
		StringReader sr = new StringReader(xml);
		T t = (T) context.unmarshalDocument(sr);
		return t;
	}
	
	/**
	 * 把对象转换为xml。
	 * @param <T> 要转换的对象的类型
	 * @param object 转换的对象 
	 * @return 转换后的结果
	 * @throws JiBXException
	 */
	public static <T> String object2Xml(T object) throws JiBXException
	{
		IMarshallingContext context = getJiBXMarshallingContext(object.getClass());
		
		StringWriter sw = new StringWriter();
		context.setOutput(sw);
		context.marshalDocument(object);
		
		return sw.toString();
		
	}
	/**
	 * 得到JiBX UnmarshallingContext。如果已经存在就从缓存中拿出。如果不存在就创建，然后放进缓存
	 * @param clazz 要创建的类型
	 * @return 得到的Context
	 * @throws JiBXException
	 */
	private static IUnmarshallingContext getJiBXUnmarshallingContext(Class clazz) throws JiBXException 
	{
		IUnmarshallingContext context = getBindingFactory(clazz).createUnmarshallingContext();
		return context;
	}

	/**
	 * 得到JiBX MarchallingContext，如果存在就从缓存中拿出，不存在就创建一个并放到缓存中。
	 * @param clazz 要创建的类型
	 * @return 得到的的Context
	 * @throws JiBXException
	 */
	private static IMarshallingContext getJiBXMarshallingContext(Class clazz) throws JiBXException 
	{
		IMarshallingContext context = getBindingFactory(clazz).createMarshallingContext();
		return context;
	}
	
	private static IBindingFactory getBindingFactory(Class clazz) throws JiBXException
	{
		String className = clazz.getName();
		IBindingFactory bindingFactory = null;
		synchronized (cacheMap) {

			if (cacheMap.containsKey(className)) {
				bindingFactory = cacheMap.get(className);
			} else {
				 bindingFactory = BindingDirectory.getFactory(clazz);
				cacheMap.put(className, bindingFactory);
			}
		}
		return bindingFactory;
	}
}
