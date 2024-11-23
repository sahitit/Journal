import React, { useEffect, useContext, useState } from 'react';
import { getOrderHistory } from '../services/OrderService';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const OrderHistoryComponent = () => {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState(null);
  
  const { backgroundColor } = useContext(BackgroundCustomizationContext);

  useEffect(() => {
    fetchOrderHistory();
  }, []);

  const fetchOrderHistory = async () => {
    try {
      const orderHistory = await getOrderHistory();
      setOrders(orderHistory);
    } catch (err) {
      setError('Failed to fetch order history. Please try again later.');
    }
  };

  const styles = {
    container: {
      padding: '20px',
      maxWidth: '800px',
      margin: '0 auto',
      fontFamily: 'Arial, sans-serif',
    },
    heading: {
      textAlign: 'center',
      marginBottom: '20px',
    },
    table: {
      width: '100%',
      borderCollapse: 'collapse',
      margin: '20px 0',
    },
    th: {
      border: '1px solid #ddd',
      padding: '8px',
      backgroundColor: '#f4f4f4',
      fontWeight: 'bold',
      textAlign: 'left',
    },
    td: {
      border: '1px solid #ddd',
      padding: '8px',
      textAlign: 'left',
    },
    errorMessage: {
      color: 'red',
      textAlign: 'center',
    },
    noOrders: {
      textAlign: 'center',
      fontStyle: 'italic',
    },
  };

  return (
    <div style={styles.container}>
      <br/>
      <br/>
	  <h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
	      WolfCafe
	  </h1>
      <h2 style={styles.heading}>Order History</h2>
      {error && <p style={styles.errorMessage}>{error}</p>}
      {!error && orders.length === 0 && <p style={styles.noOrders}>No orders found in history.</p>}
      {orders.length > 0 && (
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>Order ID</th>
              <th style={styles.th}>Name</th>
              <th style={styles.th}>Status</th>
              <th style={styles.th}>Total Cost</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td style={styles.td}>{order.id}</td>
                <td style={styles.td}>{order.name}</td>
                <td style={styles.td}>{order.status}</td>
                <td style={styles.td}>${order.orderCost.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default OrderHistoryComponent;
