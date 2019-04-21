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
    private final static Map<String,JpaRepository> JPA_MAPPING = new HashMap<>();
    protected abstract JpaRepository<M,ID> jpa();
    protected abstract Class<M> modelType();

    @PostConstruct
    private void init() {
        JPA_MAPPING.put(modelType().getName(),jpa());
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

    @Override
    public <T> T oneToOneModel(Class<T> clazz, Long id) {
        return (T) JPA_MAPPING.get(clazz.getName()).findById(id);
    }

    @Override
    public <T> List<T> oneToMany(Class<T> clazz, Long foreignKey,String foreignKeyName) {
        try {
            T model = clazz.newInstance();
            Method method = clazz.getMethod("set" + foreignKeyName.toUpperCase().substring(0,1) + foreignKeyName.substring(1),Long.class);
            method.invoke(model,foreignKey);
            return JPA_MAPPING.get(clazz.getName()).findAll(Example.of(model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
