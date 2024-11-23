import React, { useState } from 'react';
import { registerAPICall } from '../services/AuthService';

const RegisterComponent = () => {
    const [name, setName] = useState('');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [emailError, setEmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [formErrors, setFormErrors] = useState({});

    const validateEmail = (email) => {
        if (!email.includes('@') || !email.includes('.')) {
            setEmailError('Invalid email format. Email must contain "@" and ".".');
            return false;
        } else {
            setEmailError('');
            return true;
        }
    };

    const validatePassword = (password) => {
        if (password.length < 8) {
            setPasswordError('Password must be at least 8 characters long.');
            return false;
        } else {
            setPasswordError('');
            return true;
        }
    };

    const validateEmptyFields = () => {
        const errors = {};
        
        if (!name.trim()) {
            errors.name = 'Name is required';
        }
        
        if (!username.trim()) {
            errors.username = 'Username is required';
        }
        
        if (!email.trim()) {
            errors.email = 'Email is required';
        }
        
        if (!password.trim()) {
            errors.password = 'Password is required';
        }
        
        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    function handleRegistrationForm(e) {
        e.preventDefault();

        // Reset any previous errors
        setFormErrors({});
        setEmailError('');
        setPasswordError('');

        // First check for empty fields
        const isEmptyFieldsValid = validateEmptyFields();
        
        // Then validate email and password
        const isEmailValid = validateEmail(email);
        const isPasswordValid = validatePassword(password);

        // Only proceed if all validations pass
        if (isEmptyFieldsValid && isEmailValid && isPasswordValid) {
            const register = { 
                name, 
                username, 
                email, 
                password 
            };

            console.log('Registering user:', register);

            registerAPICall(register)
                .then((response) => {
                    console.log('Registration response:', response.data);
                    alert('Registration successful!');
                    
                    // Optional: Reset form after successful registration
                    setName('');
                    setUsername('');
                    setEmail('');
                    setPassword('');
                    setFormErrors({});
                })
                .catch((error) => {
                    console.error('Registration error:', error);
                    
                    // Check if error response contains more details
                    if (error.response) {
                        console.error('Error details:', error.response.data);
                        alert(`Registration failed: ${error.response.data.message || 'Unknown error'}`);
                    } else {
                        alert('Registration failed. Please try again.');
                    }
                });
        } else {
            alert('Please fix the errors before submitting.');
        }
    }

    return (
        <div className="container">
            <br />
            <br />
            <div className="row">
                <div className="col-md-6 offset-md-3 offset-md-3">
                    <div className="card">
                        <div className="card-header">
                            <h2 className="text-center">Sign Up</h2>
                        </div>
                        <div className="card-body">
                            <form onSubmit={handleRegistrationForm}>
                                <div className="row mb-3">
                                    <label className="col-md-3 control-label">Name</label>
                                    <div className="col-md-9">
                                        <input
                                            type="text"
                                            name="name"
                                            className="form-control"
                                            placeholder="Enter name"
                                            value={name}
                                            onChange={(e) => setName(e.target.value)}
                                        />
                                        {formErrors.name && <p style={{ color: 'red' }}>{formErrors.name}</p>}
                                    </div>
                                </div>

                                <div className="row mb-3">
                                    <label className="col-md-3 control-label">Username</label>
                                    <div className="col-md-9">
                                        <input
                                            type="text"
                                            name="username"
                                            className="form-control"
                                            placeholder="Enter username"
                                            value={username}
                                            onChange={(e) => setUsername(e.target.value)}
                                        />
                                        {formErrors.username && <p style={{ color: 'red' }}>{formErrors.username}</p>}
                                    </div>
                                </div>

                                <div className="row mb-3">
                                    <label className="col-md-3 control-label">Email</label>
                                    <div className="col-md-9">
                                        <input
                                            type="text"
                                            name="email"
                                            className="form-control"
                                            placeholder="Enter email"
                                            value={email}
                                            onChange={(e) => {
                                                setEmail(e.target.value);
                                                validateEmail(e.target.value);
                                            }}
                                        />
                                        {formErrors.email && <p style={{ color: 'red' }}>{formErrors.email}</p>}
                                        {emailError && <p style={{ color: 'red' }}>{emailError}</p>}
                                    </div>
                                </div>

                                <div className="row mb-3">
                                    <label className="col-md-3 control-label">Password</label>
                                    <div className="col-md-9">
                                        <input
                                            type="password"
                                            name="password"
                                            className="form-control"
                                            placeholder="Enter password"
                                            value={password}
                                            onChange={(e) => {
                                                setPassword(e.target.value);
                                                validatePassword(e.target.value);
                                            }}
                                        />
                                        {formErrors.password && <p style={{ color: 'red' }}>{formErrors.password}</p>}
                                        {passwordError && <p style={{ color: 'red' }}>{passwordError}</p>}
                                    </div>
                                </div>

                                <div className="form-group mb-3">
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                    >
                                        Submit
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RegisterComponent;