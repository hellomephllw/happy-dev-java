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

    public void addBatch(List<${entityClassName}> ${entityInstanceName}s) throws Exception;

    public void remove(long id) throws Exception;

    public void removeByIds(List<Long> ids) throws Exception;

    public void update(long id) throws Exception;

    public void update(${entityClassName} ${entityInstanceName}) throws Exception;

    public void update(List<${entityClassName}> ${entityInstanceName}s) throws Exception;

    public ${entityClassName} get(long id) throws Exception;

    public PagingDto<${entityClassName}> query(int pageNo, int pageSize) throws Exception;

    public List<${entityClassName}> findAll() throws Exception;

    public List<${entityClassName}> findByIds(List<Long> ids) throws Exception;

}