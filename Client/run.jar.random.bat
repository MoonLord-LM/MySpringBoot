@echo off

set "server_port=%random%"

set "log_file=logs\%~n0.%random%.log"
if not exist "logs" (
    mkdir "logs"
)

set "jar_file=target\client-0.0.1-SNAPSHOT.jar"
if not exist "target" (
    mkdir "target"
)
if not exist "%jar_file%" (
    call mvn install
)

echo java -jar "%jar_file%" --server.port=%server_port% ^>^>"%log_file%" 2^>^>^&1
java -jar "%jar_file%" --server.port=%server_port% >>"%log_file%" 2>>&1

exit