package com.happy.base;

import com.happy.dto.PagingDto;
import com.happy.exception.BusinessException;
import com.happy.express.sql.obverse.SqlParser;
import com.happy.util.CollectionUtil;
import com.happy.util.StringUtil;
import org.hibernate.query.NativeQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description: jpa基类
 * @author: llw
 * @date: 2018-11-17
 */
public abstract class BaseJpaDao<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * 泛型T的class模板
     */
    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseJpaDao() {
        //获取泛型T的class模板
        Type genericType = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
        entityClass = (Class<T>) types[0];
    }

    /**
     * 获取去除掉所有包名的类名
     * @param clazz 类模板
     * @return
     */
    private String getClassNameExcludePackage(Class<?> clazz) throws Exception {
        String wholeName = clazz.getName();
        String[] fragments = wholeName.split("\\.");

        return fragments[fragments.length - 1];
    }

    /**
     * 根据id获取实体
     * @param id id
     * @return 实体
     * @throws Exception
     */
    protected T findById(int id) throws Exception {
        if (id < 1) throw new Exception("id必须大于0");

        return entityManager.find(entityClass, id);
    }

    /**
     * 根据id集合获取实体集合
     * @param ids id集合
     * @return 实体集合
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByIds(Collection<Integer> ids) throws Exception {
        if (ids == null || ids.isEmpty()) throw new Exception("id集合不能为空");

        StringBuilder jpql = new StringBuilder("from ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" where id in (:ids)");

        return entityManager.createQuery(jpql.toString())
                .setParameter("ids", ids)
                .getResultList();
    }

    /**
     * 查询
     * @param jpql   jpql
     * @param values 传入jpql语句的参数(不要放置集合)
     * @return 返回结果集
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> find(String jpql, Object... values) throws Exception {
        if (jpql == null || "".equals(jpql)) throw new Exception("jpql不能为空");
        if (values == null) throw new Exception("参数可以不填写，但是不能为空");

        Query query = entityManager.createQuery(jpql);
        for (int i = 0; i < values.length; ++i) {
            query.setParameter(i + 1, values[i]);
        }

        return query.getResultList();
    }

    /**
     * 查询(只用写where之后的jpql)
     * @param afterWhereJpql 查询条件
     * @param values         传入jpql语句的参数(不要放置集合)
     * @return 返回结果集
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> findQuick(String afterWhereJpql, Object... values) throws Exception {
        if (afterWhereJpql == null) throw new Exception("afterWherejpql可以为空字符串，但是不能为空");
        if (values == null) throw new Exception("参数可以不填写，但是不能为空");

        StringBuilder jpql = new StringBuilder("select t from ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" t");
        jpql.append(" where 1=1");
        jpql.append(afterWhereJpql);

        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < values.length; ++i) {
            query.setParameter(i + 1, values[i]);
        }

        return query.getResultList();
    }

    /**
     * 查询(只用写where之后的jpql, 按id降序)
     * @param afterWhereJpql 查询条件
     * @param values         传入jpql语句的参数(不要放置集合)
     * @return 返回结果集
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> findQuickIdDesc(String afterWhereJpql, Object... values) throws Exception {
        if (afterWhereJpql == null) throw new Exception("afterWherejpql可以为空字符串，但是不能为空");
        if (values == null) throw new Exception("参数可以不填写，但是不能为空");

        StringBuilder jpql = new StringBuilder("select t from ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" t");
        jpql.append(" where 1=1");
        jpql.append(afterWhereJpql);
        jpql.append(" order by id desc");

        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < values.length; ++i) {
            query.setParameter(i + 1, values[i]);
        }

        return query.getResultList();
    }

    /**
     * 分页查询(只用写where之后的jpql)
     * @param afterWhereJpql 查询条件
     * @param pageNo         当前页码
     * @param pageSize       每页结果集数量
     * @param values         传入jpql语句的参数(不要放置集合)
     * @return 分页数据传输对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected PagingDto<T> pagingQuick(String afterWhereJpql, int pageNo, int pageSize, Object... values) throws Exception {
        if (afterWhereJpql == null) throw new Exception("jpql可以是空字符串，但是不能为空");
        if (pageNo < 1) throw new Exception("当前页码必须大于0");
        if (pageSize < 1) throw new Exception("每页的结果集数量必须大于0");
        if (values == null) throw new Exception("参数可以不写，但不能为空");

        StringBuilder jpql = new StringBuilder("select t from ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" t where 1=1");
        jpql.append(afterWhereJpql);

        //结果集
        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < values.length; ++i) {
            query.setParameter(i + 1, values[i]);
        }
        List<T> result = query
                .setFirstResult(--pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        //总数
        StringBuilder countJpql = new StringBuilder("select count(*) from ");
        countJpql.append(getClassNameExcludePackage(entityClass));
        countJpql.append(" t where 1=1");
        countJpql.append(afterWhereJpql);
        query = entityManager.createQuery(countJpql.toString());
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i + 1, values[i]);
        }
        Long count = (Long) query.getResultList().get(0);

        return new PagingDto<>(result, count);
    }

    /**
     * 分页查询(只用写where之后的jpql, 默认按id倒叙)
     * @param afterWhereJpql 查询条件
     * @param pageNo         当前页码
     * @param pageSize       每页结果集数量
     * @param values         传入jpql语句的参数(不要放置集合)
     * @return 分页数据传输对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected PagingDto<T> pagingQuickIdDesc(String afterWhereJpql, int pageNo, int pageSize, Object... values) throws Exception {
        if (afterWhereJpql == null) throw new Exception("jpql可以是空字符串，但是不能为空");
        if (pageNo < 1) throw new Exception("当前页码必须大于0");
        if (pageSize < 1) throw new Exception("每页的结果集数量必须大于0");
        if (values == null) throw new Exception("参数可以不写，但不能为空");

        StringBuilder jpql = new StringBuilder("select t from ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" t where 1=1");
        jpql.append(afterWhereJpql);
        jpql.append(" order by id desc");

        //结果集
        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < values.length; ++i) {
            query.setParameter(i + 1, values[i]);
        }
        List<T> result = query
                .setFirstResult(--pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        //总数
        StringBuilder countJpql = new StringBuilder("select count(*) from ");
        countJpql.append(getClassNameExcludePackage(entityClass));
        countJpql.append(" t where 1=1");
        countJpql.append(afterWhereJpql);
        query = entityManager.createQuery(countJpql.toString());
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i + 1, values[i]);
        }
        Long count = (Long) query.getResultList().get(0);

        return new PagingDto<>(result, count);
    }

    /**
     * 持久化实体
     * @param entity 实体对象
     * @throws Exception
     */
    protected void save(T entity) throws Exception {
        if (entity == null) throw new Exception("实体不能为空");

        entityManager.persist(entity);
    }

    /**
     * 批量持久化实体
     * @param entities 实体对象集合
     * @throws Exception
     */
    protected void saveBatch(Collection<T> entities) throws Exception {
        if (entities == null || entities.isEmpty()) throw new Exception("实体集合不能为空");

        for (int i = 0; i < entities.size(); ++i) {
            entityManager.persist(entities.toArray()[i]);
            if (i % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    /**
     * 删除实体
     * @param entity 实体
     * @throws Exception
     */
    protected void delete(T entity) throws Exception {
        if (entity == null) throw new Exception("实体不能为空");

        entityManager.remove(entity);
    }

    /**
     * 根据id删除实体
     * @param id id
     * @throws Exception
     */
    protected void deleteById(int id) throws Exception {
        if (id < 1) throw new Exception("id必须大于0");

        StringBuilder jpql = new StringBuilder("delete ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" entity where entity.id=:id");

        entityManager.createQuery(jpql.toString())
                .setParameter("id", id)
                .executeUpdate();
    }

    /**
     * 根据id集合批量删除实体
     * @param ids id集合
     * @throws Exception
     */
    protected void deleteByIds(Collection<Integer> ids) throws Exception {
        if (ids == null || ids.isEmpty()) throw new Exception("id集合不能为空");


        StringBuilder jpql = new StringBuilder("delete ");
        jpql.append(getClassNameExcludePackage(entityClass));
        jpql.append(" entity where entity.id in(:ids)");

        entityManager.createQuery(jpql.toString())
                .setParameter("ids", ids)
                .executeUpdate();
    }

    /**
     * 更新实体
     * @param entity 实体
     * @throws Exception
     */
    protected void update(T entity) throws Exception {
        if (entity == null) throw new Exception("实体不能为空");

        entityManager.merge(entity);
    }

    /**
     * 批量更新实体
     * @param entities 实体
     * @throws Exception
     */
    protected void updateBatch(Collection<T> entities) throws Exception {
        if (entities == null || entities.isEmpty()) throw new Exception("实体集合不能为空");

        for (int i = 0; i < entities.size(); ++i) {
            entityManager.merge(entities.toArray()[i]);
            if (i % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    /**
     * 执行jpql的数据查询语言
     * @param dql      数据查询语言
     * @param dtoClass 数据传输对象的类模板对象
     * @param values   传入jpql语句的参数(不要放置集合)
     * @return 执行结果集合
     * @throws Exception
     */
    protected <T> List<T> execDql(String dql, Class<T> dtoClass, Object... values) throws Exception {
        if (dql == null || "".equals(dql)) throw new Exception("不能没有dql");
        if (dtoClass == null) throw new Exception("dto的类模板对象不能为空");
        if (values == null) throw new Exception("参数可以不写，但不能为空");

        Query query = entityManager.createQuery(dql, dtoClass);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i + 1, values[i]);
        }

        return query.getResultList();
    }

    /**
     * 执行jpql的数据操作语言
     * @param dml    数据操作语言
     * @param values 传入jpql语句的参数(不要放置集合)
     * @throws Exception
     */
    protected void execDml(String dml, Object... values) throws Exception {
        if (dml == null || "".equals(dml)) throw new Exception("不能没有ddl");
        if (values == null) throw new Exception("参数可以不写，但不能为空");

        Query query = entityManager.createQuery(dml);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i + 1, values[i]);
        }
        query.executeUpdate();
    }

    /**
     * 执行对象原生sql
     * @param expressSql 对象原生sql
     * @param resultClass 结果class
     * @param params 列表参数
     * @param <T> 结果class
     * @return 结果集合
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    protected <T> List<T> express(String expressSql, Class<T> resultClass, Object... params) throws Exception {
        if (StringUtil.isEmpty(expressSql)) throw new BusinessException("执行sql不能为空");
        if (resultClass == null) throw new BusinessException("不能没有返回结果的类模板");
        if (params == null) throw new Exception("参数可以不写，但不能为空");

        //添加sql
        Query query = entityManager.createNativeQuery(SqlParser.parse(expressSql));

        //添加参数
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }

        //添加查询结果的类模板
        query.unwrap(NativeQuery.class)
                .addEntity(resultClass);

        return (List<T>) query.getResultList();
    }

    /**
     * 执行对象原生sql
     * @param expressSql 对象原生sql
     * @param resultClass 结果class
     * @param params map参数
     * @param <T> 结果class
     * @return 结果集合
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    protected <T> List<T> express(String expressSql, Class<T> resultClass, Map<String, Object> params) throws Exception {
        if (StringUtil.isEmpty(expressSql)) throw new BusinessException("执行sql不能为空");
        if (resultClass == null) throw new BusinessException("不能没有返回结果的类模板");

        //添加sql
        Query query = entityManager.createNativeQuery(SqlParser.parse(expressSql));

        //添加参数
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }

        //添加查询结果的类模板
        query.unwrap(NativeQuery.class)
                .addEntity(resultClass);

        return (List<T>) query.getResultList();
    }

}
