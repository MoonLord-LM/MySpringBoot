:begin

call mvn -B -U -e install -DcreateChecksum=true --file "pom.xml"

pause
cls
goto :begin
