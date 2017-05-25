FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD gitlab-to-s3-0.1.0.jar gitlab-to-s3.jar
RUN sh -c 'touch /gitlab-to-s3.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /gitlab-to-s3.jar" ]