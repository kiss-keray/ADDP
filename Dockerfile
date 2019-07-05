# 拉取基础镜像
FROM registry.cn-beijing.aliyuncs.com/jingxun/addp:1.0.1

RUN mkidr -p /home/admin

COPY ./* /home/admin
