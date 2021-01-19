<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoPackagePath}.I${entityClassName}Dao">
    <!-- baseResultMap、tableName、baseColumns、insertValues可以自动更新 -->
    <#include "./mapper/baseResultMap.ftl">
    <#include "./mapper/tableName.ftl">
    <#include "./mapper/baseColumns.ftl">
    <#include "./mapper/insertValues.ftl">

    <!-- 以下sql可以自动更新 -->
    <insert id="add" parameterType="${entityPackagePath}.${entityClassName}">
        insert into <include refid="tableName"/> (<include refid="baseColumns"/>)
        values (<include refid="insertValues"/>)
    </insert>

    <insert id="addBatch" parameterType="${entityPackagePath}.${entityClassName}">
        insert into <include refid="tableName"/> (<include refid="baseColumns"/>)
        values
        <foreach collection="list" item="item" separator=",">
            (<include refid="insertValues"/>)
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
            ${prop.col}=${wellNumberPre}item.${prop.prop}${wellNumberEnd}<#if (num!=size)>,</#if>
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
        <include refid="tableName"/>
        from <include refid="baseColumns"/>
        where id=${wellNumberPre}id${wellNumberEnd}
    </select>

    <select id="findAll" resultMap="baseResultMap">
        select
        <include refid="tableName"/>
        from <include refid="baseColumns"/>
    </select>

    <select id="findByIds" resultMap="baseResultMap">
        select
        <include refid="tableName"/>
        from <include refid="baseColumns"/>
        where id in
        <foreach collection="list" item="item" open="(" separator="," close=")">
            ${wellNumberPre}item${wellNumberEnd}
        </foreach>
    </select>

    <!-- 以下需要自行改动, 不会自动更新 -->
    <update id="updateProps">
        update <include refid="tableName"/>
        <set>
            <if test="todo!=null">
                ${wellNumberPre}todo${wellNumberEnd}
            </if>
        </set>
        where id=${wellNumberPre}id${wellNumberEnd}
    </update>

    <select id="query" resultMap="baseResultMap">
        select
        <include refid="tableName"/>
        from <include refid="baseColumns"/>
        <where>
            <if test="todo!=null and todo!=''">
                todo=${wellNumberPre}todo${wellNumberEnd}
            </if>
        </where>
        limit ${wellNumberPre}startNo${wellNumberEnd}, ${wellNumberPre}endNo${wellNumberEnd}
    </select>

    <select id="count" resultType="int">
        select count(*) from <include refid="tableName"/>
        <where>
            <if test="todo!=null and todo!=''">
                todo=${wellNumberPre}todo${wellNumberEnd}
            </if>
        </where>
    </select>

</mapper>