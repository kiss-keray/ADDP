#!/bin/sh
# 应用名
APP=$1
# 环境
ENV=$2
# 应用端口
port=9000
# 构建应用镜像
docker build -t ${APP}:${ENV}   --build-arg APP_NAME=${APP} -f ./Dockerfile ./
# 启动应用镜像
docker run  --name ${APP}-${ENV}  -p ${port}:8000 -v /tmp:/tmp -dit ${APP}:${ENV}

docker exec -it ${APP}-${ENV} bash
