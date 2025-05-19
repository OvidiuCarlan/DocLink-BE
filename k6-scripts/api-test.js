// Modified: k6-scripts/api-test.js
import http from 'k6/http';
import { sleep, check } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export default function() {
    // Generate unique email for each test run
    const uniqueEmail = `test_${randomString(8)}_${Date.now()}@example.com`;

    // Test user registration (POST) - removed id field to let the backend generate it
    // Also removed the role object to use the default role assignment
    const userData = JSON.stringify({
        firstName: 'Test',
        lastName: 'User',
        email: uniqueEmail,
        password: 'password123'
    });

    console.log(`Testing user registration with email: ${uniqueEmail}`);
    const registerRes = http.post('http://api-gateway:9000/users', userData, {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`Registration status: ${registerRes.status}`);

    // Check registration was successful
    check(registerRes, {
        'Registration successful': (r) => r.status === 201,
        'Registration returns userId': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.userId !== undefined;
            } catch (e) {
                console.error(`Failed to parse registration response: ${e.message}`);
                return false;
            }
        }
    });

    // If registration worked, try login
    if (registerRes.status === 201) {
        const loginData = JSON.stringify({
            email: uniqueEmail,
            password: 'password123'
        });

        console.log('Testing login with registered user');
        const loginRes = http.post('http://api-gateway:9000/users/tokens', loginData, {
            headers: { 'Content-Type': 'application/json' }
        });

        console.log(`Login status: ${loginRes.status}`);

        // Check login was successful
        check(loginRes, {
            'Login successful': (r) => r.status === 201 || r.status === 200,
            'Login returns valid token': (r) => {
                try {
                    const body = JSON.parse(r.body);
                    return body.accessToken && body.accessToken.length > 20;
                } catch (e) {
                    console.error(`Failed to parse login response: ${e.message}`);
                    return false;
                }
            }
        });

        // If we got a token, try accessing protected endpoints
        if (loginRes.status === 201 || loginRes.status === 200) {
            try {
                const token = JSON.parse(loginRes.body).accessToken;
                console.log(`Received token: ${token.substring(0, 15)}...`);

                // Try getting user's posts using the token
                console.log('Testing access to protected endpoint (GET /posts)');

                // Get userId from registration response
                let userId;
                try {
                    userId = JSON.parse(registerRes.body).userId;
                } catch {
                    userId = '1'; // Fallback to a default ID if parsing fails
                }

                const postsRes = http.get(`http://api-gateway:9000/posts/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                console.log(`GET /posts status: ${postsRes.status}`);

                check(postsRes, {
                    'Protected endpoint access successful': (r) => r.status < 400,
                });
            } catch (e) {
                console.error(`Error in token workflow: ${e.message}`);
            }
        }
    }

    sleep(1);
}