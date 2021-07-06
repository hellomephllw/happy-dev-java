package ${packagePath};

import ${entitySourceCodePath};
import com.happy.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description:
* @author: ${author}
* @date: ${date}
*/
@Repository
public interface ${entityClassName}Dao extends BaseMapper<${entityClassName}> {

}