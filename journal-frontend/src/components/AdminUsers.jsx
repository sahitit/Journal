import React, { useEffect, useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
    getAllStaffAPICall, 
    getAllCustomersAPICall, 
    getAllMarketersAPICall,
    deleteUserAPICall 
} from '../services/AuthService';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const AdminUsers = () => {
    const [staffList, setStaffList] = useState([]);
    const [customerList, setCustomerList] = useState([]);
    const [marketerList, setMarketerList] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

	const { backgroundColor } = useContext(BackgroundCustomizationContext);
	
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setLoading(true);

                const staffResponse = await getAllStaffAPICall();
                setStaffList(staffResponse.data);

                const customerResponse = await getAllCustomersAPICall();
                setCustomerList(customerResponse.data);

                const marketerResponse = await getAllMarketersAPICall();
                setMarketerList(marketerResponse.data);

            } catch (error) {
                console.error("Error fetching users", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, []);

    const handleDeleteUser = async (username) => {
        try {
            await deleteUserAPICall(username);
            // Update all lists using username
            setStaffList((prevList) => prevList.filter((user) => user.username !== username));
            setCustomerList((prevList) => prevList.filter((user) => user.username !== username));
            setMarketerList((prevList) => prevList.filter((user) => user.username !== username));
            alert("User deleted successfully.");
        } catch (error) {
            console.error("Error deleting user", error);
            alert("Failed to delete user. Please try again.");
        }
    };

    const tableStyle = {
        width: '100%',
        borderCollapse: 'collapse',
        marginBottom: '20px',
    };

    const thStyle = {
        padding: '10px',
        borderBottom: '1px solid #ddd',
        textAlign: 'left',
        backgroundColor: '#f4f4f4',
    };

    const tdStyle = {
        padding: '10px',
        borderBottom: '1px solid #ddd',
        textAlign: 'left',
    };

    const buttonStyle = {
        padding: '5px 10px',
        margin: '0 5px',
        border: 'none',
        borderRadius: '3px',
        cursor: 'pointer',
    };

    const editButtonStyle = {
        ...buttonStyle,
        backgroundColor: '#4CAF50',
        color: 'white',
    };

    const deleteButtonStyle = {
        ...buttonStyle,
        backgroundColor: '#f44336',
        color: 'white',
    };

    const sectionStyle = {
        marginBottom: '40px',
    };

    return (
        <div className="admin-users" style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
            <br></br>
            <h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
                WolfCafe
            </h1>
            {loading ? (
                <p>Loading users...</p>
            ) : (
                <>
					{/* Staff Section */}
                    <div className="staff-section" style={sectionStyle}>
                        <h3>Staff Users</h3>
                        <button
                            onClick={() => navigate('/admin/add-staff')}
                            style={{
                                ...buttonStyle,
                                backgroundColor: '#007BFF',
                                color: 'white',
                                marginBottom: '10px',
                            }}
                        >
                            Add Staff
                        </button>
                        <table style={tableStyle}>
                            <thead>
                                <tr>
                                    <th style={thStyle}>Name</th>
                                    <th style={thStyle}>Username</th>
                                    <th style={thStyle}>Email</th>
                                    <th style={thStyle}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {staffList.map((staff) => (
                                    <tr key={staff.username}>
                                        <td style={tdStyle}>{staff.name}</td>
                                        <td style={tdStyle}>{staff.username}</td>
                                        <td style={tdStyle}>{staff.email}</td>
                                        <td style={tdStyle}>
                                            <button
                                                onClick={() => navigate(`/admin/edit-user/${staff.username}`)}
                                                style={editButtonStyle}
                                            >
                                                Edit
                                            </button>
                                            <button
                                                onClick={() => handleDeleteUser(staff.username)}
                                                style={deleteButtonStyle}
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
					
					{/* Marketer Section */}
					<div className="marketer-section" style={sectionStyle}>
					    <h3>Marketer Users</h3>
					    <button
					        onClick={() => navigate('/admin/add-marketer')} // Navigate to marketer creation
					        style={{
					            ...buttonStyle,
					            backgroundColor: '#FF9900',
					            color: 'white',
					            marginBottom: '10px',
					        }}
					    >
					        Add Marketer
					    </button>
					    <table style={tableStyle}>
					        <thead>
					            <tr>
					                <th style={thStyle}>Name</th>
					                <th style={thStyle}>Username</th>
					                <th style={thStyle}>Email</th>
					                <th style={thStyle}>Actions</th>
					            </tr>
					        </thead>
					        <tbody>
					            {marketerList.map((marketer) => (
					                <tr key={marketer.username}>
					                    <td style={tdStyle}>{marketer.name}</td>
					                    <td style={tdStyle}>{marketer.username}</td>
					                    <td style={tdStyle}>{marketer.email}</td>
					                    <td style={tdStyle}>
					                        <button
					                            onClick={() => navigate(`/admin/edit-user/${marketer.username}`)}
					                            style={editButtonStyle}
					                        >
					                            Edit
					                        </button>
					                        <button
					                            onClick={() => handleDeleteUser(marketer.username)}
					                            style={deleteButtonStyle}
					                        >
					                            Delete
					                        </button>
					                    </td>
					                </tr>
					            ))}
					        </tbody>
					    </table>
					</div>
					
					{/* Customer Section */}
                    <div className="customer-section" style={sectionStyle}>
                        <h3>Customer Users</h3>
                        <table style={tableStyle}>
                            <thead>
                                <tr>
                                    <th style={thStyle}>Name</th>
                                    <th style={thStyle}>Username</th>
                                    <th style={thStyle}>Email</th>
                                    <th style={thStyle}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {customerList.map((customer) => (
                                    <tr key={customer.username}>
                                        <td style={tdStyle}>{customer.name}</td>
                                        <td style={tdStyle}>{customer.username}</td>
                                        <td style={tdStyle}>{customer.email}</td>
                                        <td style={tdStyle}>
                                            <button
                                                onClick={() => navigate(`/admin/edit-user/${customer.username}`)}
                                                style={editButtonStyle}
                                            >
                                                Edit
                                            </button>
                                            <button
                                                onClick={() => handleDeleteUser(customer.username)}
                                                style={deleteButtonStyle}
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </>
            )}
        </div>
    );
};

export default AdminUsers;