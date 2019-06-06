package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;

import java.util.function.Consumer;

/**
 * @author keray
 * @date 2019/05/20 18:16
 */
public interface IReleaseBillService  extends BaseService<ReleaseBillModel,Long>{
    /**
     * <h1>自动部署1,2,3阶段,不适用与正式环境</h1>
     * <h4>
     *     正式环境发布步骤1,2阶段同时进行，3阶段需要分批，暂停
     * </h4>
     *
     *
     * 部署发布单
     * 暂时支持单一变更部署
     * 部署变更流程
     * <H1>一阶段</H1>
     * <li>git分支切换</li>
     * <li>pull代码</li>
     * <h1>二阶段</h1>
     * <li>mvn 打包</li>
     * <li>
     * 执行项目的ADDP-INF/build.sh (项目名，环境，主机占用端口)
     * <p>build.sh只构建镜像</p>
     * </li>
     * <h1>三阶段</h1>
     * <li>./ADDP-INF/start.sh</li>
     * <li>
     * <p>
     * 判断项目启动成功的标志。目前仅做spring系列的
     * 判断命令返回的行是否存在Tomcat started on port(s): 8000的字样
     * </p>
     * <li>docker logs -f --tail "10" {@link ProjectsModel#getName()}-{@link ReleaseBillModel#getEnvironment()}</li>
     * </li>
     *
     * 次方法将发布单从任何状态修改为{@link com.nix.jingxun.addp.web.IEnum.ReleasePhase#init}
     */
    ReleaseBillModel deployBranch(ReleaseBillModel releaseBillModel, Consumer<ReleaseBillModel> successCallback,Consumer<ReleaseBillModel> failCallback) throws Exception;

    /**
     * <H1>一阶段</H1>
     * <li>git分支切换</li>
     * <li>pull代码</li>
     * */
    boolean pullCode(ReleaseBillModel releaseBillModel);

    /**
     * <h1>二阶段</h1>
     * <li>mvn 打包</li>
     * <li>
     * 执行项目的ADDP-INF/build.sh (项目名，环境，主机占用端口)
     * <p>在build执行中的docker run启动了应用</p>
     * </li>
     * */
    boolean build(ReleaseBillModel releaseBillModel);

    /**
     * <h1>三阶段</h1>
     * <li>
     * <p>
     * 判断项目启动成功的标志。目前仅做spring系列的
     * 判断命令返回的行是否存在Tomcat started on port(s): 8000的字样
     * </p>
     * <li>docker logs -f --tail "10" {@link ProjectsModel#getName()}-{@link ReleaseBillModel#getEnvironment()}</li>
     * </li>
     */
    boolean startApp(ReleaseBillModel releaseBillModel);


    /**
     * 根据变更获取发布单
     * */
    ReleaseBillModel changeBill(Long changeId, ADDPEnvironment environment);

    /**
     * 给变更创建发布单
     * */
    ReleaseBillModel createBill(ChangeBranchModel model,ADDPEnvironment environment) throws Exception;

    /**
     * 获取项目当前在流程的发布单
     * */
    ReleaseBillModel selectProjectBill(Long projectId,ADDPEnvironment environment);


    /**
     * 发布单下线
     * */
    ReleaseBillModel billDown(Long billId) throws Exception;


    /**
     * 正式环境一阶段二阶段执行
     * */
    ReleaseBillModel proBuild(Long id ,Consumer<ReleaseBillModel> successCallback,Consumer<ReleaseBillModel> failCallback);

    /**
     * 获取发布单的总发布次数
     * */
    int getAllBatch(ReleaseBillModel releaseBillModel);

    /**
     * 自动进行正式环境第三阶段
     * */
    ReleaseBillModel proStart(ReleaseBillModel releaseBillModel,boolean skip);

    /**
     * 分批发布
     * */
    boolean proBatchRelease(ReleaseBillModel releaseBillModel,Integer batchNum);

    boolean pullCode(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception;

    boolean build(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception;

    boolean startApp(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception;

    boolean billDown(ReleaseBillModel bill, ShellExe shellExe);

    void setBillStatus(ReleaseBillModel billModel,ReleasePhase releasePhase, ReleaseType releaseType);

}
