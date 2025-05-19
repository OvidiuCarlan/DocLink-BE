import http from 'k6/http';
import { sleep, check } from 'k6';

export default function() {
    // Try different URL variations
    const urls = [
        'http://api-gateway:9000/',
        'http://api-gateway:9000/users',
        'http://172.18.0.10:9000/',  // Using IP directly
        'http://172.18.0.10:9000/users'
    ];

    for (const url of urls) {
        console.log(`Sending request to: ${url}`);

        const res = http.get(url);

        console.log(`URL: ${url}, Response status: ${res.status}`);
        if (res.body) {
            console.log(`Response body length: ${res.body.length}`);
            if (res.body.length < 100) {
                console.log(`Full response body: ${res.body}`);
            }
        }

        sleep(1);
    }
}