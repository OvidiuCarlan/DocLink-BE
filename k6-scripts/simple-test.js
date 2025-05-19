import http from 'k6/http';
import { sleep } from 'k6';

export default function() {
    const userData = JSON.stringify({
        firstName: 'Simple',
        lastName: 'Test',
        email: `simple${Date.now()}@example.com`,
        password: 'simplepassword'
    });

    console.log(`Testing simple registration...`);

    const res = http.post('http://api-gateway:9000/users', userData, {
        headers: { 'Content-Type': 'application/json' }
    });

    console.log(`Status: ${res.status}`);
    console.log(`Response: ${res.body}`);

    sleep(1);
}