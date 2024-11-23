import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addMarketerAPICall } from '../services/AuthService';

const AddMarketer = () => {
  const navigate = useNavigate();
  const [staffData, setStaffData] = useState({
    name: '',
    username: '',
    email: ''
  });
  const [emailError, setEmailError] = useState('');

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleEmailChange = (e) => {
    const email = e.target.value;
    setStaffData({ ...staffData, email });
    
    if (email && !validateEmail(email)) {
      setEmailError('Please enter a valid email address (must contain @ and .)');
    } else {
      setEmailError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateEmail(staffData.email)) {
      setEmailError('Please enter a valid email address (must contain @ and .)');
      return;
    }

    try {
      await addMarketerAPICall(staffData);
      alert('marketer added successfully');
      navigate('/admin/users');
    } catch (error) {
      console.error("Error adding marketer:", error);
      alert('Failed to add marketer');
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

  const errorStyle = {
    color: '#f44336',
    fontSize: '14px',
    marginTop: '4px',
  };

  return (
    <div style={{ padding: '50px', maxWidth: '500px', margin: '0 auto' }}>
      <h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: '#927d61', marginBottom: '20px' }}>
                WolfCafe
            </h1>
      <h2 style={{ marginBottom: '20px' }}>Add New Staff Member</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <input
            type="text"
            placeholder="Name"
            value={staffData.name}
            onChange={(e) => setStaffData({ ...staffData, name: e.target.value })}
            style={inputStyle}
            required
          />
        </div>
        <div>
          <input
            type="text"
            placeholder="Username"
            value={staffData.username}
            onChange={(e) => setStaffData({ ...staffData, username: e.target.value })}
            style={inputStyle}
            required
          />
        </div>
        <div>
          <input
            type="email"
            placeholder="Email"
            value={staffData.email}
            onChange={handleEmailChange}
            style={{
              ...inputStyle,
              borderColor: emailError ? '#f44336' : '#ddd',
            }}
            required
          />
          {emailError && <div style={errorStyle}>{emailError}</div>}
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
            Save
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

export default AddMarketer;