package com.happy.express.paging.plugin;

import com.happy.dto.PagingDto;
import com.happy.util.RegexUtil;
import com.happy.util.StringUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.*;

/**
 * @description: 分页插件
 * @author: liliwen
 * @date: 2021-07-06
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class EasyPagingPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler<?> resultHandler = (ResultHandler<?>) args[3];

        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String originSql = boundSql.getSql();
        String mapperId = mappedStatement.getId();
        if (isPagingMapper(mapperId)) {//处理以paging结尾的mapper
            //执行分页查询
            List<?> list = executor.query(mappedStatement, parameterObject, rowBounds, resultHandler);

            //为count查询准备参数
            MappedStatement countMappedStatement = buildCountMappedStatement(mappedStatement);
            BoundSql countBoundSql = buildCountBoundSql(originSql, mappedStatement, boundSql, parameterObject);
            CacheKey countCacheKey = executor.createCacheKey(countMappedStatement, parameterObject, RowBounds.DEFAULT, countBoundSql);

            //执行count查询
            List<Object> countQueryResult = executor.query(countMappedStatement, parameterObject, RowBounds.DEFAULT, resultHandler, countCacheKey, countBoundSql);
            Long count = (Long) countQueryResult.get(0);

            return new PagingDto<>(list, count);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    /**
     * 确定分页
     * @param mapperId mapperId
     * @return 是分页
     */
    private boolean isPagingMapper(String mapperId) {
        return !StringUtil.isEmpty(mapperId) && RegexUtil.match(".*((p|P)aging)$", mapperId);
    }

    /**
     * 构建count统计查询的mappedStatement
     * @param mappedStatement 原mappedStatement
     * @return count统计查询的mappedStatement
     */
    private MappedStatement buildCountMappedStatement(MappedStatement mappedStatement) {
        MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), mappedStatement.getId() + "_count", mappedStatement.getSqlSource(), mappedStatement.getSqlCommandType());
        ResultMap resultMap = new ResultMap.Builder(mappedStatement.getConfiguration(), mappedStatement.getId(), Long.class, new ArrayList<>(0)).build();
        builder.resource(mappedStatement.getResource())
                .fetchSize(mappedStatement.getFetchSize())
                .statementType(mappedStatement.getStatementType())
                .timeout(mappedStatement.getTimeout())
                .parameterMap(mappedStatement.getParameterMap())
                .resultSetType(mappedStatement.getResultSetType())
                .cache(mappedStatement.getCache())
                .flushCacheRequired(mappedStatement.isFlushCacheRequired())
                .useCache(mappedStatement.isUseCache())
                .resultMaps(Arrays.asList(resultMap));
        if (mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length > 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : mappedStatement.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        return builder.build();
    }

    /**
     * 构建count统计查询的boundSql
     * @param originSql 原sql
     * @param mappedStatement 原mappedStatement
     * @param boundSql 原boundSql
     * @param parameterObject 参数对象
     * @return count统计BoundSql
     */
    private BoundSql buildCountBoundSql(String originSql, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) {
        return new BoundSql(mappedStatement.getConfiguration(), buildCountSql(originSql), removeStartNoAndPageSize(boundSql.getParameterMappings()), parameterObject);
    }

    /**
     * 构建count统计查询sql
     * @param originSql 原sql
     * @return count统计sql
     */
    private String buildCountSql(String originSql) {
        if (StringUtil.isEmpty(originSql)) throw new RuntimeException("原始sql为空");
        originSql = originSql.toLowerCase().trim();
        if (originSql.endsWith(";")) originSql = originSql.substring(0, originSql.length() - 1).trim();
        boolean includeOrderBy = RegexUtil.match("[\\s\\S]+(\\s+order\\s+by\\s+[a-zA-Z0-9]+\\s+limit\\s+\\?\\s*,\\s*\\?)$", originSql);
        if (!includeOrderBy && !RegexUtil.match("[\\s\\S]+(\\s+limit\\s+\\?\\s*,\\s*\\?)$", originSql)) throw new RuntimeException("sql必须是以order by和limit结尾");

        //去掉最后的limit和order by
        String orderStr = "order";
        String limitStr = "limit";
        String keyWords = includeOrderBy ? orderStr : limitStr;
        int keyWordsAmount = includeOrderBy ? wordsAmount(originSql, orderStr) : wordsAmount(originSql, limitStr);
        String[] fragments = originSql.split("\\s+");
        int count = 0;
        List<String> noLimitFragments = new LinkedList<>();
        for (String fragment : fragments) {
            if (fragment.equals(keyWords) && ++count == keyWordsAmount) break;
            noLimitFragments.add(fragment);
        }

        //查询列替换为count(*), 利用select和from来配对, 挖空并替换第一对select from的中间部分
        String selectStr = "select";
        String fromStr = "from";
        List<String> countFragments = new LinkedList<>();
        boolean stopCollect = false;
        boolean beginCollect = false;
        int matchCount = 0;
        for (String fragment : noLimitFragments) {
            if (fromStr.equals(fragment) && --matchCount == 0) {
                stopCollect = false;
                beginCollect = true;
            }
            if (!stopCollect || beginCollect) countFragments.add(fragment);
            if ((selectStr.equals(fragment) || ("(" + selectStr).equals(fragment)) && ++matchCount == 1) {//包括select和(select
                countFragments.add("count(*)");
                stopCollect = true;
            }
        }

        return String.join(" ", countFragments);
    }

    /**
     * 统计单词
     * @param originSql sql
     * @param words 单词
     * @return 数量
     */
    private int wordsAmount(String originSql, String words) {
        String[] fragments = originSql.split("\\s+");
        int count = 0;
        for (String fragment : fragments) {
            if (fragment.equals(words)) ++count;
        }

        return count;
    }

    /**
     * 去掉startNo和pageSize参数
     * @param parameterMappings 参数映射
     * @return 去掉后的参数映射
     */
    private List<ParameterMapping> removeStartNoAndPageSize(List<ParameterMapping> parameterMappings) {
        List<ParameterMapping> newParameterMappings = new LinkedList<>();
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (!parameterMapping.getProperty().equals("startNo") && !parameterMapping.getProperty().equals("pageSize")) {
                newParameterMappings.add(parameterMapping);
            }
        }

        return newParameterMappings;
    }

}
