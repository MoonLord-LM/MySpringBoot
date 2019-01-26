@echo off

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

echo java -jar "%jar_file%" ^>^>"%log_file%" 2^>^>^&1
java -jar "%jar_file%" >>"%log_file%" 2>>&1

exit
