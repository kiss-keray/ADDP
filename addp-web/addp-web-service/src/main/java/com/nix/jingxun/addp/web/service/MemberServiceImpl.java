package com.nix.jingxun.addp.web.service;

import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.jpa.MemberJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2018/12/04 下午5:59
 */
@Service
public class MemberServiceImpl extends BaseServiceImpl<MemberModel,Integer> implements IMemberService {
    @Resource
    private MemberJpa memberJpa;
    @Override
    protected JpaRepository<MemberModel, Integer> jpa() {
        return memberJpa;
    }
}
