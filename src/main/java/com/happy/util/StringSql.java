package com.happy.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 用来拼接sql查询语句的工具类
 * @author: happy
 * @date: 2018-11-22
 */
public class StringSql {

    /**初始化字符串*/
    private String initString;
    /**sql字符串片段*/
    private List<String> strFragments = new ArrayList<>();

    public StringSql() {
        initString = "";
    }

    public StringSql(String initStr) {
        initString = initStr;
    }

    /**
     * 添加sql字符串片段
     * @param strFragment sql字符串
     * @return 返回自身
     */
    public StringSql add(String strFragment) {
        strFragments.add(strFragment);
        return this;
    }

    public String toString() {

        String sql = initString;

        for (int i = 0; i < strFragments.size(); ++i) {
            sql += strFragments.get(i) + (i + 1);
        }

        return sql;
    }

}
