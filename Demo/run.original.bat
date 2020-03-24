@echo off
chcp 65001 >nul 2>&1

set "jar_file=target\demo-0.0.1-SNAPSHOT.jar.original"
set "lib_path=target\lib"
set "main_class=cn.moonlord.DemoApplication"
set "log_file=logs\%~n0.log"

if not exist "logs" (
  echo mkdir "logs"
  mkdir "logs"
  if not exist "logs" ( goto :end )
)
if exist "%log_file%" (
  echo del /f /q "%log_file%"
  del /f /q "%log_file%"
  if exist "%log_file%" ( goto :end )
)

if not exist "target" (
  echo mkdir "target"
  mkdir "target"
  if not exist "target" ( goto :end )
)
if not exist "%jar_file%" (
  echo call mvn install ^>^>"%log_file%" 2^>^>^&1
  call mvn install >>"%log_file%" 2>>&1
  if not exist "%jar_file%" ( goto :end )
)

if not exist "%lib_path%" (
  echo mkdir "%lib_path%"
  mkdir "%lib_path%"
  if not exist "%lib_path%" ( goto :end )
)
dir /b "%lib_path%" | find /c ".jar" >nul 2>&1
if %errorlevel%==1 (
  echo call mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="%lib_path%" ^>^>"%log_file%" 2^>^>^&1
  call mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="%lib_path%" >>"%log_file%" 2>>&1
)
dir /b "%lib_path%" | find /c ".jar" >nul 2>&1
if %errorlevel%==1 ( goto :end )

echo java -cp "%jar_file%" -Djava.ext.dirs="%JAVA_HOME%\jre\lib\ext;%lib_path%;" "%main_class%" ^>^>"%log_file%" 2^>^>^&1
java -cp "%jar_file%" -Djava.ext.dirs="%JAVA_HOME%\jre\lib\ext;%lib_path%;" "%main_class%" >>"%log_file%" 2>>&1

:end
echo.
echo view the log file at "%log_file%"
echo.
pause
explorer "%log_file%"

exit
