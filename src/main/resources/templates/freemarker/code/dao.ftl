package ${packagePath};

import ${entitySourceCodePath};
import com.happy.dto.PagingDto;

import java.util.List;

/**
* @description:
* @author: ${author}
* @date: ${date}
*/
public interface ${entityClassName}Dao {

    void add(${entityClassName} ${entityInstanceName});

    void addBatch(List<${entityClassName}> ${entityInstanceName}List);

    void remove(int id) ;

    void removeByIds(List<Integer> ids);

    void update(int id);

    void update(${entityClassName} ${entityInstanceName});

    void update(List<${entityClassName}> ${entityInstanceName}List);

    ${entityClassName} get(int id);

    PagingDto<${entityClassName}> query(int pageNo, int pageSize);

    List<${entityClassName}> findAll();

    List<${entityClassName}> findByIds(List<Integer> ids);

}