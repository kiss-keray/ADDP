#!/bin/sh
# 等到jar包
jar=$(ls ./|grep \\.name)
env=$(ls ./|grep \\.env)
echo $jar
# 等到项目名
APP=$(echo ${jar%.name*})
env=$(echo ${env%.env*})
# 启动命令
echo $APP
echo $env
if [ "$env" = "bak" ]
    then
        java -jar /home/admin/${APP}/${APP}.jar --spring.profiles.active=pro
else
    java -jar /home/admin/${APP}/${APP}.jar --spring.profiles.active=${env}
fi

