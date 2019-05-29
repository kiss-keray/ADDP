package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.common.supper.WebThreadPool;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.ServerJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServerRe;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class ServerServiceImpl extends BaseServiceImpl<ServerModel, Long> implements IServerService {

    @Resource
    private ServerJpa serverJpa;

    @Override
    protected JpaRepository<ServerModel, Long> jpa() {
        return serverJpa;
    }

    @Override
    public List<ServerModel> selectMemberServices(MemberModel memberModel) {
        return jpa().findAll(Example.of(ServerModel.builder().memberId(memberModel.getId()).build()));
    }
    /**
     * 获取服务器的shell通道
     * 如果主机是备份环境主机，需要拿真实ip
     */
    public ShellExe shellExeByUsername(ServerModel serverModel) throws IOException, JSchException {
        // 拿到服务器执行shell
        return ShellExe.connect(serverModel.getIp(), serverModel.getUsername(), AESUtil.decrypt(serverModel.getPassword()));
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

    public boolean moreServiceExec(List<ServerModel> serverModels, Consumer<ServerModel> exec) {
        final CountDownLatch latch = new CountDownLatch(serverModels.size());
        final AtomicInteger success = new AtomicInteger(0);
        for (ServerModel model : serverModels) {
            WebThreadPool.IO_THREAD.submit(() -> {
                try {
                    exec.accept(model);
                    success.getAndIncrement();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("server {} 执行失败",model.getId());
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
            log.error("服务器组执行超时,size={} other={}", serverModels.size(), latch.getCount());
            return false;
        }
        if (success.get() < serverModels.size()) {
            log.error("服务器组执行部分失败,size={} fail={}", serverModels.size(), serverModels.size() - success.get());
            return false;
        }
        return true;
    }

    @Override
    public List<ServerModel> selectEnvServices(ProjectsModel projectsModel, ADDPEnvironment environment) {
        List<ServerModel> serverModels = serverJpa.selectEnvServices(
                projectsModel._getProjectsServiceRes()
                        .stream()
                        .map(ProjectsServerRe::getServerId)
                        .collect(Collectors.toList())
                , environment
        );
        if (environment == ADDPEnvironment.pro) {
            serverModels.addAll(serverJpa.selectEnvServices(
                    projectsModel._getProjectsServiceRes()
                            .stream()
                            .map(ProjectsServerRe::getServerId)
                            .collect(Collectors.toList())
                    , ADDPEnvironment.bak
            ));
        }
        // 生产环境只返回允许发布的机器  无缝接入分批发布
        return serverModels.stream().filter(service -> environment != ADDPEnvironment.pro || service.getAllowRestart()).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateProAllow(List<Long> ids) throws Exception{
        if (serverJpa.updateProAllow(ids) != ids.size()) {
            throw new WebRunException(Code.dataError,"机器状态更新失败");
        }
    }

    @Override
    public Page<ServerModel> memberServices(WebPageable webPageable, ADDPEnvironment environment) {
        MemberModel member = MemberCache.currentUser();
        return  serverJpa.findAll(Example.of(
                ServerModel.builder()
                        .memberId(member.getId())
                        .environment(environment)
                        .build()
        ), webPageable);
    }
}
