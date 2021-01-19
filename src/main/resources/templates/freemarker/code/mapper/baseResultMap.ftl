    <resultMap id="baseResultMap" type="${entityPackagePath}.${entityClassName}">
        <id column="id" property="id"/>
        <#list propsWithoutId as prop>
        <result column="${prop.col}" property="${prop.prop}"/>
        </#list>
    </resultMap>
