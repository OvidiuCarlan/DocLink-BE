@echo off
echo Checking if services are running...
docker ps | findstr "doclink-api-gateway" >nul
if errorlevel 1 (
    echo Services don't appear to be running. Starting services first...
    call start-services.bat
    timeout /t 30 >nul
)

echo Running user login test...
docker exec -it k6 k6 run /scripts/user-login-test.js

echo Running user registration test...
docker exec -it k6 k6 run /scripts/user-registration-test.js

echo Running create post test...
docker exec -it k6 k6 run /scripts/create-post-test.js

echo Running get posts test...
docker exec -it k6 k6 run /scripts/get-posts-test.js

echo Running create appointment test...
docker exec -it k6 k6 run /scripts/create-appointment-test.js

echo Running get appointments test...
docker exec -it k6 k6 run /scripts/get-appointments-test.js

echo Running full system test...
docker exec -it k6 k6 run /scripts/full-system-test.js

echo Tests completed! View results in Grafana at http://localhost:3000