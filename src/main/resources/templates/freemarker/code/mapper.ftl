<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoPackagePath}.I${entityClassName}Dao">
    <resultMap id="BaseResultMap" type="${entityPackagePath}.${entityClassName}">
        <id column="id" property="id"/>
        <#list props as prop>
        <result column="${prop.col}" property="${prop.prop}"/>
        </#list>
    </resultMap>
    <sql id="BaseColumnList">
        ${entityCols}
    </sql>

    <insert id="add" parameterType="${entityPackagePath}.${entityClassName}">
        insert into ${tableName} (${insertCols})
        values (${insertValues})
    </insert>

    <insert id="addBatch" parameterType="${entityPackagePath}.${entityClassName}">
        insert into ${tableName} (${insertCols})
        values
        <foreach collection="list" item="item" separator=",">
            (
            ${batchInsertValues}
            )
        </foreach>
    </insert>

    <delete id="remove">
        delete from ${tableName} where id=${wellNumberPre}id${wellNumberEnd}
    </delete>

    <delete id="removeByIds" parameterType="java.util.List">
        delete from ${tableName} where id in
        <foreach collection="list" item="item" open="(" separator="," close=")">
            ${wellNumberPre}item${wellNumberEnd}
        </foreach>
    </delete>

    <update id="updateProps">
        update ${tableName}
        set todo=${wellNumberPre}todo${wellNumberEnd}
        where id=${wellNumberPre}id${wellNumberEnd}
    </update>

    <update id="update" parameterType="${entityPackagePath}.${entityClassName}">
        update ${tableName}
        set
        <#assign num=0>
        <#list propsWithoutId as prop>
            <#assign num=num+1>
            <#if (num>1)>,<#else> </#if>${prop.col}=${wellNumberPre}${prop.prop}${wellNumberEnd}
        </#list>
        where id=${wellNumberPre}id${wellNumberEnd}
    </update>

    <update id="updateBatch" parameterType="java.util.List">
        <foreach collection="list" item="item" separator=";">
            update ${tableName}
            set
            <#assign num=0>
            <#list propsWithoutId as prop>
                <#assign num=num+1>
                <#if (num>1)>,<#else> </#if>${prop.col}=${wellNumberPre}item.${prop.prop}${wellNumberEnd}
            </#list>
            where id=${wellNumberPre}item.id${wellNumberEnd}
        </foreach>
    </update>

    <select id="get" resultMap="BaseResultMap">
        select
        <include refid="BaseColumnList"/>
        from ${tableName}
        where id=${wellNumberPre}id${wellNumberEnd}
    </select>

    <select id="findAll" resultMap="BaseResultMap">
        select
        <include refid="BaseColumnList"/>
        from ${tableName}
    </select>

    <select id="findByIds" resultMap="BaseResultMap">
        select
        <include refid="BaseColumnList"/>
        from ${tableName}
        where id in
        <foreach collection="list" item="item" open="(" separator="," close=")">
            ${wellNumberPre}item${wellNumberEnd}
        </foreach>
    </select>

    <select id="query" resultMap="BaseResultMap">
        select
        <include refid="BaseColumnList"/>
        from ${tableName}
        <where>
            <if test="todo!=null and todo!=''">
                todo=${wellNumberPre}todo${wellNumberEnd}
            </if>
        </where>
        limit ${wellNumberPre}startNo${wellNumberEnd}, ${wellNumberPre}endNo${wellNumberEnd}
    </select>

    <select id="count" resultType="int">
        select count(*) from ${tableName}
        <where>
            <if test="todo!=null and todo!=''">
                todo=${wellNumberPre}todo${wellNumberEnd}
            </if>
        </where>
    </select>

</mapper>