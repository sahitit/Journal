import axios from 'axios';
import { getToken } from './AuthService';

const BASE_REST_API_URL = 'http://localhost:8080/api/inventory';

axios.interceptors.request.use(function (config) {
  config.headers['Authorization'] = getToken();
  return config;
}, function (error) {
  // Do something with request error
  return Promise.reject(error);
});

// Get current inventory
export const getInventory = () => axios.get(BASE_REST_API_URL);

// Add a new item to the inventory
export const addItem = (item) => axios.post(BASE_REST_API_URL, item);

// Get an item by its ID
export const getItemById = (id) => axios.get(`${BASE_REST_API_URL}/item/${id}`);

// Update the inventory with a list of items
export const updateInventory = (inventory) => axios.put(BASE_REST_API_URL, inventory);

