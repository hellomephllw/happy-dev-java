package ${packagePath};

import ${entitySourceCodePath};
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description:
* @author: ${author}
* @date: ${date}
*/
@Repository
public interface I${entityClassName}Dao {

    public void add(${entityClassName} ${entityInstanceName}) throws Exception;

    public void addBatch(List<${entityClassName}> ${entityInstanceName}List) throws Exception;

    public void remove(int id) throws Exception;

    public void removeByIds(List<Integer> ids) throws Exception;

    public void updateProps(@Param("id")int id) throws Exception;

    public void update(${entityClassName} ${entityInstanceName}) throws Exception;

    public void updateBatch(List<${entityClassName}> ${entityInstanceName}List) throws Exception;

    public ${entityClassName} get(int id) throws Exception;

    public List<${entityClassName}> findAll() throws Exception;

    public List<${entityClassName}> findByIds(List<Integer> ids) throws Exception;

    public List<${entityClassName}> query(@Param("pageStart")int pageStart,
                                          @Param("pageEnd")int pageEnd) throws Exception;

    public int count(@Param("pageStart")int pageStart,
                     @Param("pageEnd")int pageEnd) throws Exception;

}