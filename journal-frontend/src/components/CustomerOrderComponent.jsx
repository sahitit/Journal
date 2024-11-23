import React, { useState, useContext, useEffect } from 'react';
import { getAllItems } from '../services/ItemService';
import { saveOrder, updateOrder, getAllOrders } from '../services/OrderService';
import { pickupOrder, getFulfilledOrders } from '../services/MakeOrderService';
import { isUserLoggedIn } from '../services/AuthService';
import { OrderDto, ItemDto } from '../services/dtos';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const CustomerOrderComponent = () => {
    const [items, setItems] = useState([]);
    const [order, setOrder] = useState(null);
    const [orderId, setOrderId] = useState(null);
    const [fulfilledOrders, setFulfilledOrders] = useState([]);
    const [addedItems, setAddedItems] = useState(new Set());
    const [quantities, setQuantities] = useState({});
    const loggedIn = isUserLoggedIn();

	const { backgroundColor } = useContext(BackgroundCustomizationContext);
	
    useEffect(() => {
        const storedOrder = localStorage.getItem('order');
        if (storedOrder) {
            setOrder(JSON.parse(storedOrder)); 
            setOrderId(JSON.parse(storedOrder).id);
        }

        const fetchData = async () => {
            await fetchOrders();
            await fetchFulfilledOrders();
        };
        fetchData();
        listItems();

        const storedAddedItems = JSON.parse(localStorage.getItem('addedItems') || '[]');
        setAddedItems(new Set(storedAddedItems));
    }, []);

    useEffect(() => {
        if (order) {
            localStorage.setItem('order', JSON.stringify(order)); // Save order in localStorage
        }
    }, [order]);

    useEffect(() => {
        localStorage.setItem('addedItems', JSON.stringify(Array.from(addedItems)));
    }, [addedItems]);

    const listItems = () => {
        getAllItems()
            .then((response) => {
                setItems(response.data);
            })
            .catch((error) => {
                console.error(error);
            });
    };

    const fetchOrders = async () => {
        console.log("fetching orders")
        try {
            const allOrders = await getAllOrders();
            console.log("All order response", allOrders)
            if (allOrders.length > 0) {
                const activeOrder = allOrders.find(
                    (order) => order.status !== 'PICKED_UP'
                );
                if (activeOrder) {
                    console.log("Order fetched: " + activeOrder.name)
                    setOrder(activeOrder);
                    setOrderId(activeOrder.id);
                } else {
                    console.log("No current order");
                    setOrder(null);
                    setOrderId(null);
                }
            }
            else {
                console.log("No order found")
            }
        } catch (error) {
            console.error('Failed to fetch orders:', error);
            setOrder(null);
            setOrderId(null);
        }
    };

    const fetchFulfilledOrders = async () => {
        try {
            const response = await getFulfilledOrders();
            if (response?.data?.length > 0) {
                setFulfilledOrders(response.data.map((order) => order.id));
            } else {
                setFulfilledOrders([]);
            }
        } catch (error) {
            console.error('Failed to fetch fulfilled orders:', error);
            setFulfilledOrders([]);
        }
    };

    const handlePickup = async () => {
        if (orderId && fulfilledOrders.includes(orderId)) {
            try {
                await pickupOrder(orderId);
                console.log('Order picked up successfully.');
                alert(`Order picked up! Enjoy!`);
                fetchOrders();
                fetchFulfilledOrders();
            } catch (error) {
                console.error('Failed to pick up order:', error);
            }
        }
    };

    const handleQuantityChange = (itemId, quantity) => {
        setQuantities((prev) => ({ ...prev, [itemId]: quantity }));
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

    return (
        <div className="container">
            <br />
            <br />
			<h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
			    WolfCafe
			</h1>
            <br />
            <h3>Your Order</h3>
            <br />
            {/* Render the item list only if the order is null or its status is 'ACTIVE' */}
            {(order === null || order.status === 'ACTIVE') && (
                
                <table className="table table-bordered table-striped">
                    
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
                        {items.map((item) => (
                            <tr key={item.id}>
                                <td>{item.name}</td>
                                <td>{item.description}</td>
                                <td>{item.price}</td>
                                <td>
                                    <input
                                        type="number"
                                        min="1"
                                        value={quantities[item.id] || 1}
                                        onChange={(e) =>
                                            handleQuantityChange(
                                                item.id,
                                                parseInt(e.target.value)
                                            )
                                        }
                                        className="form-control"
                                        style={{ width: '80px' }}
                                    />
                                </td>
                                <td>
                                    {loggedIn && (
                                        <button
                                        className={`btn ${
                                            addedItems.has(item.id) ? 'btn-success' : ''
                                        }`}
                                        onClick={() => addItemToOrder(item)}
                                        disabled={addedItems.has(item.id)}
                                        style={{
                                            marginLeft: '10px',
                                            padding: '10px 20px',
                                            backgroundColor: addedItems.has(item.id) ? '#ccc' : '#927d61',
                                            color: addedItems.has(item.id) ? '#5a4636' : '#fff',
                                            border: 'none',
                                            borderRadius: '5px',
                                            fontFamily: 'Georgia, serif',
                                            fontSize: '16px',
                                            cursor: addedItems.has(item.id) ? 'not-allowed' : 'pointer',
                                            transition: 'background-color 0.3s',
                                        }}
                                        onMouseOver={(e) => {
                                            if (!addedItems.has(item.id)) {
                                                e.target.style.backgroundColor = '#755c47';
                                            }
                                        }}
                                        onMouseOut={(e) => {
                                            if (!addedItems.has(item.id)) {
                                                e.target.style.backgroundColor = '#927d61';
                                            }
                                        }}
                                    >
                                        {addedItems.has(item.id) ? 'Added' : 'Add to Order'}
                                    </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

{order && (
    <div>
        {order.status === 'PURCHASED' && (
            <div
                style={{
                    backgroundColor: '#f4e3d1',
                    color: '#5a4636',
                    padding: '15px',
                    borderRadius: '8px',
                    marginTop: '20px',
                    fontFamily: 'Georgia, serif',
                    boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                    textAlign: 'center',
                }}
            >
                <h4 style={{ margin: '0', fontSize: '18px', fontWeight: 600 }}>
                    Your order is being prepared.
                </h4>
            </div>
        )}
        {order.status === 'FULFILLED' && fulfilledOrders.includes(orderId) && (
            <div
                style={{
                    backgroundColor: '#e6d6c4',
                    color: '#4a372b',
                    padding: '15px',
                    borderRadius: '8px',
                    marginTop: '20px',
                    fontFamily: 'Georgia, serif',
                    boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                    textAlign: 'center',
                }}
            >
                <h4 style={{ margin: '0', fontSize: '18px', fontWeight: 600 }}>
                    Your order is ready for pickup!
                </h4>
                <button
                    style={{
                        marginTop: '10px',
                        padding: '10px 20px',
                        backgroundColor: '#927d61',
                        color: '#fff',
                        border: 'none',
                        borderRadius: '5px',
                        fontFamily: 'Georgia, serif',
                        fontSize: '16px',
                        cursor: 'pointer',
                        transition: 'background-color 0.3s',
                    }}
                    onClick={handlePickup}
                    onMouseOver={(e) => (e.target.style.backgroundColor = '#755c47')}
                    onMouseOut={(e) => (e.target.style.backgroundColor = '#927d61')}
                >
                    Pick Up Order
                </button>
            </div>
        )}
    </div>
)}
        </div>
    );
};

export default CustomerOrderComponent;