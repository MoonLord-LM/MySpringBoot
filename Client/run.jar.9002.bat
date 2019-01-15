@echo off

set "active_profiles=9002"

set "jar_file=target\client-0.0.1-SNAPSHOT.jar"
if not exist "target" (
    mkdir "target"
)
if not exist "%jar_file%" (
    call mvn install
)

copy /B "%jar_file%" "%jar_file%.%active_profiles%"
set "jar_file=%jar_file%.%active_profiles%"

echo java -jar "%jar_file%" --spring.profiles.active=%active_profiles% ^>^>"%~n0.log" 2^>^>^&1
java -jar "%jar_file%" --spring.profiles.active=%active_profiles% >>"%~n0.log" 2>>&1

exit
