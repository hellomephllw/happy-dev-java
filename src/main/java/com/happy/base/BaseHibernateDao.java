package com.happy.base;

import com.happy.dto.PagingDto;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @description: Dao实现基类，所有dao实现类都最好继承
 * @author: llw
 * @date: 2016-11-17
 */
public abstract class BaseHibernateDao<T> {

    /** session工厂 */
    @Autowired
    protected SessionFactory sessionFactory;
    /** 泛型T的class模板 */
    private Class<T> entityClass;

    /** 构造方法 */
    @SuppressWarnings("unchecked")
    public BaseHibernateDao() {
        //获取泛型T的class模板
        Type genericType = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
        entityClass = (Class<T>) types[0];
    }

    /** 获取当前线程的session */
    protected Session getSession() {

        return sessionFactory.getCurrentSession();
    }

    /**
     * 根据id查询lazy
     * @param id id
     * @return 查询对象
     * @throws Exception
     */
    protected T findByIdLazy(long id) throws Exception {
        if (id == 0) throw new Exception("必须填写id不为0的整数！");

        return getSession().load(entityClass, id);
    }

    /**
     * 根据id查询
     * @param id id
     * @return 查询对象
     * @throws Exception
     */
    protected T findById(long id) throws Exception {
        if (id == 0) throw new Exception("必须填写id不为0的整数！");

        return getSession().get(entityClass, id);
    }

    /**
     * 根据id集合查找所对应的对象
     * @param ids id集合
     * @return 对象集合
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByIds(List<Long> ids) throws Exception {
        if (ids == null || ids.isEmpty()) throw new Exception("必须填写ids！");

        StringBuilder hql = new StringBuilder("from ");
        hql.append(getClassNameExcludePackage(entityClass));
        hql.append(" where id in (:ids)");

        return (List<T>) getSession()
                .createQuery(hql.toString())
                .setParameterList("ids", ids)
                .list();
    }

    /**
     * 查询
     * @param hql hql
     * @param values 传入hql语句的参数(不要放置集合)
     * @return 返回结果集
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> find(String hql, Object ...values) throws Exception {
        if (hql == null || "".equals(hql)) throw new Exception("必须填写hql！");
        if (values == null) throw new Exception("参数可以不写，但不能为空！");

        Query query = getSession().createQuery(hql);
        for (int i = 0; i < values.length; ++i) {
            query.setParameter(i, values[i]);
        }

        return query.list();
    }

    /**
     * 分页查询
     * @param hql hql
     * @param offset 第一条记录索引位置
     * @param pageSize 每页需要显示的记录数
     * @param values 查询参数
     * @return 分页结果集
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected List<T> find(String hql, int offset, int pageSize, Object ...values) throws Exception {
        if (hql == null || "".equals(hql)) throw new Exception("必须填写hql！");
        if (offset < 0) throw new Exception("第一条记录索引位置不能为负数！");
        if (pageSize < 1) throw new Exception("每页需要显示的记录数必须大于0！");
        if (values == null) throw new Exception("参数可以不写，但不能为空！");
        //执行查询
        Query query = getSession().createQuery(hql);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        List<T> result = query.setFirstResult(offset)
                .setMaxResults(pageSize)
                .list();

        return result;
    }

    /**
     * 快捷分页查询
     * @param partsOfHql 部分hql，如:" and id = ? and name = ?"，也可以不要参数，如:""
     * @param offset 第一条记录索引位置
     * @param pageSize 每页需要显示的记录数
     * @param values 参数值
     * @return 分页持久层对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected PagingDto<T> pagingQuick(String partsOfHql, int offset, int pageSize, Object ...values) throws Exception {
        if (partsOfHql == null) throw new Exception("hql不能为null！");
        if (offset < 0) throw new Exception("第一条记录索引位置不能为负数！");
        if (pageSize < 1) throw new Exception("每页需要显示的记录数必须大于0！");
        if (values == null) throw new Exception("参数可以不写，但不能为空！");
        //hql公共部分
        StringBuilder commonHql = new StringBuilder("from ");
        commonHql.append(entityClass.getName());
        commonHql.append(" where 1=1");

        //条件查询分页后结果集
        StringBuilder entitiesHql = commonHql.append(partsOfHql);
        Query query = getSession().createQuery(entitiesHql.toString());
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        List<T> result = query.setFirstResult(offset)
                .setMaxResults(pageSize)
                .list();
        //条件查询总结果数
        StringBuilder countHql = new StringBuilder("select count(*) ");
        countHql.append(commonHql);
        query = getSession().createQuery(countHql.toString());
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        Long count = (Long) query.list().get(0);

        return new PagingDto<>(result, count);
    }

    /**
     * 删除实体
     * @param entity 删除的实体
     * @throws Exception
     */
    protected void delete(T entity) throws Exception {
        getSession().delete(entity);
    }

    /**
     * 根据id删除
     * @param id id
     * @throws Exception
     */
    protected void deleteById(long id) throws Exception {
        if (id == 0) throw new Exception("必须填写id不为0的整数！");

        StringBuilder hql = new StringBuilder("delete ");
        hql.append(getClassNameExcludePackage(entityClass));
        hql.append(" entity where entity.id = ?");

        Query query = getSession().createQuery(hql.toString());
        query.setParameter(0, id);
        query.executeUpdate();
    }

    /**
     * 根据id批量删除
     * @param ids id集合
     * @throws Exception
     */
    protected void deleteByIds(Collection<Long> ids) throws Exception {
        if (ids == null || ids.isEmpty()) throw new Exception("必须填写ids！");

        StringBuilder hql = new StringBuilder("delete ");
        hql.append(getClassNameExcludePackage(entityClass));
        hql.append(" entity where entity.id in(:ids)");

        Query query = getSession().createQuery(hql.toString());
        query.setParameterList("ids", ids);
        query.executeUpdate();
    }

    /**
     * 新增对象
     * @param entity 新增的实体
     * @throws Exception
     */
    protected void save(T entity) throws Exception {
        if (entity == null) throw new Exception("必须填写实体entity！");

        getSession().save(entity);
    }

    /**
     * 批量新增
     * @param entities 新增实体集合
     * @throws Exception
     */
    protected void saveBatch(List<T> entities) throws Exception {
        if (entities == null || entities.isEmpty()) throw new Exception("必须填写entities集合，并且集合中不能没有实体！");

        //忽略二级缓存
        getSession().setCacheMode(CacheMode.IGNORE);
        //批量新增
        for (int i = 0; i < entities.size(); ++i) {
            getSession().save(entities.get(i));
            if (i % 20 == 0) {
                getSession().flush();
                getSession().clear();
            }
        }

    }

    /**
     * 更新实体
     * @param entity 实体
     * @throws Exception
     */
    protected void update(T entity) throws Exception {
        getSession().update(entity);
    }

    /**
     * 批量更新
     * @param entities 更新实体集合
     * @throws Exception
     */
    protected void updateBatch(List<T> entities) throws Exception {
        if (entities == null || entities.isEmpty()) throw new Exception("必须填写entities集合，并且集合中不能没有实体！");

        //忽略二级缓存
        getSession().setCacheMode(CacheMode.IGNORE);
        //批量更新
        for (int i = 0; i < entities.size(); ++i) {
            getSession().update(entities.get(i));
            if (i % 20 == 0) {
                getSession().flush();
                getSession().clear();
            }
        }

    }

    /**
     * 获取去除掉所有包的类名
     * @param clazz 类模板
     * @return
     */
    private String getClassNameExcludePackage(Class<?> clazz) throws Exception {
        String wholeName = clazz.getName();
        String[] fragments = wholeName.split("\\.");

        return fragments[fragments.length - 1];
    }

}
