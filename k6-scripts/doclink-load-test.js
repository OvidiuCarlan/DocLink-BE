import http from 'k6/http';
import { sleep, check, group } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { b64decode } from 'k6/encoding';

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

    const userData = JSON.stringify({
        firstName: 'Load',
        lastName: 'Test',
        email: email,
        password: password
    });

    console.log(`Sending user registration request: ${userData}`);

    const registerRes = http.post('http://api-gateway:9000/users', userData, {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`Registration response status: ${registerRes.status}`);
    console.log(`Registration response body: ${registerRes.body}`);
    console.log(`Registration response headers: ${JSON.stringify(registerRes.headers)}`);

    check(registerRes, {
        'User created successfully': (r) => r.status === 201
    });

    // Login to get auth token
    const loginData = JSON.stringify({
        email: email,
        password: password
    });

    console.log(`Sending login request: ${loginData}`);

    const loginRes = http.post('http://api-gateway:9000/users/tokens', loginData, {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`Login response status: ${loginRes.status}`);
    console.log(`Login response body: ${loginRes.body}`);
    console.log(`Login response headers: ${JSON.stringify(loginRes.headers)}`);

    check(loginRes, {
        'Login successful': (r) => r.status === 201
    });

    try {
        const loginResult = JSON.parse(loginRes.body);
        accessToken = loginResult.accessToken;
        console.log(`Got access token: ${accessToken ? 'yes' : 'no'}`);

        // If using a JWT token, parse it to get the userId
        // This assumes your JWT contains the userId in the payload
        if (accessToken) {
            try {
                const tokenParts = accessToken.split('.');
                console.log(`Token parts: ${tokenParts.length}`);
                const payload = JSON.parse(b64decode(tokenParts[1]));
                console.log(`Token payload: ${JSON.stringify(payload)}`);
                userId = payload.userId;
                console.log(`Setup complete. Using userId: ${userId}`);
            } catch (e) {
                console.log(`Error parsing token: ${e.message}`);
                console.log('Using default userId 1');
                userId = 1;
            }
        } else {
            console.log('No access token received, using default userId 1');
            userId = 1;
        }
    } catch (e) {
        console.log(`Error parsing login response: ${e.message}`);
        console.log('Using default userId 1 and no token');
        userId = 1;
        accessToken = '';
    }

    return { userId, accessToken };
}

export default function(data) {
    const { userId, accessToken } = data;
    console.log(`Starting iteration with userId: ${userId} and token: ${accessToken ? 'present' : 'missing'}`);

    const authHeaders = {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
    };

    group('Get Posts', function() {
        const res = http.get(`http://api-gateway:9000/posts/${userId}`, {
            headers: authHeaders,
        });

        console.log(`Get Posts response: ${res.status}, body length: ${res.body ? res.body.length : 0}`);

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

        console.log(`Create Post response: ${res.status}, body: ${res.body}`);

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

                console.log(`Create Appointment response: ${res.status}, body: ${res.body}`);

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

        console.log(`Get Appointments response: ${res.status}, body length: ${res.body ? res.body.length : 0}`);

        check(res, {
            'Get Appointments Status is 200': (r) => r.status === 200,
        });

        sleep(1);
    });
}