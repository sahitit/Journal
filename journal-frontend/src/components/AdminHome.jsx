import React, { useState, useContext, useEffect } from 'react';
import { getAllItems } from '../services/ItemService';
import { saveOrder, updateOrder } from '../services/OrderService';
import { getTaxRate, updateTaxRate } from '../services/TaxService'; // Import tax-related services
import { OrderDto } from '../services/dtos';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';
import { isUserLoggedIn } from '../services/AuthService';
import { ItemDto } from '../services/dtos'; 


/**
 * This component allows customers to create an order by adding items from a list.
 * Each item in the list has an "Add to Order" button and quantity input, and the order is saved
 * each time an item is added. 
 * 
 * @author: Gabriel Perri
 */
const AdminHome = () => {
    const [items, setItems] = useState([]);
    const [order, setOrder] = useState(null);
    const [orderId, setOrderId] = useState(null);
    const [currentTaxRate, setCurrentTaxRate] = useState(null); // State for current tax rate
    const [newTaxRate, setNewTaxRate] = useState(''); // State for inputting new tax rate as a percentage
    const [taxError, setTaxError] = useState(''); // State for tax rate update errors

	const { backgroundColor } = useContext(BackgroundCustomizationContext);
	
    // Tracks added items so button can turn green
    const [addedItems, setAddedItems] = useState(new Set());
    const [quantities, setQuantities] = useState({});  // Stores quantities for each item

    const loggedIn = isUserLoggedIn();

    // Get items and tax rate when component is opened
    useEffect(() => {
        listItems();
        fetchCurrentTaxRate();
    }, []);

    function listItems() {
        getAllItems().then((response) => {
            setItems(response.data);
        }).catch(error => {
            console.error(error);
        });
    }

    const fetchCurrentTaxRate = async () => {
        try {
            const taxRate = await getTaxRate();
            setCurrentTaxRate(taxRate);
        } catch (error) {
            console.error('Failed to fetch tax rate:', error);
        }
    };

    const handleQuantityChange = (itemId, quantity) => {
        setQuantities(prev => ({ ...prev, [itemId]: quantity }));
    };

	const addItemToOrder = async (item) => {
	    const quantity = quantities[item.id] || 1;
	    const itemWithQuantity = {
	        id: item.id,
	        name: item.name,
	        amount: quantity,
	        price: item.price,
	    };

	    const updatedItem = new ItemDto(item.name, quantity, item.price);

	    if (orderId) {
	        // If order exists, add the item
	        const updatedOrder = new OrderDto(orderId, order.name, [...order.items, updatedItem], order.status);
	        try {
	            const savedOrder = await updateOrder(orderId, updatedOrder);
	            setOrder(savedOrder);
	            console.log('Order updated successfully');
	        } catch (error) {
	            console.error('Failed to update order:', error);
	        }
	    } else {
	        // If no order, create one and add the new item
	        const currentDate = new Date();
	        const formattedDate = currentDate.toISOString().replace(/[-T:.Z]/g, '').slice(0, 15);
	        const dateName = `Order-${formattedDate}`;

	        const newOrder = new OrderDto(null, dateName, [updatedItem], 'ACTIVE');
	        try {
	            const savedOrder = await saveOrder(newOrder);
	            setOrder(savedOrder);
	            setOrderId(savedOrder.id);
	            console.log('Order created successfully');
	        } catch (error) {
	            console.error('Failed to create order:', error);
	        }
	    }

	    // Add item to addedItems set
	    setAddedItems(prev => new Set(prev).add(item.id));
	};

    const handleTaxRateUpdate = async () => {
        setTaxError(''); // Reset error message
        if (!newTaxRate || isNaN(newTaxRate) || parseFloat(newTaxRate) < 0) {
            setTaxError('Please enter a valid positive tax rate.');
            return;
        }

        try {
            const decimalTaxRate = parseFloat(newTaxRate) / 100; // Convert percentage to decimal
            await updateTaxRate(decimalTaxRate);
            setCurrentTaxRate(decimalTaxRate);
            alert('Tax rate updated successfully!');
            setNewTaxRate(''); // Clear the input field
        } catch (error) {
            console.error('Failed to update tax rate:', error);
            setTaxError('Failed to update tax rate. Please try again.');
        }
    };

    return (
        <div className='container'>
            <br /> <br />
            <h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
                WolfCafe
            </h1>
            <h2 className='text-center'>Add To Your Order!</h2>
            <div>
                <table className='table table-bordered table-striped'>
                    <thead>
                        <tr>
                            <th>Item Name</th>
                            <th>Description</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            items.map((item) => (
                                <tr key={item.id}>
                                    <td>{item.name}</td>
                                    <td>{item.description}</td>
                                    <td>{item.price}</td>
                                    <td>
                                        <input 
                                            type="number" 
                                            min="1" 
                                            value={quantities[item.id] || 1} 
                                            onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value))}
                                            className="form-control"
                                            style={{ width: "80px" }}
                                        />
                                    </td>
                                    <td>
                                        {
                                            loggedIn && (
                                                <button 
                                                    className={`btn ${addedItems.has(item.id) ? 'btn-success' : 'btn-primary'}`} 
                                                    onClick={() => addItemToOrder(item)} 
                                                    style={{ marginLeft: "10px" }}
                                                >
                                                    {addedItems.has(item.id) ? 'Added' : 'Add to Order'}
                                                </button>
                                            )
                                        }
                                    </td>
                                </tr>
                            ))
                        }
                    </tbody>
                </table>
            </div>

            {/* Admin Actions Section */}
            <div>
                <h3>Admin Actions</h3>
                <div className="row align-items-center">
                    <div className="col">
                        <p style={{ color: 'black'}}>Current Tax Rate: {currentTaxRate !== null ? `${(currentTaxRate * 100).toFixed(2)}%` : 'Loading...'}</p>
                    </div>
                    <div className="col">
                        <input
                            type="number"
                            step="1"
                            value={newTaxRate}
                            onChange={(e) => setNewTaxRate(e.target.value)}
                            placeholder="Enter new tax rate (%)"
                            className="form-control"
                        />
                    </div>
                    <div className="col">
                        <button className="btn btn-primary" onClick={handleTaxRateUpdate}>
                            Update Tax Rate
                        </button>
                    </div>
                </div>
                {taxError && <p style={{ color: 'red', marginTop: '10px' }}>{taxError}</p>}
            </div>
        </div>
    );
};

export default AdminHome;
