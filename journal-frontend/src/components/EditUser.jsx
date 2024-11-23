import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { editUserAPICall, getAllStaffAPICall, getAllCustomersAPICall } from '../services/AuthService';

const EditUser = () => {
    const navigate = useNavigate();
    const { username } = useParams();
    const [userData, setUserData] = useState({
        name: '',
        username: '',
        email: '',
        password: '' // Optional field for password change
    });
    const [loading, setLoading] = useState(true);
    const [emailError, setEmailError] = useState(''); // State to track email validation error

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                setLoading(true);
                const [staffResponse, customerResponse] = await Promise.all([
                    getAllStaffAPICall(),
                    getAllCustomersAPICall()
                ]);

                const allUsers = [...staffResponse.data, ...customerResponse.data];
                const user = allUsers.find(user => user.username === username);

                if (user) {
                    setUserData({
                        name: user.name,
                        username: user.username,
                        email: user.email,
                        password: '' // Password field starts empty
                    });
                } else {
                    alert('User not found');
                    navigate('/admin/users');
                }
            } catch (error) {
                console.error("Error fetching user data:", error);
                alert('Error loading user data');
                navigate('/admin/users');
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [username, navigate]);

    const validateEmail = (email) => {
        if (!email.includes('@') || !email.includes('.')) {
            setEmailError('Invalid email format. Email must contain "@" and ".".');
        } else {
            setEmailError('');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (emailError) {
            alert('Please fix email errors before submitting.');
            return;
        }

        try {
            await editUserAPICall(
                username,
                userData.username,
                userData.name,
                userData.email,
                userData.password || undefined // Only send password if it's been changed
            );
            alert('User updated successfully');
            navigate('/admin/users');
        } catch (error) {
            console.error("Error updating user:", error);
            alert('Failed to update user');
        }
    };

    const inputStyle = {
        width: '100%',
        padding: '8px',
        margin: '10px 0',
        border: '1px solid #ddd',
        borderRadius: '4px',
    };

    const buttonStyle = {
        padding: '10px 20px',
        margin: '5px',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
    };

    if (loading) {
        return <div style={{ padding: '20px' }}>Loading...</div>;
    }

    return (
        <div style={{ maxWidth: '500px', margin: '0 auto' }}>
		<br /> <br />
            <h2 style={{ marginBottom: '20px' }}>Edit User</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <input
                        type="text"
                        placeholder="Name"
                        value={userData.name}
                        onChange={(e) => setUserData({ ...userData, name: e.target.value })}
                        style={inputStyle}
                        required
                    />
                </div>
                <div>
                    <input
                        type="text"
                        placeholder="Username"
                        value={userData.username}
                        onChange={(e) => setUserData({ ...userData, username: e.target.value })}
                        style={inputStyle}
                        required
                    />
                </div>
                <div>
                    <input
                        type="email"
                        placeholder="Email"
                        value={userData.email}
                        onChange={(e) => {
                            const email = e.target.value;
                            setUserData({ ...userData, email });
                            validateEmail(email);
                        }}
                        style={inputStyle}
                        required
                    />
                    {emailError && <p style={{ color: 'red', marginTop: '-5px' }}>{emailError}</p>}
                </div>
                <div>
                    <input
                        type="password"
                        placeholder="New Password (Optional)"
                        value={userData.password}
                        onChange={(e) => setUserData({ ...userData, password: e.target.value })}
                        style={inputStyle}
                    />
                </div>
                <div>
                    <button
                        type="submit"
                        style={{
                            ...buttonStyle,
                            backgroundColor: '#4CAF50',
                            color: 'white',
                        }}
                    >
                        Save Changes
                    </button>
                    <button
                        type="button"
                        onClick={() => navigate('/admin/users')}
                        style={{
                            ...buttonStyle,
                            backgroundColor: '#f44336',
                            color: 'white',
                        }}
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditUser;
