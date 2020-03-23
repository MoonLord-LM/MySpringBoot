@echo off
chcp 65001

set "jar_file=target\starter-0.0.1-SNAPSHOT.jar.original"
set "main_class=cn.moonlord.StarterApplication"

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

set "lib_path=target\lib"
:: del /F /S /Q "%lib_path%"
if not exist "%lib_path%" (
    mkdir "%lib_path%"
    call mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="%lib_path%" >>"%log_file%" 2>>&1
)

echo java -cp "%jar_file%" -Djava.ext.dirs="%JAVA_HOME%\jre\lib\ext;%lib_path%;" "%main_class%" ^>^>"%log_file%" 2^>^>^&1
java -cp "%jar_file%" -Djava.ext.dirs="%JAVA_HOME%\jre\lib\ext;%lib_path%;" "%main_class%" >>"%log_file%" 2>>&1

echo see error log in "%log_file%"
pause
exit
