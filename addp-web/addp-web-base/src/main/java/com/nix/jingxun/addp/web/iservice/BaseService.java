package com.nix.jingxun.addp.web.iservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

/**
 * @author 11723
 */
public interface BaseService<M extends Object,ID extends Serializable>{

    /**
     * 保存对象M
     * @param m
     * */
    M save(M m)  throws Exception;
    /**
     * 删除对象M
     * @param id
     * */
    void delete(ID id);

    /**
     * 批量删除对象M
     * @param ids
     * */
    void delete(ID[] ids);

    /**
     * 更新对象M
     * @param m
     * */
    M update(M m)  throws Exception;
    /**
     * 查找唯一对象M
     * @param id id值
     * @return 唯一对象
     * */
    M findById(ID id);

    /**
     * 查找部对象
     * @return 全部对象
     * */
    List<M> findAll();

    /**
     * 基础分页查找
     * */
    Page<M> page(Pageable pageable);

}
