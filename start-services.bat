@echo off
echo Starting main services...
docker-compose up -d

echo Waiting for API Gateway to be ready...
set max_attempts=30
set attempt=0

:check_loop
set /a attempt+=1
echo Checking API Gateway (attempt %attempt%/%max_attempts%)...

curl -s http://localhost:9000/ >nul 2>&1
if %errorlevel% equ 0 (
    echo API Gateway is responding!
    goto :gateway_ready
)
if %errorlevel% equ 52 (
    echo API Gateway is responding with empty result!
    goto :gateway_ready
)

echo API Gateway not ready yet...
if %attempt% lss %max_attempts% (
    timeout /t 10 >nul
    goto :check_loop
)

echo API Gateway did not become ready in time. Check your services.
exit /b 1

:gateway_ready
echo Starting monitoring and testing services...
docker-compose -f docker-compose.operations.yml up -d

echo All services started!
echo =============================================================
echo Access Grafana at http://localhost:3000 (admin/admin)
echo Access Prometheus at http://localhost:9090
echo Run load tests with: docker exec -it k6 k6 run /scripts/full-system-test.js
echo Generate test data with: docker logs -f doclink-test-data-generator
echo =============================================================