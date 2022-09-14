:begin


call mvn -B -U -e install --file "pom.xml" ^
 -DcreateChecksum=true ^
 -Dmaven.wagon.http.ssl.insecure=true ^
 -Dmaven.wagon.http.ssl.allowall=true ^
 -Dmaven.wagon.http.ssl.ignore.validity.dates=true


pause
cls
goto :begin
