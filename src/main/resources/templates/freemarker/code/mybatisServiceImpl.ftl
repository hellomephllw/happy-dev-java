package ${packagePath};

import ${daoClassPackagePath};
import ${serviceClassPackagePath};
import com.happy.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @description:
* @author: ${author}
* @date: ${date}
*/
@Transactional
@Service
public class ${entityClassName}ServiceImpl extends BaseService implements ${entityClassName}Service {

    @Autowired
    private ${entityClassName}Dao ${entityInstanceName}Dao;

}