// services/MakeOrderService.js

import axios from 'axios';

const BASE_REST_API_URL = 'http://localhost:8080/api/makeorder';

// Fetch orders to fulfill
export const getOrdersToFulfill = async () => {
  try {
    const response = await axios.get(`${BASE_REST_API_URL}/displayOrder`);
    return response;
  } catch (error) {
    console.error('Error fetching orders to fulfill:', error);
    throw error;
  }
};

// Fetch fulfilled orders
export const getFulfilledOrders = async () => {
  try {
    const response = await axios.get(`${BASE_REST_API_URL}/fulfilledOrders`);
    return response;
  } catch (error) {
    console.error('Error fetching fulfilled orders:', error);
    throw error;
  }
};

export const fulfillOrder = (orderId) => 
  axios.put(`${BASE_REST_API_URL}/fulfillOrder`, null, {
    params: { orderId }, // Add query parameter
  })
  .then(response => response.data)
  .catch(error => {
    console.error('Error in fulfillOrder:', error);
    throw error; // Re-throw error to handle it in the calling function
  });

  export const pickupOrder = (orderId) => 
    axios.put(`${BASE_REST_API_URL}/pickupOrder`, null, {
      params: { orderId }, // Add query parameter
    })
    .then(response => response.data)
    .catch(error => {
      console.error('Error in pickupOrder:', error);
      throw error; // Re-throw error to handle it in the calling function
    });