import http from 'k6/http';
import { sleep, check, group } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { SharedArray } from 'k6/data';
import { Counter } from 'k6/metrics';

// Track custom metrics
const successfulLogins = new Counter('successful_logins');
const successfulPosts = new Counter('successful_posts');
const successfulAppointments = new Counter('successful_appointments');

// Pregenerate test data
const testEmails = new SharedArray('emails', function() {
    const emails = [];
    for (let i = 0; i < 50; i++) {
        emails.push(`loadtest_${randomString(8)}@example.com`);
    }
    return emails;
});

// Store tokens during test execution
const tokens = new Map();

export const options = {
    scenarios: {
        ramp_arrival_rate: {
            executor: 'ramping-arrival-rate',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 100,
            stages: [
                { duration: '1m', target: 10 },  // Ramp up to 10 requests/s
                { duration: '3m', target: 10 },  // Stay at 10 requests/s
                { duration: '1m', target: 20 },  // Ramp up to 20 requests/s
                { duration: '2m', target: 20 },  // Stay at 20 requests/s
                { duration: '1m', target: 0 },   // Ramp down to 0 requests/s
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete within 500ms
        'http_req_duration{endpoint:login}': ['p(95)<300'],
        'http_req_duration{endpoint:get_posts}': ['p(95)<200'],
        'successful_logins': ['count>100'],
    },
};

export default function () {
    const email = testEmails[Math.floor(Math.random() * testEmails.length)];
    const password = 'TestPassword123';
    let userId, userToken, postId;

    // Scenario 1: Register a new user (10% of the time)
    if (Math.random() < 0.1 && !tokens.has(email)) {
        group('user_registration', function() {
            const payload = JSON.stringify({
                firstName: 'Load',
                lastName: 'Test',
                email: email,
                password: password
            });

            const params = {
                headers: { 'Content-Type': 'application/json' },
                tags: { endpoint: 'register' },
            };

            const res = http.post('http://api-gateway:9000/users', payload, params);

            if (check(res, {
                'registration successful': (r) => r.status === 201,
            })) {
                userId = JSON.parse(res.body).userId;
                console.log(`Registered user: ${email} with ID: ${userId}`);
            }
        });

        sleep(1);
    }

    // Scenario 2: Login
    group('user_login', function() {
        const payload = JSON.stringify({
            email: email,
            password: password
        });

        const params = {
            headers: { 'Content-Type': 'application/json' },
            tags: { endpoint: 'login' },
        };

        const res = http.post('http://api-gateway:9000/users/tokens', payload, params);

        if (check(res, {
            'login successful': (r) => r.status === 201 || r.status === 200,
        })) {
            userToken = JSON.parse(res.body).accessToken;
            tokens.set(email, userToken);
            successfulLogins.add(1);
        }
    });

    if (!userToken) {
        // If login failed, try with a predefined test user
        userToken = 'placeholder-token'; // Replace with actual token
        userId = 1; // Replace with actual user ID
    }

    // Need token for subsequent requests
    const authHeaders = {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json'
    };

    // Scenario 3: Create Post (30% of the time)
    if (Math.random() < 0.3) {
        group('create_post', function() {
            const payload = JSON.stringify({
                userId: userId || 1,
                title: `Load Test Post ${randomString(5)}`,
                content: `This is a post created during load testing. ${randomString(30)}`,
                category: 'general'
            });

            const res = http.post(
                'http://api-gateway:9000/posts',
                payload,
                { headers: authHeaders, tags: { endpoint: 'create_post' } }
            );

            if (check(res, {
                'post creation successful': (r) => r.status === 201,
            })) {
                postId = JSON.parse(res.body).postId;
                successfulPosts.add(1);
            }
        });

        sleep(0.5);
    }

    // Scenario 4: Get Posts (60% of the time)
    if (Math.random() < 0.6) {
        group('get_posts', function() {
            const res = http.get(
                `http://api-gateway:9000/posts/${userId || 1}`,
                { headers: authHeaders, tags: { endpoint: 'get_posts' } }
            );

            check(res, {
                'get posts successful': (r) => r.status === 200,
            });
        });

        sleep(0.3);
    }

    // Scenario 5: Create Appointment (20% of the time)
    if (Math.random() < 0.2) {
        group('create_appointment', function() {
            // Generate date for appointment
            const today = new Date();
            const futureDate = new Date(today);
            futureDate.setDate(today.getDate() + Math.floor(Math.random() * 30) + 1);
            const dateStr = futureDate.toISOString().split('T')[0];

            // Random time
            const hour = Math.floor(Math.random() * 8) + 9;
            const minute = Math.random() > 0.5 ? '00' : '30';
            const timeStr = `${hour}:${minute}`;

            const payload = JSON.stringify({
                userId: userId || 1,
                postId: postId || 1,
                date: dateStr,
                time: timeStr,
                notes: `Load test appointment: ${randomString(20)}`
            });

            const res = http.post(
                'http://api-gateway:9000/appointments',
                payload,
                { headers: authHeaders, tags: { endpoint: 'create_appointment' } }
            );

            if (check(res, {
                'appointment creation successful': (r) => r.status === 201,
            })) {
                successfulAppointments.add(1);
            }
        });

        sleep(0.5);
    }

    // Scenario 6: Get Appointments (40% of the time)
    if (Math.random() < 0.4) {
        group('get_appointments', function() {
            const res = http.get(
                `http://api-gateway:9000/appointments/${userId || 1}`,
                { headers: authHeaders, tags: { endpoint: 'get_appointments' } }
            );

            check(res, {
                'get appointments successful': (r) => r.status === 200,
            });
        });
    }

    sleep(1);
}