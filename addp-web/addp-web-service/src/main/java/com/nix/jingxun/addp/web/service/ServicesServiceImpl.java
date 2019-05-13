package com.nix.jingxun.addp.web.service;

import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ServicesJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author keray
 * @date 2019/04/21 13:55
 */
@Service
public class ServicesServiceImpl extends BaseServiceImpl<ServicesModel,Long> implements IServicesService {

    @Resource
    private ServicesJpa servicesJpa;

    @Override
    protected JpaRepository<ServicesModel, Long> jpa() {
        return servicesJpa;
    }

    @Override
    protected Class<ServicesModel> modelType() {
        return ServicesModel.class;
    }

    @Override
    public List<ServicesModel> selectMemberServices(MemberModel memberModel) {
        return servicesJpa.findAll(Example.of(ServicesModel.builder().memberId(memberModel.getId()).build()));
    }

    public ShellExe shellExeByUsername(ServicesModel servicesModel) throws IOException, JSchException {
        // 拿到服务器执行shell
        return ShellExe.connect(servicesModel.getIp(), servicesModel.getUsername(), servicesModel.getPassword());
    }
}
