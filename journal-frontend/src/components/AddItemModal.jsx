import React, { useState } from 'react';
import { addItem } from '../services/InventoryService';
import { useNavigate } from 'react-router-dom';

const AddItemModal = () => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [price, setPrice] = useState('');
    const [amount, setAmount] = useState('');
    const navigate = useNavigate();

    const handleSave = async (e) => {
        e.preventDefault();
        const newItem = { name, description, price: parseFloat(price), amount: parseInt(amount, 10) };

        try {
            await addItem(newItem);
            alert('Item added successfully');
            navigate('/admin/inventory');  // Redirect to the inventory page after saving
        } catch (error) {
            console.error('Error adding item:', error);
        }
    };

    return (
        <div className='container'>
            <h2 className='text-center'>Add New Item</h2>
            <form onSubmit={handleSave}>
                <div className='form-group'>
                    <label>Item Name</label>
                    <input
                        type='text'
                        className='form-control'
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div className='form-group'>
                    <label>Description</label>
                    <input
                        type='text'
                        className='form-control'
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                </div>
                <div className='form-group'>
                    <label>Price</label>
                    <input
                        type='number'
                        className='form-control'
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        required
                        min='0'
                        step='0.01'
                    />
                </div>
                <div className='form-group'>
                    <label>Amount</label>
                    <input
                        type='number'
                        className='form-control'
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        required
                        min='0'
                    />
                </div>
                <div className='d-flex justify-content-center mt-3'>
                    <button type='submit' className='btn btn-success'>
                        Save
                    </button>
                </div>
            </form>
        </div>
    );
};

export default AddItemModal;
