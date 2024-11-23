import axios from 'axios';
import { getToken } from './AuthService';

const BASE_REST_API_URL = 'http://localhost:8080/api/orders';

// Setting up the Authorization token for every request
axios.interceptors.request.use(function (config) {
  const token = getToken();
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`; // Add Bearer prefix if using JWT
  }
  return config;
}, function (error) {
  // Handle request error
  return Promise.reject(error);
});

// API call to save a new order
export const saveOrder = (order) => 
  axios.post(BASE_REST_API_URL, order)
       .then(response => response.data)
       .catch(error => console.error('Error saving order:', error));

// API call to get order by ID
export const getOrderById = (id) => 
  axios.get(`${BASE_REST_API_URL}/${id}`)
       .then(response => response.data)
       .catch(error => console.error('Error fetching order by ID:', error));

// API call to get the active order
export const getActiveOrder = () => 
  axios.get(`${BASE_REST_API_URL}/active`)
       .then(response => response.data)
       .catch(error => console.error('Error fetching active order:', error));

// API call to get all orders
export const getAllOrders = () => 
  axios.get(BASE_REST_API_URL)
       .then(response => response.data)
       .catch(error => console.error('Error fetching all orders:', error));

// API call to purchase an order
// API call to purchase an order with payment amount
export const purchaseOrder = (amtPaid) => 
  axios.post(`${BASE_REST_API_URL}/purchase`, amtPaid, {
    headers: { 'Content-Type': 'text/plain' } // Specify content type as plain text for the amount
  })
  .then(response => response.data)
  .catch(error => {
    console.error('Error purchasing order:', error);
    throw error; // Re-throw error to allow the caller to handle it
  });


// API call to update an existing order
export const updateOrder = (id, order) => 
  axios.put(`${BASE_REST_API_URL}/${id}`, order)
       .then(response => response.data)
       .catch(error => console.error('Error updating order:', error));

	   
export const getOrderHistory = () => 
	axios.get(`${BASE_REST_API_URL}/history`)
	     .then(response => response.data)
	     .catch(error => {
	      console.error('Error fetching order history:', error);
	        throw error; // Re-throw error to allow the caller to handle it
	      });
		  
		  export const editActiveOrder = async (orderDto) => {
		      try {
		          const response = await axios.put(`${BASE_URL}/editActive`, orderDto);
		          return response.data;
		      } catch (error) {
		          throw error;
		      }
		  };
		  
		  export const deleteOrder = (id) =>
		    axios.delete(`${BASE_REST_API_URL}/${id}`)
		      .then(response => response.data)
		      .catch(error => {
		        console.error('Error deleting order:', error);
		        throw error; // Re-throw error to allow the caller to handle it
		      });
			  
			  export const updateItemQuantity = async (itemName, quantityToAdd) => {
			    try {
			      // Make the PUT request to update the item quantity
			      const response = await axios.put(
			        `${API_URL}/active/items/${itemName}`,
			        quantityToAdd, // Send the quantity to add in the request body
			        {
			          headers: {
			            'Content-Type': 'application/json',
			            // Add Authorization header if required (example: Authorization: `Bearer ${token}`)
			          }
			        }
			      );

			      // Return the updated order data
			      return response.data;
			    } catch (error) {
			      // Handle errors here
			      if (error.response) {
			        // Server responded with an error
			        console.error('Error updating item quantity:', error.response.data);
			        throw error.response.data;  // Rethrow error data for handling in the UI
			      } else if (error.request) {
			        // No response received from server
			        console.error('No response from server:', error.request);
			      } else {
			        // Other errors (e.g., configuration issues)
			        console.error('Error:', error.message);
			      }

			      // Throw error to be handled in the calling component
			      throw new Error('Failed to update item quantity');
			    }
			  };