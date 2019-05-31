package com.nix.jingxun.addp.web.service;

import cn.hutool.crypto.digest.MD5;
import com.nix.jingxun.addp.web.common.util.MD5Util;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.jpa.MemberJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2018/12/04 下午5:59
 */
@Service
public class MemberServiceImpl extends BaseServiceImpl<MemberModel,Long> implements IMemberService {
    @Resource
    private MemberJpa memberJpa;
    @Resource
    private IMemberService memberService;
    @Override
    protected JpaRepository<MemberModel, Long> jpa() {
        return memberJpa;
    }

    @Override
    public MemberModel register(MemberModel member) {
        if (memberJpa.exists(Example.of(MemberModel.builder().username(member.getUsername()).build()))) {
            return member;
        }
        try {
            return memberService.save(member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MemberModel login(String username, String password) {
        return jpa().findOne(Example.of(
                MemberModel.builder()
                        .username(username)
                        .password(password)
                        .build()
        )).map(m -> {
            m.setPassword(null);
            return m;
        }).orElse(null);
    }
}
