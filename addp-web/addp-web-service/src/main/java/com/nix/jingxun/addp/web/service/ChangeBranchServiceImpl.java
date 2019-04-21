package com.nix.jingxun.addp.web.service;

import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.jpa.ChangeBranchJpa;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/04/21 13:58
 */
@Service
public class ChangeBranchServiceImpl extends BaseServiceImpl<ChangeBranchModel,Long> implements IChangeBranchService {
    @Resource
    private ChangeBranchJpa changeBranchJpa;

    @Override
    protected JpaRepository<ChangeBranchModel, Long> jpa() {
        return changeBranchJpa;
    }

    @Override
    protected Class<ChangeBranchModel> modelType() {
        return ChangeBranchModel.class;
    }
}
