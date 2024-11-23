import React, { useState, useContext, useEffect } from 'react';
import { 
    saveOrder, 
    updateOrder, 
    getOrderById, 
    purchaseOrder as purchaseOrderService, 
    getActiveOrder,
	deleteOrder 
} from '../services/OrderService';
import { isUserLoggedIn } from '../services/AuthService';
import { getTaxRate } from '../services/TaxService';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const CartComponent = () => {
    const [orderId, setOrderId] = useState(null);
    const [items, setItems] = useState([]);
    const [order, setOrder] = useState(null);
    const [paymentAmount, setPaymentAmount] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [addedItems, setAddedItems] = useState(new Set());
    const [taxRate, setTaxRate] = useState(0.0);
    const [tipPercentage, setTipPercentage] = useState(0); 
    const [customTip, setCustomTip] = useState(); // New state for custom tip

	const { backgroundColor } = useContext(BackgroundCustomizationContext);
	
    const loggedIn = isUserLoggedIn();

    useEffect(() => {
        if (!orderId) {
            fetchActiveOrder();
        }
    }, [orderId]);

    useEffect(() => {
        const fetchTaxRate = async () => {
            try {
                const rate = await getTaxRate();
                setTaxRate(rate);
            } catch (error) {
                console.error('Failed to fetch tax rate:', error);
            }
        };
        fetchTaxRate();
    }, []);

    const fetchActiveOrder = async () => {
        try {
            const activeOrder = await getActiveOrder();
            if (activeOrder) {
                setOrderId(activeOrder.id);
                setOrder(activeOrder);
                setItems(activeOrder.items);
            }
        } catch (error) {
            console.error('Failed to fetch active order:', error);
        }
    };

    const calculateSubtotal = () => {
        return items.reduce((total, item) => total + (item.amount * item.price), 0).toFixed(2);
    };

    const calculateTaxAmount = () => {
        const subtotal = parseFloat(calculateSubtotal());
        return (subtotal * taxRate).toFixed(2);
    };

	const calculateTipAmount = () => {
	    const subtotal = parseFloat(calculateSubtotal());
	    if (tipPercentage === 'custom') {
	        return (customTip || 0).toFixed(2); // Ensure valid decimal format
	    }
	    return ((subtotal * parseFloat(tipPercentage)) / 100).toFixed(2);
	};


    const calculateTotal = () => {
        const subtotal = parseFloat(calculateSubtotal());
        const taxAmount = parseFloat(calculateTaxAmount());
        const tipAmount = parseFloat(calculateTipAmount());
        return (subtotal + taxAmount + tipAmount).toFixed(2);
    };

    const handlePurchaseOrder = async () => {
        const totalAmount = parseFloat(calculateTotal());
        const paidAmount = parseFloat(paymentAmount);

        if (paidAmount < totalAmount) {
            setErrorMessage(`Insufficient payment. Please pay at least $${totalAmount.toFixed(2)}.`);
            return;
        }

        if (order) {
            try {
                const response = await purchaseOrderService(paymentAmount);

                const change = (paidAmount - totalAmount).toFixed(2);
                alert(`Order successfully paid and confirmed! Your change is $${change}.`);

                setOrder(null);
                setItems([]);
                setErrorMessage('');
                setPaymentAmount('');
                await fetchActiveOrder(); 
            } catch (error) {
                console.error('Failed to process the purchase', error);
                alert('Error processing the order. Please try again later.');
            }
        }
    };
	
	const removeItem = async (item) => {
		       if (order) {
		           const updatedItems = items.filter((i) => i.id !== item.id);
		           const updatedOrder = { ...order, items: updatedItems };
		           try {
		               await updateOrder(orderId, updatedOrder);
		               setItems(updatedItems);
		               setOrder(updatedOrder);
		               setAddedItems((prevAddedItems) => {
		                   const newAddedItems = new Set(prevAddedItems);
		                   newAddedItems.delete(item.id);
		                   return newAddedItems;
		               });
		               await fetchActiveOrder();
		               alert(`${item.name} has been removed from your order.`);
		           } catch (error) {
		               console.error('Failed to remove item from order', error);
		           }
				   if (updatedItems.length === 0) {
				                   // If the last item is removed, delete the entire order
				                   try {
				                       await deleteOrder(order.id); // Call deleteOrder with the order ID
				                       setItems([]); // Clear the items from the cart
				                       setOrder(null); // Clear the order state
				                       setOrderId(null); // Reset the order ID
				                       alert('Your order has been deleted as it was the last item.');
				                   } catch (error) {
				                       console.error('Failed to delete order:', error);
				                   }
				               } else {
				                   // Otherwise, update the order with the remaining items
				                   const updatedOrder = { ...order, items: updatedItems };
				                   try {
				                       await editActiveOrder(updatedOrder); // Update the order
				                       setItems(updatedItems); // Update the state with remaining items
				                       setOrder(updatedOrder); // Update the order state
				                       alert(`${item.name} has been removed from your order.`);
				                   } catch (error) {
				                       console.error('Failed to remove item from order', error);
				                   }
				               }
		       }
		   };


    const buttonStyles = {
        base: {
            padding: '10px 20px',
            borderRadius: '5px',
            fontFamily: 'Georgia, serif',
            fontSize: '16px',
            cursor: 'pointer',
            transition: 'background-color 0.3s',
        },
        remove: {
            backgroundColor: '#d9534f',
            color: '#fff',
            border: 'none',
        },
        purchase: {
            backgroundColor: '#927d61',
            color: '#fff',
            border: 'none',
        },
        hoverRemove: {
            backgroundColor: '#c9302c',
        },
        hoverPurchase: {
            backgroundColor: '#755c47',
        },
    };

    return (
        <div className='container'>
            <br />
            <br />
            <h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
                WolfCafe
            </h1>
            <br />
            <h2 className='text-center'>Current Order</h2>
            <div>
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
                        {items.map((item) => (
                            <tr key={item.id}>
                                <td>{item.name}</td>
                                <td>{item.description}</td>
                                <td>{item.price}</td>
                                <td>{item.amount}</td>
                                <td>
                                    {loggedIn && (
                                        <button
                                        style={{
                                            ...buttonStyles.base,
                                            ...buttonStyles.remove,
                                        }}
                                        onMouseOver={(e) => (e.target.style.backgroundColor = buttonStyles.hoverRemove.backgroundColor)}
                                            onMouseOut={(e) => (e.target.style.backgroundColor = buttonStyles.remove.backgroundColor)}
                                            className={`btn ${addedItems.has(item.id) ? 'btn-success' : 'btn-primary'}`}
                                            onClick={() => removeItem(item)}
                                        >
                                            Remove
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

				<div className="order-summary">
				    <table className="table table-bordered" style={{ fontSize: '0.9em' }}>
				        <tbody>
				            <tr>
				                <td>Subtotal</td>
				                <td>${calculateSubtotal()}</td>
				            </tr>
				            <tr>
				                <td>Tax ({(taxRate * 100).toFixed(2)}%)</td>
				                <td>${calculateTaxAmount()}</td>
				            </tr>
				            <tr>
				                <td>Tip</td>
				                <td>${calculateTipAmount()}</td>
				            </tr>
				        </tbody>
				    </table>
				    <h4>Total: ${calculateTotal()}</h4>
				</div>


                <div className="tip-selection">
                    <label htmlFor="tip">Select Tip:</label>
                    <select 
                        id="tip" 
                        value={tipPercentage} 
                        onChange={(e) => setTipPercentage(e.target.value)}
                        className="form-control"
                    >
                        <option value="0">No Tip</option>
                        <option value="10">10%</option>
                        <option value="15">15%</option>
                        <option value="20">20%</option>
                        <option value="custom">Custom</option>
                    </select>
					{tipPercentage === 'custom' && (
					    <input
					        type="number"
					        step="0.01" // Allows decimal step increments
					        placeholder="Enter custom tip amount"
					        value={customTip}
					        onChange={(e) => setCustomTip(parseFloat(e.target.value) || 0)}
					        className="form-control"
					        style={{ marginTop: '10px' }}
					    />
					)}
                </div>

                <div className="payment-section">
                    <input
                        type="number"
                        value={paymentAmount}
                        onChange={(e) => setPaymentAmount(e.target.value)}
                        placeholder="Enter amount to pay"
                        className="form-control"
                    />
                    {errorMessage && <div style={{ color: 'red', marginTop: '10px' }}>{errorMessage}</div>}
                </div>
                    <br/>
                <button className='btn btn-primary' onClick={handlePurchaseOrder} style={{
                        ...buttonStyles.base,
                        ...buttonStyles.purchase,
                    }}
                    onMouseOver={(e) => (e.target.style.backgroundColor = buttonStyles.hoverPurchase.backgroundColor)}
                    onMouseOut={(e) => (e.target.style.backgroundColor = buttonStyles.purchase.backgroundColor)}
                    >
                    Purchase Order
                </button>
                <br/>
            </div>
        </div>
    );
};

export default CartComponent;