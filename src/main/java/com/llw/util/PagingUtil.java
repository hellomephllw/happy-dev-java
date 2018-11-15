package com.llw.util;

import com.llw.dto.PagingDto;
import com.llw.dto.vo.PagingVo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * @discription: 分页工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class PagingUtil {

    /**
     * 把分页数据传输对象转换成分页数据对象
     * @param dto 数据传输对象
     * @param voClazz 值对象class
     * @param pageNo 当前页码
     * @param pageSize 每页数量
     * @return 分页数据对象
     */
    @SuppressWarnings("unchecked")
    public static PagingVo transformDtoToVo(PagingDto dto, Class voClazz, int pageNo, int pageSize) throws Exception {
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
