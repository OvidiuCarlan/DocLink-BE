import http from 'k6/http';
import { sleep, check } from 'k6';
import { SharedArray } from 'k6/data';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Pregenerate test data with user IDs and tokens
const users = new SharedArray('users', function() {
    return [
        { userId: 1, token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzQ3MzAwMTMwLCJleHAiOjE3NDczMDE5MzAsInJvbGVzIjpbIlVTRVIiXSwidXNlcklkIjozfQ.6i61_hwu88XnEdar3w8XLk-Kjfy6G9QPCtVII9WaVTU' },
        { userId: 2, token: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzQ3MzAwMTMwLCJleHAiOjE3NDczMDE5MzAsInJvbGVzIjpbIlVTRVIiXSwidXNlcklkIjozfQ.6i61_hwu88XnEdar3w8XLk-Kjfy6G9QPCtVII9WaVTU' },
        // Add more test users as needed
    ];
});

export const options = {
    stages: [
        { duration: '30s', target: 20 }, // Ramp up to 20 users
        { duration: '1m', target: 20 },  // Stay at 20 users
        { duration: '30s', target: 0 },  // Ramp down
    ],
};

export default function () {
    const user = users[Math.floor(Math.random() * users.length)];

    // Generate a random future date (within 30 days)
    const today = new Date();
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + Math.floor(Math.random() * 30) + 1);
    const dateStr = futureDate.toISOString().split('T')[0];

    // Random time between 9 AM and 5 PM
    const hour = Math.floor(Math.random() * 8) + 9;
    const minute = Math.random() > 0.5 ? '00' : '30';
    const timeStr = `${hour}:${minute}`;

    const payload = JSON.stringify({
        userId: user.userId,
        postId: 1, // Replace with actual post IDs
        date: dateStr,
        time: timeStr,
        notes: `Appointment notes: ${randomString(20)}`
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${user.token}`
        },
    };

    const res = http.post('http://api-gateway:9000/appointments', payload, params);

    check(res, {
        'appointment creation successful': (r) => r.status === 201,
        'returns appointment ID': (r) => JSON.parse(r.body).appointmentId !== undefined,
    });

    sleep(1);
}