// File: k6-scripts/doclink-load-test.js
import http from 'k6/http';
import { sleep, check, group } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Test configuration
export const options = {
    stages: [
        { duration: '30s', target: 10 }, // Ramp up
        { duration: '1m', target: 10 },  // Steady load
        { duration: '30s', target: 0 },  // Ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete within 500ms
    },
};

// Global variables to store session data
let userId;
let accessToken;

export function setup() {
    console.log('Setting up test - creating test user and getting token');

    // Create a random test user
    const email = `loadtest_${randomString(8)}@example.com`;
    const password = 'TestPassword123';

    const registerRes = http.post('http://api-gateway:9000/users', JSON.stringify({
        firstName: 'Load',
        lastName: 'Test',
        email: email,
        password: password
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    check(registerRes, {
        'User created successfully': (r) => r.status === 201
    });

    // Login to get auth token
    const loginRes = http.post('http://api-gateway:9000/users/tokens', JSON.stringify({
        email: email,
        password: password
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    check(loginRes, {
        'Login successful': (r) => r.status === 201
    });

    const loginData = JSON.parse(loginRes.body);
    accessToken = loginData.accessToken;

    // If using a JWT token, parse it to get the userId
    // This assumes your JWT contains the userId in the payload
    if (accessToken) {
        try {
            const tokenParts = accessToken.split('.');
            const payload = JSON.parse(atob(tokenParts[1]));
            userId = payload.userId;
            console.log(`Setup complete. Using userId: ${userId}`);
        } catch (e) {
            console.log('Could not parse token for userId, using default 1');
            userId = 1;
        }
    }

    return { userId, accessToken };
}

export default function(data) {
    const { userId, accessToken } = data;

    const authHeaders = {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
    };

    group('Get Posts', function() {
        const res = http.get(`http://api-gateway:9000/posts/${userId}`, {
            headers: authHeaders,
        });

        check(res, {
            'Get Posts Status is 200': (r) => r.status === 200,
        });

        sleep(1);
    });

    group('Create Post', function() {
        const title = `Test Post ${randomString(5)}`;
        const content = `This is test content ${randomString(20)}`;

        const res = http.post('http://api-gateway:9000/posts', JSON.stringify({
            userId: userId,
            title: title,
            content: content,
            category: 'general'
        }), {
            headers: authHeaders,
        });

        check(res, {
            'Create Post Status is 201': (r) => r.status === 201,
        });

        if (res.status === 201) {
            const postData = JSON.parse(res.body);
            const postId = postData.postId;

            // Create an appointment for this post
            group('Create Appointment', function() {
                // Generate future date
                const today = new Date();
                const futureDate = new Date();
                futureDate.setDate(today.getDate() + 7);
                const dateStr = futureDate.toISOString().split('T')[0];

                const res = http.post('http://api-gateway:9000/appointments', JSON.stringify({
                    userId: userId,
                    postId: postId,
                    date: dateStr,
                    time: '10:00',
                    notes: 'Auto-generated test appointment'
                }), {
                    headers: authHeaders,
                });

                check(res, {
                    'Create Appointment Status is 201': (r) => r.status === 201,
                });
            });
        }

        sleep(1);
    });

    group('Get Appointments', function() {
        const res = http.get(`http://api-gateway:9000/appointments/${userId}`, {
            headers: authHeaders,
        });

        check(res, {
            'Get Appointments Status is 200': (r) => r.status === 200,
        });

        sleep(1);
    });
}