import React, { useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Button, Box } from '@mui/material';
import { isUserLoggedIn, logout, getUserRole } from '../services/AuthService';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';
import wolfCafeImage from '../visuals/wolfcafe.jpg'; // Import the image

const HeaderComponent = () => {
  const [isAuth, setIsAuth] = useState(isUserLoggedIn());
  const [userRole, setUserRole] = useState(getUserRole());
  const navigate = useNavigate();
  
  const { backgroundColor } = useContext(BackgroundCustomizationContext);

  const handleLogout = () => {
    logout();
    setIsAuth(false);
    setUserRole(null);
    navigate('/login');
  };

  useEffect(() => {
    // Update authentication status and role on component mount or when authentication changes
    setIsAuth(isUserLoggedIn());
    setUserRole(getUserRole());
  }, [isAuth]);

  const buttonStyles = {
    fontFamily: "'Georgia', serif", 
    fontWeight: 600, 
    fontSize: '16px', 
    textTransform: 'none', 
    color: '#ffffff',
  };

  return (
    <AppBar 
      position="fixed" 
      elevation={1} 
      style={{ backgroundColor: backgroundColor, padding: '10px 0' }}
    >
      <Toolbar style={{ justifyContent: 'space-between', height: '80px' }}>
        <Box 
          style={{ 
            display: 'flex', 
            alignItems: 'center',
            gap: '20px',
          }}
        >
          <img 
            src={wolfCafeImage} 
            alt="Wolf Cafe Logo" 
            style={{ height: '80px', borderRadius: '8px' }} 
          />
          
          {/* Authenticated Navigation */}
          {isAuth && (
            <>
              {userRole === 'ROLE_ADMIN' && (
                <>
                  <Button sx={buttonStyles} onClick={() => navigate('/admin/home')}>Home</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/admin/inventory')}>Inventory</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/admin/users')}>Users</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/admin/makeorder/fulfillOrder')}>All Orders</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/admin/cart')}>Cart</Button>
                </>
              )}

              {userRole === 'ROLE_STAFF' && (
                <>
                  <Button sx={buttonStyles} onClick={() => navigate('/staff/home')}>Home</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/staff/inventory')}>Inventory</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/staff/makeorder/fulfillOrder')}>Orders</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/staff/account')}>Account</Button>
                </>
              )}

              {userRole === 'ROLE_CUSTOMER' && (
                <>
                  <Button sx={buttonStyles} onClick={() => navigate('/customer/home')}>Home</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/customer/orders')}>Past Orders</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/customer/account')}>Account</Button>
                  <Button sx={buttonStyles} onClick={() => navigate('/customer/cart')}>Cart</Button>
                </>
              )}

              <Button sx={buttonStyles} onClick={handleLogout}>Logout</Button>
            </>
          )}
          
          {!isAuth && (
            <>
              <Button sx={buttonStyles} onClick={() => navigate('/login')}>Login</Button>
              <Button sx={buttonStyles} onClick={() => navigate('/register')}>Sign Up</Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default HeaderComponent;
