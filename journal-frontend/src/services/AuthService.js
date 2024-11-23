import axios from 'axios'

const AUTH_REST_API_BASE_URL = 'http://localhost:8080/api/auth'

export const registerAPICall = (registerObj) => axios.post(AUTH_REST_API_BASE_URL + '/register', registerObj)

export const loginAPICall = (usernameOrEmail, password) => axios.post(AUTH_REST_API_BASE_URL + '/login', { usernameOrEmail, password })

export const storeToken = (token) => localStorage.setItem('token', token)

export const getToken = () => localStorage.getItem('token')

export const saveLoggedInUser = (username, role) => {
    sessionStorage.setItem('authenticatedUser', username)
    sessionStorage.setItem('role', role)
}

export const isUserLoggedIn = () => {
    const username = sessionStorage.getItem('authenticatedUser')

    if (username == null) return false
    else return true
}

export const getUserRole = () => {
    return sessionStorage.getItem('role');
};

export const getLoggedInUser = () => {
    const username = sessionStorage.getItem('authenticatedUser')
    return username
}

export const logout = () => {
    const currentColor = localStorage.getItem('backgroundColor'); // Save the current background color
    localStorage.clear(); // Clear all other localStorage data
    if (currentColor) {
        localStorage.setItem('backgroundColor', currentColor); // Restore the background color
    }
    sessionStorage.clear();
};


export const isAdminUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_ADMIN';
}

// Adds a new staff member, requires an admin role
export const addStaffAPICall = (staffRegistrationObj) =>
  axios.post(`${AUTH_REST_API_BASE_URL}/addStaff`, staffRegistrationObj, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });

// Adds a new marketer, requires an admin role
export const addMarketerAPICall = (marketerRegistrationObj) =>
  axios.post(`${AUTH_REST_API_BASE_URL}/addMarketer`, marketerRegistrationObj, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });

// Edits user details, requires an admin role
export const editUserAPICall = (username, newUsername, name, email, password) =>
  axios.put(
    `${AUTH_REST_API_BASE_URL}/${username}`,
    {
      username: newUsername,
      name,
      email, 
      password
    },
    {
      headers: {
        Authorization: `Bearer ${getToken()}`,
      },
    }
  );

// Retrieves all users, requires an admin role
export const getAllUsersAPICall = () =>
  axios.get(`${AUTH_REST_API_BASE_URL}/users`, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });

// Retrieves all customers, requires an admin role
export const getAllCustomersAPICall = () =>
  axios.get(`${AUTH_REST_API_BASE_URL}/customers`, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });

// Retrieves all staff members, requires an admin role
export const getAllStaffAPICall = () =>
  axios.get(`${AUTH_REST_API_BASE_URL}/staff`, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });

// Retrieves all marketers, requires an admin role
export const getAllMarketersAPICall = () =>
  axios.get(`${AUTH_REST_API_BASE_URL}/marketers`, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });
  
// Deletes a user, requires an admin role
export const deleteUserAPICall = (username) =>
  axios.delete(`${AUTH_REST_API_BASE_URL}/users/${username}`, {
    headers: {
      Authorization: `Bearer ${getToken()}`,
    },
  });

