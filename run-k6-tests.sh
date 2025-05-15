##!/bin/bash
#
## Run individual endpoint tests
#echo "Running user login test..."
#docker-compose exec k6 k6 run /scripts/user-login-test.js
#
#echo "Running user registration test..."
#docker-compose exec k6 k6 run /scripts/user-registration-test.js
#
#echo "Running create post test..."
#docker-compose exec k6 k6 run /scripts/create-post-test.js
#
#echo "Running get posts test..."
#docker-compose exec k6 k6 run /scripts/get-posts-test.js
#
#echo "Running create appointment test..."
#docker-compose exec k6 k6 run /scripts/create-appointment-test.js
#
#echo "Running get appointments test..."
#docker-compose exec k6 k6 run /scripts/get-appointments-test.js
#
## Optional: Run the full system test
#echo "Running full system test..."
#docker-compose exec k6 k6 run /scripts/full-system-test.js