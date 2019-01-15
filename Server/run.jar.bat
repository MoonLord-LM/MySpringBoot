@echo off

set "jar_file=target\server-0.0.1-SNAPSHOT.jar"
if not exist "%jar_file%" (
    mkdir "target"
    call mvn install
)

echo java -jar "%jar_file%" ^>^>"%~n0.log" 2^>^>^&1
java -jar "%jar_file%" >>"%~n0.log" 2>>&1

exit
