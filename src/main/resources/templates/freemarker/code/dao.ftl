package ${packagePath};

import ${entitySourceCodePath};
import com.llw.dto.PagingDto;

import java.util.List;

/**
* @description:
* @author: ${author}
* @date: ${date}
*/
public interface I${entityClassName}Dao {

    public void add(${entityClassName} ${entityInstanceName}) throws Exception;

    public void addBatch(List<${entityClassName}> ${entityInstanceName}List) throws Exception;

    public void remove(int id) throws Exception;

    public void removeByIds(List<Integer> ids) throws Exception;

    public void update(int id) throws Exception;

    public void update(${entityClassName} ${entityInstanceName}) throws Exception;

    public void update(List<${entityClassName}> ${entityInstanceName}List) throws Exception;

    public ${entityClassName} get(int id) throws Exception;

    public PagingDto<${entityClassName}> query(int pageNo, int pageSize) throws Exception;

    public List<${entityClassName}> findAll() throws Exception;

    public List<${entityClassName}> findByIds(List<Integer> ids) throws Exception;

}