package com.nix.jingxun.addp.web.service.base;

import com.nix.jingxun.addp.web.iservice.BaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 11723
 */
public abstract class BaseServiceImpl<M extends Object,ID extends Serializable> implements BaseService<M,ID> {
    protected abstract JpaRepository<M,ID> jpa();

    @PostConstruct
    private void init() {

    }
    @Override
    public M update(M o)  throws Exception{
        return jpa().saveAndFlush(o);
    }

    @Override
    public M findById(ID id) {
        return jpa().getOne(id);
    }

    @Override
    public List<M> findAll() {
        return jpa().findAll();
    }

    @Override
    public M save(M m)  throws Exception{
        return jpa().save(m);
    }

    @Override
    public void delete(ID id) {
        jpa().deleteById(id);
    }

    @Override
    public void delete(ID[] ids) {
        for (ID id:ids) {
            delete(id);
        }
    }
}
