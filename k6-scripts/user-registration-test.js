import http from 'k6/http';
import { sleep, check } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
    stages: [
        { duration: '30s', target: 20 }, // Ramp up to 20 users
        { duration: '1m', target: 20 },  // Stay at 20 users
        { duration: '30s', target: 0 },  // Ramp down
    ],
};

export default function () {
    const email = `user_${randomString(8)}@example.com`;

    const payload = JSON.stringify({
        firstName: 'Test',
        lastName: 'User',
        email: email,
        password: 'Password123',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post('http://api-gateway:9000/users', payload, params);

    check(res, {
        'registration successful': (r) => r.status === 201,
        'returns user ID': (r) => JSON.parse(r.body).userId !== undefined,
    });

    sleep(1);
}