@echo off
echo Starting DocLink Load Test Suite

REM Check if services are running
docker-compose ps | findstr "api-gateway" > nul
if %errorlevel% neq 0 (
  echo Starting services...
  docker-compose up -d
  echo Waiting for services to start...
  timeout /t 30 /nobreak > nul
)

echo.
echo Running Login Performance Test...
docker-compose exec k6 k6 run -o influxdb=http://influxdb:8086/k6 /scripts/login-test.js
echo Login test completed!

echo.
echo Running Main Load Test...
docker-compose exec k6 k6 run -o influxdb=http://influxdb:8086/k6 /scripts/doclink-load-test.js
echo Main load test completed!

echo.
echo All tests completed! View results in Grafana at http://localhost:3000
echo Dashboard login: admin/admin
pause