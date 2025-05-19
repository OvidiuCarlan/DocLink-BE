import http from 'k6/http';
import { sleep } from 'k6';

export default function() {
    const userData = JSON.stringify({
        firstName: 'Basic',
        lastName: 'Test',
        email: `basic_${Date.now()}@example.com`,
        password: 'password123'
    });

    console.log('Testing basic registration...');

    // Use IP address instead of hostname
    const res = http.post('http://172.18.0.10:9000/users', userData, {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`Status: ${res.status}`);
    console.log(`Response: ${res.body}`);

    sleep(1);
}