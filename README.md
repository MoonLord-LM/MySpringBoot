# Spring-Boot-Demo
Spring Boot Demo Projects

# Maven Settings  
https://maven.aliyun.com  

    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>

# TODO

    <properties>
        <!-- JDK 版本 -->
        <java.version>1.8</java.version>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
        <!-- 字符编码 -->
        <encoding>UTF-8</encoding>
        <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <!-- Spring 版本 -->
        <spring.boot.version>2.3.4.RELEASE</spring.boot.version><!-- 2020.09.17-->
        <spring.cloud.version>Hoxton.SR9</spring.cloud.version><!-- 2020.11.09-->
        <spring.cloud.alibaba.version>2.2.5.RELEASE</spring.cloud.alibaba.version><!-- 2021.02.01-->
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring.cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring.cloud.alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

