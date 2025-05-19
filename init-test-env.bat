@echo off
echo Setting up DocLink Load Testing Environment

REM Create necessary directories if they don't exist
if not exist "grafana\provisioning\dashboards" mkdir "grafana\provisioning\dashboards"
if not exist "grafana\provisioning\datasources" mkdir "grafana\provisioning\datasources"
if not exist "prometheus" mkdir "prometheus"
if not exist "k6-scripts" mkdir "k6-scripts"

REM Create Prometheus config
echo Creating Prometheus configuration...
(
echo global:
echo   scrape_interval: 15s
echo   evaluation_interval: 15s
echo.
echo scrape_configs:
echo   - job_name: 'prometheus'
echo     static_configs:
echo       - targets: ['localhost:9090']
echo.
echo   - job_name: 'user-service'
echo     metrics_path: '/actuator/prometheus'
echo     static_configs:
echo       - targets: ['user-service:8080']
echo.
echo   - job_name: 'post-service'
echo     metrics_path: '/actuator/prometheus'
echo     static_configs:
echo       - targets: ['post-service:8081']
echo.
echo   - job_name: 'appointment-service'
echo     metrics_path: '/actuator/prometheus'
echo     static_configs:
echo       - targets: ['appointment-service:8082']
echo.
echo   - job_name: 'api-gateway'
echo     metrics_path: '/actuator/prometheus'
echo     static_configs:
echo       - targets: ['api-gateway:9000']
) > prometheus\prometheus.yml

REM Create Grafana datasource config
echo Creating Grafana datasource configuration...
(
echo apiVersion: 1
echo.
echo datasources:
echo   - name: Prometheus
echo     type: prometheus
echo     access: proxy
echo     url: http://prometheus:9090
echo     isDefault: true
echo.
echo   - name: InfluxDB
echo     type: influxdb
echo     access: proxy
echo     url: http://influxdb:8086
echo     database: k6
echo     isDefault: false
) > grafana\provisioning\datasources\datasources.yaml

REM Create Grafana dashboard config
echo Creating Grafana dashboard configuration...
(
echo apiVersion: 1
echo.
echo providers:
echo   - name: 'Default'
echo     orgId: 1
echo     folder: ''
echo     type: file
echo     disableDeletion: false
echo     updateIntervalSeconds: 10
echo     options:
echo       path: /etc/grafana/provisioning/dashboards
) > grafana\provisioning\dashboards\dashboard.yaml

REM Download pre-made Grafana dashboard
echo Downloading K6 Grafana dashboard...
curl -L -o grafana\provisioning\dashboards\k6-dashboard.json https://raw.githubusercontent.com/grafana/k6/master/grafana/k6-load-testing-results.json

echo Environment setup complete!
echo To start the environment, run: docker-compose up -d
echo To run tests, use: run-tests.bat
pause