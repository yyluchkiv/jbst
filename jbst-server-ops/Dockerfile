FROM eclipse-temurin:17.0.7_7-jdk-alpine
WORKDIR /app
EXPOSE ${PORT}
ADD /target/app.jar app.jar
CMD java ${JVM_ARGUMENTS} -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -Dspring.profiles.active=${SPRING_BOOT_PROFILE} -Dspring.config.location=${CONFIG_LOCATIONS} -Djasypt.encryptor.password=${JASYPT_PASSWORD} -jar app.jar
