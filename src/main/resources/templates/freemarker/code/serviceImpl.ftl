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
public class I${entityClassName}ServiceImpl extends BaseService implements I${entityClassName}Service {

    @Autowired
    private I${entityClassName}Dao ${entityInstanceName}Dao;

}