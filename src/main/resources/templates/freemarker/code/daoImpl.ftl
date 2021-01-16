package ${packagePath};

import com.happy.base.BaseJpaDao;
import ${daoClassPackagePath};
import ${entitySourceCodePath};
import com.happy.dto.PagingDto;
import com.happy.util.StringSql;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @description:
* @author: ${author}
* @date: ${date}
*/
@Repository
public class I${entityClassName}DaoImpl extends BaseJpaDao<${entityClassName}> implements I${entityClassName}Dao {

    @Override
    public void add(${entityClassName} ${entityInstanceName}) {
        super.save(${entityInstanceName});
    }

    @Override
    public void addBatch(List<${entityClassName}> ${entityInstanceName}List) {
        super.saveBatch(${entityInstanceName}List);
    }

    @Override
    public void remove(int id) {
        super.deleteById(id);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        super.deleteByIds(ids);
    }

    @Override
    public void update(int id) {
        super.execDml("update User set todo=?1 where id=?2", Arrays.asList(null, id).toArray());
    }

    @Override
    public void update(${entityClassName} ${entityInstanceName}) {
        super.update(${entityInstanceName});
    }

    @Override
    public void update(List<${entityClassName}> ${entityInstanceName}List) {
        super.updateBatch(${entityInstanceName}List);
    }

    @Override
    public ${entityClassName} get(int id) {
        return super.findById(id);
    }

    @Override
    public PagingDto<${entityClassName}> query(int pageNo, int pageSize) {
        StringSql jpql = new StringSql();
        List<Object> params = new ArrayList<>();

        if (pageNo == -1) {
            jpql.add(" and todo=?");
            params.add("");
        }

        return super.pagingQuickIdDesc(jpql.toString(), pageNo, pageSize, params.toArray());
    }

    @Override
    public List<${entityClassName}> findAll() {
        return super.findQuickIdDesc("");
    }

    @Override
    public List<${entityClassName}> findByIds(List<Integer> ids) {
        return super.findByIds(ids);
    }

}
