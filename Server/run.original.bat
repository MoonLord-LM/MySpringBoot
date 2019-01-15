@echo off

set "main_class=cn.moonlord.server.ServerApplication"

set "jar_file=target\server-0.0.1-SNAPSHOT.jar.original"
if not exist "target" (
    mkdir "target"
)
if not exist "%jar_file%" (
    call mvn install
)

set "lib_path=target\lib"
:: del /F /S /Q "%lib_path%"
if not exist "%lib_path%" (
    mkdir "%lib_path%"
    call mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="%lib_path%"
)

echo java -cp "%jar_file%" -Djava.ext.dirs="%JAVA_HOME%\jre\lib\ext;%lib_path%;" "%main_class%" ^>^>"%~n0.log" 2^>^>^&1
java -cp "%jar_file%" -Djava.ext.dirs="%JAVA_HOME%\jre\lib\ext;%lib_path%;" "%main_class%" >>"%~n0.log" 2>>&1

exit