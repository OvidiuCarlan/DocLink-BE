const axios = require('axios');
const faker = require('faker');

const API_URL = 'http://api-gateway:9000';
const NUM_USERS = 50;
const NUM_POSTS = 100;
const NUM_APPOINTMENTS = 200;

// Store created data for reference
const users = [];
const posts = [];
const appointments = [];

async function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function createUsers() {
    console.log(`Creating ${NUM_USERS} test users...`);

    for (let i = 0; i < NUM_USERS; i++) {
        const firstName = faker.name.firstName();
        const lastName = faker.name.lastName();
        const email = `testuser_${i}_${Date.now()}@example.com`;
        const password = 'Test123456';

        try {
            const response = await axios.post(`${API_URL}/users`, {
                firstName,
                lastName,
                email,
                password
            });

            // Login to get the token
            const loginResponse = await axios.post(`${API_URL}/users/tokens`, {
                email,
                password
            });

            users.push({
                id: response.data.userId,
                email,
                password,
                token: loginResponse.data.accessToken
            });

            console.log(`Created user ${i+1}/${NUM_USERS}: ${email}`);
        } catch (error) {
            console.error(`Error creating user ${i+1}: ${error.message}`);
        }

        // Add a small delay to avoid overwhelming the API
        await wait(100);
    }

    console.log(`Created ${users.length} users successfully.`);

    // Save users to a file for k6 to use
    const fs = require('fs');
    fs.writeFileSync('/app/test-users.json', JSON.stringify(users));
    console.log('User data saved to test-users.json');
}

async function createPosts() {
    if (users.length === 0) {
        console.error('No users available to create posts');
        return;
    }

    console.log(`Creating ${NUM_POSTS} test posts...`);

    for (let i = 0; i < NUM_POSTS; i++) {
        // Select a random user
        const user = users[Math.floor(Math.random() * users.length)];

        const title = faker.lorem.sentence(5);
        const content = faker.lorem.paragraphs(3);
        const category = faker.random.arrayElement(['general', 'health-tip', 'announcement']);

        try {
            const response = await axios.post(`${API_URL}/posts`, {
                userId: user.id,
                title,
                content,
                category
            }, {
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                }
            });

            posts.push({
                id: response.data.postId,
                userId: user.id,
                title
            });

            console.log(`Created post ${i+1}/${NUM_POSTS}: ${title}`);
        } catch (error) {
            console.error(`Error creating post ${i+1}: ${error.message}`);
        }

        // Add a small delay
        await wait(100);
    }

    console.log(`Created ${posts.length} posts successfully.`);
}

async function createAppointments() {
    if (users.length === 0 || posts.length === 0) {
        console.error('No users or posts available to create appointments');
        return;
    }

    console.log(`Creating ${NUM_APPOINTMENTS} test appointments...`);

    for (let i = 0; i < NUM_APPOINTMENTS; i++) {
        // Select a random user and post
        const user = users[Math.floor(Math.random() * users.length)];
        const post = posts[Math.floor(Math.random() * posts.length)];

        // Generate random date (within next 30 days)
        const date = new Date();
        date.setDate(date.getDate() + Math.floor(Math.random() * 30) + 1);
        const dateStr = date.toISOString().split('T')[0];

        // Random time between 9 AM and 5 PM
        const hour = Math.floor(Math.random() * 8) + 9;
        const minute = Math.random() > 0.5 ? '00' : '30';
        const timeStr = `${hour}:${minute}`;

        const notes = faker.lorem.sentence();

        try {
            const response = await axios.post(`${API_URL}/appointments`, {
                userId: user.id,
                postId: post.id,
                date: dateStr,
                time: timeStr,
                notes
            }, {
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    'Content-Type': 'application/json'
                }
            });

            appointments.push({
                id: response.data.appointmentId,
                userId: user.id,
                postId: post.id,
                date: dateStr,
                time: timeStr
            });

            console.log(`Created appointment ${i+1}/${NUM_APPOINTMENTS}`);
        } catch (error) {
            console.error(`Error creating appointment ${i+1}: ${error.message}`);
        }

        // Add a small delay
        await wait(100);
    }

    console.log(`Created ${appointments.length} appointments successfully.`);
}

async function main() {
    console.log('Starting test data generation...');

    // Wait for services to be ready
    console.log('Waiting for services to be ready...');
    await wait(30000);

    try {
        await createUsers();
        await createPosts();
        await createAppointments();

        console.log('Test data generation completed!');
    } catch (error) {
        console.error('Error during test data generation:', error);
    }
}

main();