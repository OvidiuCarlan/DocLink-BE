// File: k6-scripts/direct-user-service-test.js
import http from 'k6/http';
import { sleep } from 'k6';

export default function() {
    const userData = JSON.stringify({
        firstName: 'Direct',
        lastName: 'Test',
        email: `direct_${Date.now()}@example.com`,
        password: 'password123'
    });

    console.log('Testing direct connection to user-service...');

    const res = http.post('http://user-service:8080/users', userData, {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`Direct status: ${res.status}`);
    console.log(`Direct response: ${res.body}`);

    sleep(1);
}