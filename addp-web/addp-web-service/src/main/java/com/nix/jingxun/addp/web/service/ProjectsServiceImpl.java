package com.nix.jingxun.addp.web.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.common.config.WebConfig;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.ProjectsJpa;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import com.nix.jingxun.addp.web.model.relationship.jpa.ProjectsServerReJpa;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServerRe;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author keray
 * @date 2019/04/21 13:57
 */
@Service
@Slf4j
public class ProjectsServiceImpl extends BaseServiceImpl<ProjectsModel, Long> implements IProjectsService {

    @Resource
    private ProjectsJpa projectsJpa;
    @Resource
    private ProjectsServerReJpa projectsServerReJpa;

    @Resource
    private IServerService servicesService;
    @Resource
    private IReleaseBillService releaseBillService;

    @Override
    protected JpaRepository<ProjectsModel, Long> jpa() {
        return projectsJpa;
    }
    @Transactional
    @Override
    public ProjectsModel update(ProjectsModel o) throws Exception {
        List<ServerModel> old = findById(o.getId())._getServerModels();
        super.update(o);
        boolean result = servicesService.moreServiceExec(
                o._getServerModels().stream().parallel()
                        .filter(s -> old.stream().noneMatch(s1 -> s1.getId().equals(s.getId())))
                        .collect(Collectors.toList()), servicesModel -> {
                    try {
                       Assert.isTrue(addProjectAtServer(servicesModel,o),StrUtil.format("添加服务器失败{}",servicesModel.getIp()));
                    } catch (Exception e) {
                        throw new ShellExeException(e);
                    }
                });
        Assert.isTrue(result, "添加新服务器失败");
        result = servicesService.moreServiceExec(old.stream().parallel()
                .filter(s -> o._getServerModels().stream().noneMatch(s1 -> s1.getId().equals(s.getId()))).collect(Collectors.toList()), (serverModel) -> {
            try {
                Assert.isTrue(deleteProjectAtServer(serverModel,o),StrUtil.format("移除服务器失败{}",serverModel.getIp()));
            } catch (Exception e) {
                throw new ShellExeException(e);
            }
        });
        Assert.isTrue(result, "移除旧服务器文件失败");
        return o;
    }

    /**
     * 新建项目时需要做的流程
     * v1：使用其他的git仓库
     * 流程链接服务器，在/opt/addp/目录下git clone仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectsModel save(ProjectsModel projectsModel) throws Exception {
        super.save(projectsModel);
        projectsModel._getServerModels().forEach(s -> addProjectAtServer(s,projectsModel));
        return projectsModel;
    }

    public void createGitClone(ProjectsModel projectsModel, ShellExe shellExe) throws ShellExeException {
        // cd /usr/addp 没有则创建 如果有就删除
        if (ShellUtil.cd(StrUtil.format("{}{}", WebConfig.addpBaseFile, projectsModel.getName()), shellExe)) {
            shellExe.oneCmd(StrUtil.format("rm -rf {}{}", WebConfig.addpBaseFile, projectsModel.getName()));
        }
        ShellUtil.cd(WebConfig.addpBaseFile, shellExe);
        shellExe.syncExecute(StrUtil.format("mkdir -p {}", WebConfig.addpBaseFile), ShellExeLog.success, ShellExeLog.fail)
                // git clone gitUtl "/opt/addp/{projectName}"
                .syncExecute(StrUtil.format("git clone {} \"{}{}\"", projectsModel.getGitUrl(), WebConfig.addpBaseFile, projectsModel.getName()),
                        result -> {
                            ShellExeLog.success.accept(result, StrUtil.format("git clone {} \"{}{}\"", projectsModel.getGitUrl(), WebConfig.addpBaseFile, projectsModel.getName()));
                            // 判断git是否需要认证
                            if (ShellUtil.shellNeedKeydown(result.toString())) {
                                servicesService.gitAuth(shellExe, projectsModel);
                            }
                        },
                        error -> ShellExeLog.fail.accept(error, StrUtil.format("git clone {} \"{}{}\" fail", projectsModel.getGitUrl(), WebConfig.addpBaseFile, projectsModel.getName())))
                .close();
        // git clone成功
    }

    @Override
    public boolean cdRoot(ProjectsModel projectsModel, ShellExe shellExe) {
        // cd 到项目目录
        return ShellUtil.cd(StrUtil.format("{}{}", WebConfig.addpBaseFile, projectsModel.getName()), shellExe);
    }

    @Override
    public boolean deleteProjectAtServer(ServerModel serverModel, ProjectsModel projectsModel) {
        try {
            ShellExe shellExe = servicesService.shellExeByUsername(serverModel);
            ReleaseBillModel bill = releaseBillService.selectProjectBill(projectsModel.getId(), serverModel.getEnvironment());
            if (cdRoot(projectsModel, shellExe) || serverModel.getEnvironment() == ADDPEnvironment.bak) {
                // 项目在当前服务器运行着
                if (bill != null && bill.getReleasePhase() != ReleasePhase.stop) {
                    shellExe.syncExecute(StrUtil.format("bash ./ADDP-INF/stop.sh {} {}", projectsModel.getName(), serverModel.getEnvironment()),
                            ShellExeLog.success, ShellExeLog.fail);
                }
                shellExe.syncExecute(StrUtil.format("rm -rf {}{}", WebConfig.addpBaseFile, projectsModel.getName()),
                        ShellExeLog.success, ShellExeLog.fail);

            }
            if (serverModel.getEnvironment() == ADDPEnvironment.pro) {
                List<ServerModel> bakServer = servicesService.selectAllServes(projectsModel,ADDPEnvironment.bak);
                if (CollectionUtil.isNotEmpty(bakServer)) {
                    ServerModel bak = bakServer.get(0);
                    // 移除备份服务器
                    if (bak.getIp().equals(serverModel.getIp())) {
                        deleteProjectAtServer(bak,projectsModel);
                    }
                }

            }
            projectsServerReJpa.deleteById(projectsServerReJpa.findOne(
                    Example.of(ProjectsServerRe.builder().serverId(serverModel.getId()).projectsId(projectsModel.getId()).build())
            ).orElse(null).getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addProjectAtServer(ServerModel serverModel, ProjectsModel projectsModel) {
        try {
            ShellExe shellExe = servicesService.shellExeByUsername(serverModel);
            createGitClone(projectsModel, shellExe);
            // 给新加的服务器部署项目
            ReleaseBillModel bill = releaseBillService.selectProjectBill(projectsModel.getId(), serverModel.getEnvironment());
            // 如果当前项目当前环境有部署 对新加的服务器进行部署
            if (bill != null && bill.getReleasePhase() != ReleasePhase.stop) {
                serverModel.setAllowRestart(true);
                // 关闭项目现有机器的发布允许发布状态
                projectsModel._getServerModels().forEach(s -> {
                    s.setAllowRestart(false);
                    try {
                        servicesService.update(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // 重新自动部署发布单
                releaseBillService.deployBranch(bill, (r) -> {
                    // 发布完成将机器全部改为允许发布
                    projectsModel._getServerModels().forEach(s -> {
                        s.setAllowRestart(true);
                        try {
                            servicesService.update(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }, r -> {
                    log.error("{} 项目添加服务器{}失败", projectsModel.getProName(), serverModel.getId());
                    deleteProjectAtServer(serverModel, projectsModel);
                });
            }
            // 创建备份服务器
            if (serverModel.getEnvironment() == ADDPEnvironment.pro && projectsModel._getProServer().size() == 1) {
                ServerModel bakService = new ServerModel();
                BeanUtil.copyProperties(serverModel,bakService);
                bakService.setId(null);
                bakService.setEnvironment(ADDPEnvironment.bak);
                servicesService.save(bakService);
                projectsModel.getProjectsServiceRes().add(ProjectsServerRe.builder()
                        .projectsId(projectsModel.getId())
                        .serverId(bakService.getId()).build());
                projectsServerReJpa.save(ProjectsServerRe.builder()
                        .serverId(bakService.getId())
                        .projectsId(projectsModel.getId())
                        .build());
            }
            projectsServerReJpa.save(ProjectsServerRe.builder()
                    .serverId(serverModel.getId())
                    .projectsId(projectsModel.getId())
                    .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
