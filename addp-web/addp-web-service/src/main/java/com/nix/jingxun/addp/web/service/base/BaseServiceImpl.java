package com.nix.jingxun.addp.web.service.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.nix.jingxun.addp.web.iservice.BaseService;
import com.nix.jingxun.addp.web.model.BaseModel;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 11723
 */
public abstract class BaseServiceImpl<M extends BaseModel,ID extends Serializable> implements BaseService<M,ID> {
    protected abstract JpaRepository<M,ID> jpa();

    @PostConstruct
    private void init() {

    }
    @Override
    public M update(M o)  throws Exception{
        M m = jpa().findById((ID) o.getId()).orElse(null);
        Assert.notNull(m);
        ignoreNull(m,o);
        BeanUtil.copyProperties(m,o);
        return jpa().saveAndFlush(m);
    }

    @Override
    public M findById(ID id) {
        return jpa().findById(id).orElse(null);
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
    public Page<M> page(Pageable pageable) {
        return jpa().findAll(pageable);
    }

    @Override
    public Page<M> page(Pageable pageable, Example<M> example) {
        return jpa().findAll(example,pageable);
    }

    @Override
    public void delete(ID[] ids) {
        for (ID id:ids) {
            delete(id);
        }
    }

    private void ignoreNull(M source,M target) throws Exception{
        Class<M> clazz = (Class<M>) source.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method get:methods) {
            try {
                if (!get.getName().equals("getClass") && get.getName().startsWith("get") && get.getParameterCount() == 0) {
                    Object value = get.invoke(target);
                    if (value != null) {
                        Method set = clazz.getMethod("set" + get.getName().substring(3),get.getReturnType());
                        set.invoke(source,value);
                    }
                }
            }catch (Exception ignore) {
            }
        }
    }
}
