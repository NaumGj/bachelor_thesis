FROM java:openjdk-8u45-jdk

MAINTAINER tilen.faganel@me.com

RUN apt-get update -qq && apt-get install -y wget git

RUN wget http://mirrors.sonic.net/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz && \
       tar -zxf apache-maven-3.3.9-bin.tar.gz && rm apache-maven-3.3.9-bin.tar.gz && \
       mv apache-maven-3.3.9 /usr/local && ln -s /usr/local/apache-maven-3.3.9/bin/mvn /usr/bin/mvn

RUN mkdir /app

WORKDIR /app

ADD . /app

RUN mvn clean package

ENV JAVA_ENV=PRODUCTION

EXPOSE 8080

CMD ["java", "-server", "-cp", "module-webservices-shop-producer/target/classes:module-webservices-shop-producer/target/dependency/*", "com.kumuluz.ee.EeApplication"]