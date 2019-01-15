@echo off

set "active_profiles=8092"

set "jar_file=target\server-0.0.1-SNAPSHOT.jar"
if not exist "%jar_file%" (
    mkdir "target"
    call mvn install
)

copy /B "%jar_file%" "%jar_file%.%active_profiles%"
set "jar_file=%jar_file%.%active_profiles%"

echo java -jar "%jar_file%" --spring.profiles.active=%active_profiles% ^>^>"%~n0.log" 2^>^>^&1
java -jar "%jar_file%" --spring.profiles.active=%active_profiles% >>"%~n0.log" 2>>&1

exit
