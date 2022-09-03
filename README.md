# MySpringBoot
Spring Boot Demo Projects

[![Maven with JDK 8](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven-jdk8.yml/badge.svg)](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven-jdk8.yml)
[![Maven with JDK 11](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven-jdk11.yml/badge.svg)](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven-jdk11.yml)
[![Maven with JDK 17](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven-jdk17.yml/badge.svg)](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven-jdk17.yml)
[![Code check with CodeQL](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/codeql.yml/badge.svg)](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/codeql.yml)

# Build in Linux

    mkdir -m 777 -p '/home/github' && cd '/home/github'
    git clone 'https://github.com/MoonLord-LM/MySpringBoot.git'
    cd '/home/github/MySpringBoot'
    git pull -v --progress 'origin'
    mvn -B -U -e install --file 'parent/pom.xml' && mvn -B -U -e install --file 'pom.xml'

# Build in Windows

    mkdir "C:/Software/GitHub/" & cd "C:/Software/GitHub/"
    git clone "https://github.com/MoonLord-LM/MySpringBoot.git"
    cd "C:/Software/GitHub/MySpringBoot"
    git pull -v --progress "origin"
    mvn -B -U -e install --file "parent/pom.xml" & mvn -B -U -e install --file "pom.xml"

# Maven Settings

    <!-- 阿里云公共仓库 -->
    <!-- https://maven.aliyun.com/mvn/guide -->
    <mirror>
      <id>aliyun-maven-central</id>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-public</id>
      <mirrorOf>jcenter,public</mirrorOf>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-google</id>
      <mirrorOf>google</mirrorOf>
      <url>https://maven.aliyun.com/repository/google</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-gradle-plugin</id>
      <mirrorOf>gradle-plugin</mirrorOf>
      <url>https://maven.aliyun.com/repository/gradle-plugin</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-spring</id>
      <mirrorOf>spring</mirrorOf>
      <url>https://maven.aliyun.com/repository/spring</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-spring-plugin</id>
      <mirrorOf>spring-plugin</mirrorOf>
      <url>https://maven.aliyun.com/repository/spring-plugin</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-grails-core</id>
      <mirrorOf>grails-core</mirrorOf>
      <url>https://maven.aliyun.com/repository/grails-core</url>
    </mirror>
    <mirror>
      <id>aliyun-maven-apache-snapshots</id>
      <mirrorOf>apache-snapshots</mirrorOf>
      <url>https://maven.aliyun.com/repository/apache-snapshots</url>
    </mirror>
