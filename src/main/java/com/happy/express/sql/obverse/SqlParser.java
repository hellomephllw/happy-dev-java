package com.happy.express.sql.obverse;

import com.google.common.base.CaseFormat;
import com.happy.express.sql.ExpressSql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: sql解析器
 * @author: llw
 * @date: 2019-05-24
 */
public class SqlParser {

    /**
     * 解析
     * @param sql sql
     * @return 解析结果
     */
    public static String parse(String sql) {
        return resolveFields(resolveEntities(sql));
    }

    /**
     * 解析sql的所有字段(把驼峰换为_)
     * @param sql sql
     * @return 解析后的结果
     */
    private static String resolveFields(String sql) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sql).replaceAll("__", "_");
    }

    /**
     * 解析sql的所有实体(把实体名换位表名)
     * @param sql sql
     * @return 解析后的结果
     */
    private static String resolveEntities(String sql) {
        for (String fragment : sql.split("\\s+")) {
            fragment = fragment.trim();
            Pattern pattern = Pattern.compile("@[a-zA-Z0-9]+");
            Matcher matcher = pattern.matcher(fragment);
            if (matcher.find()) {
                String target = matcher.group();
                String tableName = ExpressSql.getTableName(target.substring(1));
                fragment = target;
                sql = sql.replaceAll(fragment, tableName);
            }
        }

        return sql;
    }

}
