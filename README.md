# MySpringBoot
Spring Boot Demo Projects

[![Java CI with Maven](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven.yml/badge.svg)](https://github.com/MoonLord-LM/MySpringBoot/actions/workflows/maven.yml)

# Maven Settings  
https://maven.aliyun.com  

    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>

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
