<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<sqlMap namespace="${namespace}">
	<typeAlias alias="${className?uncap_first}" type="${classFullName}"/>
    <resultMap id="${className?uncap_first}" class="${classFullName}">
    <#list fields?keys as key >
        <result column="${fields[key]}" property="${key}" jdbcType="${jdbctype[key]}"/>
    </#list>
    </resultMap>

    <#assign values=""/>
    <sql id="Column_List">
        <#list fields?keys as key >
            <#if (key_index) == 0>${"\t\t"}</#if><#t/>
            ${fields[key]}<#if key_has_next>,</#if><#t/>
            <#if (key_index+1) % 5 == 0>${"\n\t\t"}</#if><#t/>
            <#if !key_has_next>${"\n"}</#if><#t/>
        </#list>
    </sql>
    <insert id="create" parameterClass="${className?uncap_first}">
        <#if sqlStatement?exists>
        <selectKey resultClass="java.lang.Long" keyProperty="sequenceId">
            ${sqlStatement?upper_case} 
        </selectKey>
        </#if>
        INSERT INTO ${tableName}(
        <#list fields?keys as key >
            <#if (key_index) == 0>${"\t\t\t"}</#if><#t/>
            ${fields[key]}<#t/>
            <#assign flag="true"/>
            <#list primaryKey as pk>
            <#if fields[key] == pk>
            	<#if sqlStatement?exists>
                <#assign flag="false"/>
                <#break/>
                </#if>
            </#if>
            </#list>
            <#if flag="true">
                <#assign values=values+"#"+key+"#"/><#t/>
            <#else>
                <#assign values=values+"#sequenceId#"/><#t/>
            </#if>
            <#if key_has_next>
                  ,<#assign values=values+","/><#t/>
            </#if>
            <#if (key_index+1) % 5 == 0>${"\n\t\t\t"}<#assign values=values+"\n\t\t\t"/></#if><#t/>
            <#if !key_has_next>${"\n"}<#assign values=values+"\n\t\t"/></#if><#t/>
        </#list>
        )
        VALUES(
            ${values})
    </insert>

    <update id="update" parameterClass="${className?uncap_first}">
        UPDATE ${tableName}
        <dynamic prepend="SET"> 
        <#list fields?keys as key >
            <#assign flag="true"/>
            <#list primaryKey as pk>
            <#if fields[key] == pk><#t/>
                 <#assign flag="false"/><#break/>
            </#if>
            </#list>
            <#if flag=="true">
        <isNotNull prepend="," property="${key}">
            ${fields[key]} = #${key}#
        </isNotNull>
            </#if> 
        </#list>
        </dynamic>
		WHERE
        <#list fields?keys as key >
            <#list primaryKey as pk>
            <#if fields[key] == pk>
            ${fields[key]} = #${key}# 
            </#if>
            </#list>
        </#list>
    </update>
    
    <#if primaryKey?size == 1>
    <delete id="deleteByPrimaryKey" parameterClass="java.lang.Long">
        DELETE FROM ${tableName} 
		WHERE
        <#list fields?keys as key >
            <#list primaryKey as pk>
            <#if fields[key] == pk>
            ${fields[key]} = #${key}# 
            </#if>
            </#list>
        </#list>
    </delete>
    </#if>

    <#if primaryKey?size == 1>
    <select id="findById" resultMap="${className?uncap_first}"
        parameterClass="java.lang.Long">
        SELECT 
        <#list fields?keys as key >
            <#if (key_index) == 0>${"\t\t\t"}</#if><#t/>
            ${fields[key]}<#if key_has_next>,</#if><#t/>
            <#if (key_index+1) % 5 == 0>${"\n\t\t\t"}</#if><#t/>
            <#if !key_has_next>${"\n"}</#if><#t/>
        </#list>
        FROM ${tableName}
        WHERE 
        <#list fields?keys as key >
            <#list primaryKey as pk>
            <#if fields[key] == pk>
            ${fields[key]} = #${key}# 
            </#if>
            </#list>
        </#list>
    </select>
    </#if>
    
    <select id="findBySelective" resultMap="${className?uncap_first}">
        SELECT 
        <#list fields?keys as key >
            <#if (key_index) == 0>${"\t\t\t"}</#if><#t/>
            ${fields[key]}<#if key_has_next>,</#if><#t/>
            <#if (key_index+1) % 5 == 0>${"\n\t\t\t"}</#if><#t/>
            <#if !key_has_next>${"\n"}</#if><#t/>
        </#list>
        FROM ${tableName}
        <dynamic prepend="WHERE"> 
        <#list fields?keys as key >
        <isNotNull prepend="AND" property="${key}">
            ${fields[key]} = #${key}# 
        </isNotNull>
        </#list>
        </dynamic>
    </select>

    <select id="findBySelective_COUNT"  resultClass="java.lang.Integer">
        SELECT count(*) FROM ${tableName}
        <dynamic prepend="WHERE">
        <#list fields?keys as key >
        <isNotNull prepend="AND" property="${key}">
            ${fields[key]} = #${key}# 
        </isNotNull>
        </#list>
        </dynamic>
    </select>
</sqlMap>