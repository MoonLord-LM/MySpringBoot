@echo off
chcp 65001

set "jar_file=target\starter-0.0.1-SNAPSHOT.jar"

set "log_file=logs\%~n0.log"
if not exist "logs" (
    mkdir "logs"
)

if not exist "target" (
    mkdir "target"
)
if not exist "%jar_file%" (
    call mvn install >>"%log_file%" 2>>&1
)

echo java -jar "%jar_file%" ^>^>"%log_file%" 2^>^>^&1
java -jar "%jar_file%" >>"%log_file%" 2>>&1

echo see error log in "%log_file%"
pause
exit
