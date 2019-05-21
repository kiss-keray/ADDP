package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.common.supper.WebThreadPool;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.diamond.ADDPEnvironment;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ServicesJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServiceRe;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author keray
 * @date 2019/04/21 13:55
 */
@Service
@Slf4j
public class ServicesServiceImpl extends BaseServiceImpl<ServicesModel, Long> implements IServicesService {

    @Resource
    private ServicesJpa servicesJpa;

    @Override
    protected JpaRepository<ServicesModel, Long> jpa() {
        return servicesJpa;
    }

    @Override
    public List<ServicesModel> selectMemberServices(MemberModel memberModel) {
        return jpa().findAll(Example.of(ServicesModel.builder().memberId(memberModel.getId()).build()));
    }

    /**
     * 获取服务器的shell通道
     * 如果主机是备份环境主机，需要拿真实ip
     */
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

    public boolean moreServiceExec(List<ServicesModel> servicesModels, Consumer<ServicesModel> exec) {
        final CountDownLatch latch = new CountDownLatch(servicesModels.size());
        final AtomicInteger success = new AtomicInteger(0);
        for (ServicesModel model : servicesModels) {
            WebThreadPool.IO_THREAD.execute(() -> {
                try {
                    exec.accept(model);
                    success.getAndIncrement();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        if (latch.getCount() > 0) {
            log.error("服务器组执行超时,size={} other={}", servicesModels.size(), latch.getCount());
            return false;
        }
        if (success.get() < servicesModels.size()) {
            log.error("服务器组执行部分失败,size={} fail={}", servicesModels.size(), servicesModels.size() - success.get());
            return false;
        }
        return true;
    }

    @Override
    public List<ServicesModel> selectEnvServices(ProjectsModel projectsModel, ADDPEnvironment environment) {
        List<ServicesModel> servicesModels = servicesJpa.selectEnvServices(
                projectsModel._getProjectsServiceRes()
                        .stream()
                        .map(ProjectsServiceRe::getServicesId)
                        .collect(Collectors.toList())
                , environment
        );
        if (environment == ADDPEnvironment.pro) {
            servicesModels.addAll(servicesJpa.selectEnvServices(
                    projectsModel._getProjectsServiceRes()
                            .stream()
                            .map(ProjectsServiceRe::getServicesId)
                            .collect(Collectors.toList())
                    , ADDPEnvironment.bak
            ));
        }
        return servicesModels;
    }
}
