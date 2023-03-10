#设置镜像基础，jdk8
FROM openjdk:17
#维护人员信息
MAINTAINER DX
#设置镜像对外暴露端口
EXPOSE 1883 3306
#将当前 target 目录下的 jar 放置在根目录下，命名为 app.jar，推荐使用绝对路径。
ADD target/mqtt-2.0-SNAPSHOT-jar-with-dependencies.jar /mqtt-2.0-SNAPSHOT-jar-with-dependencies.jar
#执行启动命令
ENTRYPOINT ["java", "-jar","/mqtt-2.0-SNAPSHOT-jar-with-dependencies.jar"]