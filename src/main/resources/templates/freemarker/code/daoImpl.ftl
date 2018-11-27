package ${packagePath};

import com.llw.base.BaseJpaDao;
import ${daoClassPackagePath};
import ${entitySourceCodePath};
import com.llw.dto.PagingDto;
import com.llw.util.StringSql;
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
    public void add(${entityClassName} ${entityInstanceName}) throws Exception {
        super.save(${entityInstanceName});
    }

    @Override
    public void addBatch(List<${entityClassName}> ${entityInstanceName}List) throws Exception {
        super.saveBatch(${entityInstanceName}List);
    }

    @Override
    public void remove(long id) throws Exception {
        super.deleteById(id);
    }

    @Override
    public void removeByIds(List<Long> ids) throws Exception {
        super.deleteByIds(ids);
    }

    @Override
    public void update(long id) throws Exception {
        super.execDml("update User set todo=?1 where id=?2", Arrays.asList(null, id).toArray());
    }

    @Override
    public void update(${entityClassName} ${entityInstanceName}) throws Exception {
        super.update(${entityInstanceName});
    }

    @Override
    public void update(List<${entityClassName}> ${entityInstanceName}List) throws Exception {
        super.updateBatch(${entityInstanceName}List);
    }

    @Override
    public ${entityClassName} get(long id) throws Exception {
        return super.findById(id);
    }

    @Override
    public PagingDto<${entityClassName}> query(int pageNo, int pageSize) throws Exception {
        StringSql jpql = new StringSql();
        List<Object> params = new ArrayList<>();

        if (pageNo == -1) {
            jpql.add(" and todo=?");
            params.add("");
        }

        return super.pagingQuickIdDesc(jpql.toString(), pageNo, pageSize, params.toArray());
    }

    @Override
    public List<${entityClassName}> findAll() throws Exception {
        return super.findQuickIdDesc("");
    }

    @Override
    public List<${entityClassName}> findByIds(List<Long> ids) throws Exception {
        return super.findByIds(ids);
    }

}
