import './App.css'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { BackgroundCustomizationProvider } from './context/BackgroundCustomizationContext'
import HeaderComponent from './components/HeaderComponent'
import FooterComponent from './components/FooterComponent'
import ListItemsComponent from './components/ListItemsComponent'
import ItemComponent from './components/ItemComponent'
import RegisterComponent from './components/RegisterComponent'
import LoginComponent from './components/LoginComponent'
import InventoryPage from './pages/InventoryPage'
import CustomizationPage from './components/CustomizationPage'
import { isUserLoggedIn } from './services/AuthService'
import CustomerOrderComponent from './components/CustomerOrderComponent.jsx'
import CartComponent from './components/CartComponent.jsx'
import AdminHome from './components/AdminHome'
import StaffHome from './components/StaffHome'
import InventoryComponent from './components/InventoryComponent'
import AddItemModal from './components/AddItemModal'
import OrderHistoryComponent from './components/OrderHistoryComponent'
import React, { useState } from 'react'
import StaffOrderComponent from './components/StaffOrderComponent'
import AdminOrderComponent from './components/StaffOrderComponent'

import AdminUsers from './components/AdminUsers';
import AddStaff from './components/AddStaff';
import AddMarketer from './components/AddMarketer'
import EditUser from './components/EditUser'
import PrivacyPolicy from './components/PrivacyPolicy.jsx'
import Accessibility from './components/Accessibility.jsx' // Import the Accessibility component


function App() {

  function AuthenticatedRoute({ children }) {
    const isAuth = isUserLoggedIn()
    if (isAuth) {
      return children
    }
    return <Navigate to='/' />
  }

  return (
    <>
      <BrowserRouter>
	  	<BackgroundCustomizationProvider>
	        <HeaderComponent />
	        <Routes>
	          <Route path='/' element={<LoginComponent />}></Route>
	          <Route path='/register' element={<RegisterComponent />}></Route>
	          <Route path='/login' element={<LoginComponent />}></Route>
	
	          <Route path='/privacy' element={<PrivacyPolicy />}></Route>
			  <Route path='/accessibility' element={<Accessibility />}></Route> {/* New Accessibility route */}
	
	          
	          <Route path='/items' element={<AuthenticatedRoute><ListItemsComponent /></AuthenticatedRoute>}></Route>
	          <Route path="inventory/*" element={<InventoryPage />} />
	          <Route path='/add-item' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
	          
	          <Route path='/update-item/:id' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
	          
	          {/* Admin-specific routes */}
	          <Route path='/admin/home' element={<AuthenticatedRoute><AdminHome /></AuthenticatedRoute>}></Route>
			  <Route path='/admin/cart' element={<AuthenticatedRoute><CartComponent /></AuthenticatedRoute>}></Route>
	          <Route path='/admin/inventory' element={<AuthenticatedRoute><InventoryComponent /></AuthenticatedRoute>}></Route>
	          <Route path='/admin/inventory/add-item' element={<AuthenticatedRoute><AddItemModal /></AuthenticatedRoute>}></Route>
	          <Route path='/admin/users' element={<AuthenticatedRoute><AdminUsers /></AuthenticatedRoute>}/>
	          <Route path='/admin/add-staff' element={<AuthenticatedRoute><AddStaff /></AuthenticatedRoute>}/>
			  <Route path='/admin/add-marketer' element={<AuthenticatedRoute><AddMarketer /></AuthenticatedRoute>}/>
			  <Route path='/admin/edit-user/:username' element={<AuthenticatedRoute><EditUser /></AuthenticatedRoute>}/>
			  <Route path='/admin/customize' element={<AuthenticatedRoute><CustomizationPage /></AuthenticatedRoute>}></Route>
			  <Route path='/admin/makeorder/displayOrder' element={<AuthenticatedRoute><AdminOrderComponent /></AuthenticatedRoute>}></Route>
			  <Route path='/admin/makeorder/fulfillOrder' element={<AuthenticatedRoute><AdminOrderComponent /></AuthenticatedRoute>}></Route>
			  <Route path='/admin/makeorder/fulfilledOrders' element={<AuthenticatedRoute><AdminOrderComponent /></AuthenticatedRoute>}></Route>
	                                           
			  {/* Marketer-specific routes */}
			  <Route path='/marketer/home' element={<AuthenticatedRoute><CustomizationPage /></AuthenticatedRoute>}></Route>
			    
	          {/* Staff-specific routes */}
	          <Route path='/staff/home' element={<AuthenticatedRoute><StaffHome /></AuthenticatedRoute>}></Route>
	          <Route path='/staff/inventory' element={<AuthenticatedRoute><InventoryComponent /></AuthenticatedRoute>}></Route>
	          <Route path='/staff/inventory/add-item' element={<AuthenticatedRoute><AddItemModal /></AuthenticatedRoute>}></Route>
			  <Route path='/staff/makeorder/add-item' element={<AuthenticatedRoute><AddItemModal /></AuthenticatedRoute>}></Route>
			  <Route path='/staff/makeorder/displayOrder' element={<AuthenticatedRoute><StaffOrderComponent /></AuthenticatedRoute>}></Route>
			  <Route path='/staff/makeorder/fulfillOrder' element={<AuthenticatedRoute><StaffOrderComponent /></AuthenticatedRoute>}></Route>
			  <Route path='/staff/makeorder/fulfilledOrders' element={<AuthenticatedRoute><StaffOrderComponent /></AuthenticatedRoute>}></Route>
	
	          {/* Customer-specific routes */}
	          <Route path='/customer/home' element={<AuthenticatedRoute><CustomerOrderComponent /></AuthenticatedRoute>}></Route>
	          <Route path='/customer/cart' element={<AuthenticatedRoute><CartComponent /></AuthenticatedRoute>}></Route>
			  <Route path='/customer/orders' element={<AuthenticatedRoute><OrderHistoryComponent /></AuthenticatedRoute>}></Route> 
	        </Routes>
	        <FooterComponent />
		</BackgroundCustomizationProvider>
      </BrowserRouter>
    </>
  )
}

export default App