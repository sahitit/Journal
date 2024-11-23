import React, { useState, useContext, useEffect } from 'react';
import { getAllItems, updateItem, deleteItemById } from '../services/ItemService';
import { isUserLoggedIn } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const InventoryComponent = () => {
    const [items, setItems] = useState([]);
    const [updatedItems, setUpdatedItems] = useState({});
    const loggedIn = isUserLoggedIn();
    const navigate = useNavigate();
	
	const { backgroundColor } = useContext(BackgroundCustomizationContext);

    useEffect(() => {
        listItems();
    }, []);

    const listItems = () => {
        getAllItems()
            .then(response => setItems(response.data))
            .catch(error => console.error(error));
    };

    const handleUpdate = (id, field, value) => {
        setUpdatedItems(prev => ({
            ...prev,
            [id]: { ...prev[id], [field]: value }
        }));
    };

    const saveChanges = async () => {
        const updates = Object.entries(updatedItems).map(([id, item]) =>
            updateItem(id, item)
        );
        try {
            await Promise.all(updates);
            alert('Changes saved successfully');
            listItems();
        } catch (error) {
            console.error('Error saving changes:', error);
        }
    };

	const deleteItem = async (id) => {
	    try {
	        const response = await deleteItemById(id);
	        if (response.status === 200) {
	            setItems(prevItems => prevItems.filter(item => item.id !== id)); // Update local state
	            alert('Item deleted successfully');
	        }
	    } catch (error) {
	        console.error('Error deleting item:', error.response?.data || error.message);
	        alert('Error deleting item. Please try again.');
	    }
	};



    return (
        <div className='container' >
		<br /> <br />
		<h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
		    WolfCafe
		</h1>
            <h2 className='text-center'>Inventory</h2>
            <table className='table table-bordered table-striped'>
                <thead>
                    <tr>
                        <th>Item Name</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Amount</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {items.map(item => (
                        <tr key={item.id}>
                            <td>{item.name}</td>
                            <td>{item.description}</td>
                            <td>
                                <input
                                    type="number"
                                    min="0"
                                    value={updatedItems[item.id]?.price || item.price}
                                    onChange={(e) =>
                                        handleUpdate(item.id, 'price', parseFloat(e.target.value))
                                    }
                                    className="form-control"
                                    style={{ width: "80px" }}
                                />
                            </td>
                            <td>
                                <input
                                    type="number"
                                    min="0"
                                    value={updatedItems[item.id]?.amount || item.amount}
                                    onChange={(e) =>
                                        handleUpdate(item.id, 'amount', parseInt(e.target.value, 10))
                                    }
                                    className="form-control"
                                    style={{ width: "80px" }}
                                />
                            </td>
                            <td>
                                {loggedIn && (
                                    <button
                                        className="btn btn-danger"
                                        onClick={() => deleteItem(item.id)}
                                    >
                                        Delete
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <div className='d-flex justify-content-center mt-3'>
                <button
                    className='btn btn-primary mr-2'
                    onClick={() => navigate('/admin/inventory/add-item')}
                >
                    Add Item
                </button>
                <button
                    className='btn btn-success ml-2'
                    onClick={saveChanges}
                >
                    Save
                </button>
            </div>
        </div>
    );
};

export default InventoryComponent;