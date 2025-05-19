@echo off
echo Starting load tests...

REM Check if services are running
docker ps | findstr "api-gateway" > nul
if %errorlevel% neq 0 (
  echo Starting services...
  docker-compose up -d
  echo Waiting for services to start...
  timeout /t 30 /nobreak > nul
)

REM Verify k6-scripts directory and files exist
if not exist "k6-scripts" (
  echo Creating k6-scripts directory...
  mkdir k6-scripts
)

if not exist "k6-scripts\simple-test.js" (
  echo Error: Test scripts not found. Please create the test scripts first.
  exit /b 1
)

REM Run the simple test first
echo Running simple test to verify k6 is working...
docker-compose exec k6 k6 run /scripts/simple-test.js

REM If that succeeds, run the main test
echo Running main load test...
docker-compose exec k6 k6 run -o influxdb=http://influxdb:8086/k6 /scripts/doclink-load-test.js

echo Test complete! View results in Grafana at http://localhost:3000
pause