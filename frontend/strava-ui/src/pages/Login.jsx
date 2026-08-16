import { useState } from 'react';
import api from '../api/axiosConfig';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState(null); // Holds our backend error message

    const handleLogin = async (e) => {
        e.preventDefault();
        setErrorMsg(null);

        try {
            // 1. Call the backend
            const response = await api.post('/auth/login', { email, password });

            // 2. Extract the token from the AuthResponse DTO
            const token = response.data.token;

            // 3. Save it (React Native: SecureStore.setItemAsync)
            localStorage.setItem('jwt', token);

            alert("Login successful! Token saved.");
            // Redirect to feed/dashboard...

        } catch (err) {
            // 4. Consume the Strict API Contract from Spring Boot
            if (err.response && err.response.data) {
                // This maps perfectly to our Java ApiErrorResponse record!
                setErrorMsg(err.response.data.message);
            } else {
                setErrorMsg("Network error. Is Spring Boot running?");
            }
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px' }}>
            <h2>Strava Clone - Login</h2>

            {/* Display our beautifully formatted backend error */}
            {errorMsg && <div style={{ color: 'red', marginBottom: '10px' }}>{errorMsg}</div>}

            <form onSubmit={handleLogin}>
                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div style={{ marginTop: '10px' }}>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" style={{ marginTop: '15px' }}>Login</button>
            </form>
        </div>
    );
}