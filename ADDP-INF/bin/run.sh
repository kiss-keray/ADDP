#!/bin/sh
# 等到jar包
jar=$(ls ./|grep \\.name)
echo $jar
# 等到项目名
APP=$(echo ${jar%.name*})
# 启动命令
echo $APP
java -jar /home/admin/${APP}/${APP}.jar

#tail -f /tmp/${APP}.log
