import http from 'k6/http';
import { sleep, check } from 'k6';
import { SharedArray } from 'k6/data';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

const users = new SharedArray('users', function() {
    return [
        { userId: 1, token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzQ3MzAwMTMwLCJleHAiOjE3NDczMDE5MzAsInJvbGVzIjpbIlVTRVIiXSwidXNlcklkIjozfQ.6i61_hwu88XnEdar3w8XLk-Kjfy6G9QPCtVII9WaVTU' },
        { userId: 2, token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzQ3MzAwMTMwLCJleHAiOjE3NDczMDE5MzAsInJvbGVzIjpbIlVTRVIiXSwidXNlcklkIjozfQ.6i61_hwu88XnEdar3w8XLk-Kjfy6G9QPCtVII9WaVTU' },
        // Add more test users as needed
    ];
});

export const options = {
    stages: [
        { duration: '30s', target: 30 }, // Ramp up to 30 users
        { duration: '2m', target: 30 },  // Stay at 30 users
        { duration: '30s', target: 0 },  // Ramp down
    ],
};

export default function () {
    const user = users[Math.floor(Math.random() * users.length)];

    const payload = JSON.stringify({
        userId: user.userId,
        title: `Test Post ${randomString(5)}`,
        content: `This is a test post content with some random text: ${randomString(50)}`,
        category: 'general'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${user.token}`
        },
    };

    const res = http.post('http://api-gateway:9000/posts', payload, params);

    check(res, {
        'post creation successful': (r) => r.status === 201,
        'returns post ID': (r) => JSON.parse(r.body).postId !== undefined,
    });

    sleep(1);
}