@echo off

set "jar_file=target\client-0.0.1-SNAPSHOT.jar"
if not exist "target" (
    mkdir "target"
)
if not exist "%jar_file%" (
    call mvn install
)

echo java -jar "%jar_file%" ^>^>"%~n0.log" 2^>^>^&1
java -jar "%jar_file%" >>"%~n0.log" 2>>&1

exit
