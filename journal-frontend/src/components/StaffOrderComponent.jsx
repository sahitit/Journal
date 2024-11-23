import React, { useState, useContext, useEffect } from 'react';
import { fulfillOrder, getOrdersToFulfill, getFulfilledOrders } from '../services/MakeOrderService';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const StaffOrderComponent = () => {
  const [activeTab, setActiveTab] = useState('displayOrder');
  const [ordersToFulfill, setOrdersToFulfill] = useState([]);
  const [fulfilledOrders, setFulfilledOrders] = useState([]);
  const [error, setError] = useState('');
  
  const { backgroundColor } = useContext(BackgroundCustomizationContext);

  useEffect(() => {
    if (activeTab === 'displayOrder') {
      loadOrdersToFulfill();
    } else if (activeTab === 'fulfilledOrders') {
      loadFulfilledOrders();
    }
  }, [activeTab]);

  const loadOrdersToFulfill = async () => {
    try {
      const response = await getOrdersToFulfill();
      setOrdersToFulfill(response.data);
    } catch (err) {
      setError('Failed to load orders to fulfill.');
    }
  };

  const loadFulfilledOrders = async () => {
    try {
      const response = await getFulfilledOrders();
      setFulfilledOrders(response.data);
    } catch (err) {
      setError('Failed to load fulfilled orders.');
    }
  };

  const handleFulfillOrder = async (orderId) => {
    try {
      await fulfillOrder(orderId);
      loadOrdersToFulfill();
      loadFulfilledOrders();
    } catch (err) {
      console.error('Error fulfilling order:', err);
      if (err.response) {
        setError(`Failed to fulfill order: ${err.response.data.message || 'Unknown error'}`);
      } else {
        setError('Failed to fulfill order.');
      }
    }
  };


  // Inline CSS styles
  const styles = {
    tableContainer: {
      width: '80%',
      margin: '20px auto', // Center-align the table
      borderCollapse: 'collapse',
      textAlign: 'center', // Center-align text inside table cells
    },
    tableHeader: {
      backgroundColor: '#f4f4f4',
      fontWeight: 'bold',
    },
    tableCell: {
      border: '1px solid #ddd',
      padding: '10px',
    },
    error: {
      color: 'red',
      textAlign: 'center',
      marginBottom: '10px',
    },
    tabs: {
      textAlign: 'center',
      marginBottom: '20px',
    },
    tabButton: {
      padding: '10px 20px',
      margin: '5px',
      cursor: 'pointer',
      border: '1px solid #007bff',
      backgroundColor: '#f1f1f1',
      color: '#007bff',
      borderRadius: '4px',
    },
    activeTabButton: {
      backgroundColor: '#007bff',
      color: 'white',
    },
    button: {
      padding: '5px 10px',
      backgroundColor: '#007bff',
      color: 'white',
      border: 'none',
      borderRadius: '4px',
      cursor: 'pointer',
    },
    buttonHover: {
      backgroundColor: '#0056b3',
    },
  };

  return (
    <div>
	
	<br /> <br />
	<h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
	    WolfCafe
	</h1>
      <h2>Staff Management</h2>
      {error && <div style={styles.error}>{error}</div>}

      <div style={styles.tabs}>
        <button
          style={{
            ...styles.tabButton,
            ...(activeTab === 'displayOrder' ? styles.activeTabButton : {}),
          }}
          onClick={() => setActiveTab('displayOrder')}
        >
          Orders to Fulfill
        </button>
        <button
          style={{
            ...styles.tabButton,
            ...(activeTab === 'fulfilledOrders' ? styles.activeTabButton : {}),
          }}
          onClick={() => setActiveTab('fulfilledOrders')}
        >
          Fulfilled Orders
        </button>
      </div>

      {activeTab === 'displayOrder' && (
        <div style={styles.tableContainer}>
          <table style={styles.tableContainer}>
            <thead>
              <tr>
                <th style={styles.tableCell}>Order ID</th>
                <th style={styles.tableCell}>Order Name</th>
                <th style={styles.tableCell}>Status</th>
                <th style={styles.tableCell}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {ordersToFulfill.map((order) => (
                <tr key={order.id}>
                  <td style={styles.tableCell}>{order.id}</td>
                  <td style={styles.tableCell}>{order.name}</td>
                  <td style={styles.tableCell}>{order.status}</td>
                  <td style={styles.tableCell}>
                    <button
                      style={styles.button}
                      onClick={() => handleFulfillOrder(order.id)}
                    >
                      Fulfill Order
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'fulfilledOrders' && (
        <div style={styles.tableContainer}>
          <table style={styles.tableContainer}>
            <thead>
              <tr>
                <th style={styles.tableCell}>Order ID</th>
                <th style={styles.tableCell}>Order Name</th>
                <th style={styles.tableCell}>Status</th>
              </tr>
            </thead>
            <tbody>
              {fulfilledOrders.map((order) => (
                <tr key={order.id}>
                  <td style={styles.tableCell}>{order.id}</td>
                  <td style={styles.tableCell}>{order.name}</td>
                  <td style={styles.tableCell}>{order.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default StaffOrderComponent;
