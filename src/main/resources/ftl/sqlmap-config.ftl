<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMapConfig
 PUBLIC "-//ibatis.apache.org//DTD SQL Map Config 2.0//EN"
 "http://ibatis.apache.org/dtd/sql-map-config-2.dtd">

<sqlMapConfig>
    <settings useStatementNamespaces="true" />
    
<#list files?keys as key >
    <sqlMap resource="${files[key]}"/>
</#list>
</sqlMapConfig>
