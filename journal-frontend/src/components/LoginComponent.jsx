import React, { useState, useContext } from 'react'
import { loginAPICall, saveLoggedInUser, storeToken } from '../services/AuthService'
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext'
import { useNavigate } from 'react-router-dom'

const LoginComponent = () => {

    const [usernameOrEmail, setUsernameOrEmail] = useState('')
    const [password, setPassword] = useState('')

	const { backgroundColor } = useContext(BackgroundCustomizationContext);
	
    const navigator = useNavigate()

	async function handleLoginForm(e) {
	    e.preventDefault();

	    const loginObj = { usernameOrEmail, password };

	    console.log(loginObj);

	    await loginAPICall(usernameOrEmail, password)
	        .then((response) => {
	            console.log(response.data);

	            const token = 'Bearer ' + response.data.accessToken;
	            const role = response.data.role;

	            storeToken(token);
	            saveLoggedInUser(usernameOrEmail, role);

	            // Navigate based on user type
	            console.log(role);
	            if (role === 'ROLE_ADMIN') {
	                navigator('/admin/home'); // Admin route
	            } else if (role === 'ROLE_STAFF') {
	                navigator('/staff/home'); // Staff route
	            } else if (role === 'ROLE_CUSTOMER') {
	                navigator('/customer/home'); // Customer route
	            } else if (role === 'ROLE_MARKETER') {
	                navigator('/marketer/home'); // Marketer route
	            } else {
	                navigator('/items'); // Default route if role is unknown
	            }

	            window.location.reload(false);
	        })
	        .catch((error) => {
	            console.error('ERROR1' + error);
	        });
	}


  return (
    <div className='container'>
        <br /><br />
        <h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
                WolfCafe
            </h1>
        <div className='row'>
            <div className='col-md-6 offset-md-3 offset-md-3'>
                <div className='card'>
                    <div className='card-header'>
                        <h2 className='text-center'>Login</h2>
                    </div>
                    <div className='card-body'>
                        <form>
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='usernameOrEmail'
                                        className='form-control'
                                        placeholder='Enter username or email'
                                        value={usernameOrEmail}
                                        onChange={(e) => setUsernameOrEmail(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Password</label>
                                <div className='col-md-9'>
                                    <input
                                        type='password'
                                        name='password'
                                        className='form-control'
                                        placeholder='Enter password'
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='form-group mb-3'>
                                <button className='btn btn-primary' onClick={(e) => handleLoginForm(e)}>Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
  )
}

export default LoginComponent