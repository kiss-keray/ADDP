package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;

/**
 * @author keray
 * @date 2019/05/20 18:16
 */
public interface IReleaseBillService  extends BaseService<ReleaseBillModel,Long>{
    /**
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
    ReleaseBillModel deployBranch(ReleaseBillModel releaseBillModel) throws Exception;

    /**
     * <H1>一阶段</H1>
     * <li>git分支切换</li>
     * <li>pull代码</li>
     * */
    boolean pullCode(ReleaseBillModel releaseBillModel) throws Exception;

    /**
     * <h1>二阶段</h1>
     * <li>mvn 打包</li>
     * <li>
     * 执行项目的ADDP-INF/build.sh (项目名，环境，主机占用端口)
     * <p>在build执行中的docker run启动了应用</p>
     * </li>
     * */
    boolean build(ReleaseBillModel releaseBillModel) throws Exception;

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
    boolean startApp(ReleaseBillModel releaseBillModel) throws Exception;


    /**
     * 根据变更发布单
     * */
    ReleaseBillModel changeBill(Long changeId, ADDPEnvironment environment);

    /**
     * 给变更创建发布单
     * */
    ReleaseBillModel createBill(ChangeBranchModel model,ADDPEnvironment environment) throws Exception;
}
