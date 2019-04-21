package com.nix.jingxun.addp.web.service.base;

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
    M add(M m)  throws Exception;
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

}
