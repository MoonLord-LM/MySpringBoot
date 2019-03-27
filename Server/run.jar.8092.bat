@echo off
chcp 65001

set "active_profiles=8092"

set "jar_file=target\server-0.0.1-SNAPSHOT.jar"
if not exist "target" (
    mkdir "target"
)
if not exist "%jar_file%" (
    call mvn install
)

set "log_file=logs\%~n0.log"
if not exist "logs" (
    mkdir "logs"
)

if not exist "%jar_file%.%active_profiles%" (
    copy /B "%jar_file%" "%jar_file%.%active_profiles%"
)

set "jar_file=%jar_file%.%active_profiles%"

echo java -jar "%jar_file%" --spring.profiles.active=%active_profiles% ^>^>"%log_file%" 2^>^>^&1
java -jar "%jar_file%" --spring.profiles.active=%active_profiles% >>"%log_file%" 2>>&1

exit
