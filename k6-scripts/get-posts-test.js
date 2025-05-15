import http from 'k6/http';
import { sleep, check } from 'k6';
import { SharedArray } from 'k6/data';

const users = new SharedArray('users', function() {
    return [
        { userId: 1, token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzQ3MzAwMTMwLCJleHAiOjE3NDczMDE5MzAsInJvbGVzIjpbIlVTRVIiXSwidXNlcklkIjozfQ.6i61_hwu88XnEdar3w8XLk-Kjfy6G9QPCtVII9WaVTU' },
        { userId: 2, token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzQ3MzAwMTMwLCJleHAiOjE3NDczMDE5MzAsInJvbGVzIjpbIlVTRVIiXSwidXNlcklkIjozfQ.6i61_hwu88XnEdar3w8XLk-Kjfy6G9QPCtVII9WaVTU' },
        // Add more test users as needed
    ];
});

export const options = {
    stages: [
        { duration: '30s', target: 50 }, // Ramp up to 50 users
        { duration: '3m', target: 50 },  // Stay at 50 users
        { duration: '30s', target: 0 },  // Ramp down
    ],
};

export default function () {
    const user = users[Math.floor(Math.random() * users.length)];

    const params = {
        headers: {
            'Authorization': `Bearer ${user.token}`
        },
    };

    const res = http.get(`http://api-gateway:9000/posts/${user.userId}`, params);

    check(res, {
        'get posts successful': (r) => r.status === 200,
        'returns posts array': (r) => Array.isArray(JSON.parse(r.body).posts),
    });

    sleep(1);
}