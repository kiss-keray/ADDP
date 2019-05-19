package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ServicesJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.List;

/**
 * @author keray
 * @date 2019/04/21 13:55
 */
@Service
public class ServicesServiceImpl extends BaseServiceImpl<ServicesModel, Long> implements IServicesService {

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
        return ShellExe.connect(servicesModel.getIp(), servicesModel.getUsername(), AESUtil.decrypt(servicesModel.getPassword()));
    }

    public ShellExe gitAuth(ShellExe shellExe, ProjectsModel projectsModel) {
        //输入账号
        return shellExe.syncExecute(projectsModel.getGitUsername(),
                ShellExeLog.success,
                (error, cmd) -> ShellExeLog.fail.accept(error, "输入账号异常"))
                //输入密码
                .syncExecute(AESUtil.decrypt(projectsModel.getGitPassword()),
                        result1 -> {
                            // 判断shell返回的认证信息；
                            if (result1.toString().contains("Authentication failed")) {
                                ShellExeLog.fail.accept(new AuthException(StrUtil.format("fatal: Authentication failed for '{}'", projectsModel.getGitUrl())),
                                        "git密码验证失败");
                            }
                        },
                        error -> ShellExeLog.fail.accept(error, "密码输入执行异常"));
    }
}
