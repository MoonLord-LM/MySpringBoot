@echo off
chcp 65001 >nul 2>&1

set "jar_file=target\starter-0.0.1-SNAPSHOT.jar"
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

echo java -jar "%jar_file%" ^>^>"%log_file%" 2^>^>^&1
java -jar "%jar_file%" >>"%log_file%" 2>>&1

:end
echo.
echo view the log file at "%log_file%"
echo.
pause
explorer "%log_file%"

exit
