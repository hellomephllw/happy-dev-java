package com.llw.util;

import com.google.common.collect.ImmutableMap;
import com.llw.dto.PagingDto;
import com.llw.dto.vo.PagingVo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

/**
 * @description: 分页工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class PagingUtil {

    /**
     * 构建jpa的PageRequest对象, 默认使用id倒序
     * @param pageNo 当前页码
     * @param pageSize 每页数据量
     * @return PageRequest对象
     * @throws Exception
     */
    public static PageRequest buildJpaPageRequest(int pageNo, int pageSize) throws Exception {
        if (pageNo < 1) throw new Exception("pageNo必须大于0");
        if (pageSize < 1) throw new Exception("pageSize必须大于0");

        return PageRequest.of(--pageNo, pageSize, new Sort(Sort.Direction.DESC, "id"));
    }

    /**
     * 构建jpa的PageRequest对象, 使用指定排序方式
     * @param pageNo 当前页码
     * @param pageSize 每页数据量
     * @param sorts 指定的多个排序方式, eg: CollectionUtil.stringMap().put("name", "desc")
     * @return PageRequest对象
     * @throws Exception
     */
    public static PageRequest buildJpaPageRequest(int pageNo, int pageSize, ImmutableMap.Builder<String, String> ...sorts) throws Exception {
        if (sorts == null) throw new Exception("sorts参数可以不写，但不能为空");

        List<Sort.Order> orders = new ArrayList<>();
        for (ImmutableMap.Builder<String, String> sort : sorts) {
            Map.Entry<String, String> entry = ((Map<String, String>) sort.build()).entrySet().iterator().next();
            orders.add(new Sort.Order(parseStringToDirection(entry.getValue()), entry.getKey()));
        }

        return PageRequest.of(--pageNo, pageSize, Sort.by(orders));
    }

    /**
     * 把desc和asc字符串解析为direction
     * @param direction 待解析字符串
     * @return direction
     * @throws Exception
     */
    private static Sort.Direction parseStringToDirection(String direction) throws Exception {
        if (direction != null) {
            if (direction.toLowerCase().equals("desc")) return Sort.Direction.DESC;
            if (direction.toLowerCase().equals("asc")) return Sort.Direction.ASC;
        }
        throw new Exception("direction必须是desc或asc字符串");
    }

    /**
     * 把分页数据传输对象转换成分页数据对象
     * @param dto 数据传输对象
     * @param voClazz 值对象class
     * @param pageNo 当前页码
     * @param pageSize 每页数量
     * @return 分页数据对象
     */
    @SuppressWarnings("unchecked")
    public static PagingVo transformPagingDtoToVo(PagingDto dto, Class voClazz, int pageNo, int pageSize) throws Exception {
        PagingVo pagingVo = new PagingVo();
        pagingVo.setEntities(new TreeSet(transformEntities(dto.getEntities(), voClazz)));
        pagingVo.setPageNo(pageNo);
        pagingVo.setPageSize(pageSize);
        pagingVo.setPageAmount(dto.getCount() % pageSize == 0 ? (int) (dto.getCount() / pageSize) : (int) (dto.getCount() / pageSize) + 1);

        return pagingVo;
    }

    /**
     * 转换实体
     * @param entities 被转换实体
     * @param voClazz 需要转换的实体class
     * @return 新的实体
     * @throws Exception
     */
    private static List transformEntities(List entities, Class voClazz) throws Exception {
        List news = new ArrayList();
        for (Object object : entities) {
            news.add(ObjectUtil.transferObjectValToAnother(object, voClazz));
        }

        return news;
    }

    /**
     * 算出分页用的offset
     * @param pageNo 当前页码
     * @param pageSize 每页数量
     * @return offset
     */
    public static int calculateSqlOffset(int pageNo, int pageSize) {

        return (pageNo - 1) * pageSize;
    }

}
