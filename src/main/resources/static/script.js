// Base URL for API
const API_BASE_URL = '/api';

// Helper function to make API calls
async function callApi(endpoint, method, data = null) {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        const token = sessionStorage.getItem('jwtToken');
        if (token) {
            options.headers['Authorization'] = 'Bearer ' + token;
        }

        if (data) {
            options.body = JSON.stringify(data);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        
        // Debug logging for QA
        console.log(`[API Debug] ${method} ${endpoint} -> Status: ${response.status}`);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Request failed with status ${response.status}: ${errorText}`);
        }

        // For DELETE requests that might not return JSON
        if (method === 'DELETE') {
            return await response.text();
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Show notification to user
function showNotification(message, isError = false) {
    alert(isError ? `Error: ${message}` : message);
}

// ADMIN LOGIN VALIDATION
function validateLogin() {
    const username = document.getElementById("username")?.value.trim();
    const password = document.getElementById("password")?.value.trim();

    // Default credentials
    const adminUsername = "admin";
    const adminPassword = "admin123";

    if (username === adminUsername && password === adminPassword) {
        // Store admin session (optional)
        sessionStorage.setItem("adminLoggedIn", "true");

        // Redirect to index page
        window.location.href = "index.html";
    } else {
        alert("Invalid username or password.");
    }
}

// Handle user registration form submission
document.getElementById('registerForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    try {
        const userData = {
            name: this.querySelector('input[name="name"]').value,
            email: this.querySelector('input[name="email"]').value,
            password: this.querySelector('input[name="password"]').value,
            role: this.querySelector('input[name="role"]').value,
            phone: this.querySelector('input[name="phone"]').value
        };

        const result = await callApi('/user/register', 'POST', userData);
        showNotification(`User registered successfully with ID: ${result.id}`);
        this.reset();
    } catch (error) {
        showNotification(error.message, true);
    }
});

// Handle user login form submission
document.getElementById('loginForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    try {
        const loginData = {
            email: this.querySelector('input[name="email"]').value,
            password: this.querySelector('input[name="password"]').value
        };

        const result = await callApi('/user/login', 'POST', loginData);

        if (result && result.token) {
            showNotification(`Login successful! Welcome ${result.name}`);
            sessionStorage.setItem('jwtToken', result.token);
            sessionStorage.setItem('currentUser', JSON.stringify(result));
            // window.location.href = 'dashboard.html'; // Optional
        } else {
            showNotification('Invalid email or password', true);
        }
    } catch (error) {
        showNotification('Login failed. Please check your credentials.', true);
    }
});

// Handle user update form submission
document.getElementById('updateForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    try {
        const userId = this.querySelector('input[name="id"]').value;
        const userData = {
            name: this.querySelector('input[name="name"]').value,
            email: this.querySelector('input[name="email"]').value,
            phone: this.querySelector('input[name="phone"]').value
        };

        const result = await callApi(`/user/update/${userId}`, 'PUT', userData);
        showNotification(`User updated successfully!`);
        this.reset();
    } catch (error) {
        showNotification(error.message, true);
    }
});

// Handle user delete form submission
document.getElementById('deleteForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    try {
        const userId = this.querySelector('input[name="id"]').value;

        if (!confirm(`Are you sure you want to delete user with ID: ${userId}?`)) {
            return;
        }

        const result = await callApi(`/user/delete/${userId}`, 'DELETE');
        showNotification(`User deleted successfully!`);
        this.reset();
    } catch (error) {
        showNotification(error.message, true);
    }
});

// Handle membership form submission
document.getElementById('addMembership')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    try {
        const formData = {
            userId: parseInt(this.querySelector('input[name="userId"]').value),
            type: this.querySelector('input[name="type"]').value,
            startDate: this.querySelector('input[name="startDate"]').value,
            durationMonths: parseInt(this.querySelector('input[name="durationMonths"]').value)
        };

        const result = await callApi('/membership/add', 'POST', formData);
        showNotification('Membership added successfully!');
        this.reset();
    } catch (error) {
        showNotification(error.message, true);
    }
});

// Handle attendance form submission
document.getElementById('attendanceForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    try {
        const attendanceData = {
            userId: parseInt(this.querySelector('input[name="userId"]').value),
            date: this.querySelector('input[name="date"]').value
        };

        const result = await callApi('/attendance/mark', 'POST', attendanceData);
        showNotification('Attendance marked successfully!');
        this.reset();
    } catch (error) {
        showNotification(error.message, true);
    }
});

function logoutAdmin() {
    sessionStorage.removeItem("adminLoggedIn");
    window.location.href = "admin-login.html";
}

// Event listener for the Track Attendance button
document.getElementById('trackAttendanceBtn').addEventListener('click', function() {
    document.getElementById('attendanceModal').style.display = 'flex';
});

// Close the modal
function closeModal() {
    document.getElementById('attendanceModal').style.display = 'none';
}

// Fetch users who marked attendance on the selected date
async function trackAttendance() {
    const date = document.getElementById('attendanceDate').value;

    if (!date) {
        alert('Please select a date');
        return;
    }

    try {
        const response = await fetch(`/api/attendance/track/${date}`);
        const users = await response.json();

        let resultsHTML = '<h3>Users Present on ' + date + ':</h3><ul>';
        users.forEach(user => {
            resultsHTML += `<li>${user.name} (${user.email})</li>`;
        });
        resultsHTML += '</ul>';

        document.getElementById('attendanceResults').innerHTML = resultsHTML;
    } catch (error) {
        console.error('Error fetching attendance data:', error);
        document.getElementById('attendanceResults').innerHTML = 'Error fetching attendance data.';
    }
}

