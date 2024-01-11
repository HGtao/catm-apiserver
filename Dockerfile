# 设置基础镜像
FROM openjdk:17-jdk

# 设置工作目录
WORKDIR /app

COPY catm/target/catm.jar /app/catm.jar

EXPOSE 8000

# 设置容器启动命令
CMD ["java", "-jar", "catm.jar"]