import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api/v1', // Your Spring Boot URL
    headers: {
        'Content-Type': 'application/json'
    }
});

// Request Interceptor: Attach the JWT before the request leaves the browser
api.interceptors.request.use(
    (config) => {
        // For React Native, use SecureStore.getItemAsync('jwt')
        const token = localStorage.getItem('jwt');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response Interceptor: Catch 401s globally
api.interceptors.response.use(
    (response) => response, // If success, just return the data
    (error) => {
        if (error.response && error.response.status === 401) {
            // Token expired or invalid. 
            console.error("JWT invalid, logging out...");
            localStorage.removeItem('jwt');
            window.location.href = '/login'; // Force redirect to login
        }
        return Promise.reject(error);
    }
);

export default api;