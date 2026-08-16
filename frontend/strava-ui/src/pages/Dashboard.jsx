import { useState } from 'react';
import api from '../api/axiosConfig';

export default function Dashboard() {
    const [profileData, setProfileData] = useState(null);

    const fetchProfile = async () => {
        try {
            // Notice we do NOT pass the JWT here. 
            // The Axios Interceptor handles it invisibly!
            const response = await api.get('/profiles/me');
            setProfileData(response.data);
        } catch (err) {
            console.error("Failed to fetch profile", err);
        }
    };

    return (
        <div>
            <h2>Dashboard</h2>
            <button onClick={fetchProfile}>Fetch My Profile</button>
            {profileData && <pre>{JSON.stringify(profileData, null, 2)}</pre>}
        </div>
    );
}