<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoPackagePath}.${entityClassName}Dao">
    <!-- baseResultMap、tableName、baseColumns、insertValues可以自动更新，请勿修改此区域mapper  -->
    <#include "mapper/baseResultMap.ftl">
    <#include "mapper/tableName.ftl">
    <#include "mapper/baseColumns.ftl">
    <#include "mapper/baseColumnsWithoutId.ftl">

    <!-- 以下sql可以自动更新，请勿修改此区域mapper -->
    <insert id="add" parameterType="${entityPackagePath}.${entityClassName}" useGeneratedKeys="true" keyProperty="id">
        insert into <include refid="tableName"/> (<include refid="baseColumnsWithoutId"/>)
        values (${insertValuesWithoutId})
    </insert>

    <insert id="addBatch" parameterType="${entityPackagePath}.${entityClassName}" useGeneratedKeys="true" keyProperty="id">
        insert into <include refid="tableName"/> (<include refid="baseColumnsWithoutId"/>)
        values
        <foreach collection="list" item="item" separator=",">
            (${batchInsertValuesWithoutId})
        </foreach>
    </insert>

    <delete id="remove">
        delete from <include refid="tableName"/> where id=${wellNumberPre}id${wellNumberEnd}
    </delete>

    <delete id="removeByIds" parameterType="java.util.List">
        delete from <include refid="tableName"/> where id in
        <foreach collection="list" item="item" open="(" separator="," close=")">
            ${wellNumberPre}item${wellNumberEnd}
        </foreach>
    </delete>

    <update id="update" parameterType="${entityPackagePath}.${entityClassName}">
        update <include refid="tableName"/>
        set
        <#assign num=0>
        <#list propsWithoutId as prop>
            <#assign num=num+1>
            <#assign size=propsWithoutId?size>
            ${prop.col}=${wellNumberPre}${prop.prop}${wellNumberEnd}<#if (num!=size)>,</#if>
        </#list>
        where id=${wellNumberPre}id${wellNumberEnd}
    </update>

    <update id="updateBatch" parameterType="java.util.List">
        <foreach collection="list" item="item" separator=";">
            update <include refid="tableName"/>
            set
            <#assign num=0>
            <#list propsWithoutId as prop>
                <#assign num=num+1>
                <#assign size=propsWithoutId?size>
                ${prop.col}=${wellNumberPre}item.${prop.prop}${wellNumberEnd}<#if (num!=size)>,</#if>
            </#list>
            where id=${wellNumberPre}item.id${wellNumberEnd}
        </foreach>
    </update>

    <select id="get" resultMap="baseResultMap">
        select
        <include refid="baseColumns"/>
        from <include refid="tableName"/>
        where id=${wellNumberPre}id${wellNumberEnd}
    </select>

    <select id="findAll" resultMap="baseResultMap">
        select
        <include refid="baseColumns"/>
        from <include refid="tableName"/>
    </select>

    <select id="findByIds" resultMap="baseResultMap">
        select
        <include refid="baseColumns"/>
        from <include refid="tableName"/>
        where id in
        <foreach collection="list" item="item" open="(" separator="," close=")">
            ${wellNumberPre}item${wellNumberEnd}
        </foreach>
    </select>

    <!-- 请在以下书写自定义mapper -->

</mapper>