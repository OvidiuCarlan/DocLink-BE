import http from 'k6/http';
import { sleep, check } from 'k6';


export const options = {
    stages: [
        { duration: '1m', target: 50 }, // Ramp up to 50 users
        { duration: '3m', target: 50 }, // Stay at 50 users
        { duration: '1m', target: 0 },  // Ramp down
    ],
};

export default function () {
    const payload = JSON.stringify({
        email: 'test@mail.com',
        password: 'test',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post('http://api-gateway:9000/users/tokens', payload, params);

    check(res, {
        'login successful': (r) => r.status === 200 || r.status === 201,
    });

    sleep(1);
}