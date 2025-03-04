import React, { useState } from 'react';
import './SignIn.css';
import Divider from '@mui/material/Divider';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import apiClient from '../../api/axios'; 
import { FaEye, FaEyeSlash } from "react-icons/fa";

import { useCart } from "../../contexts/CartContext";

const SignIn = () => {
    const navigate = useNavigate();
    
    const { cart, setCartManually} = useCart();


    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

   
    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Basic validation
        if (!formData.email || !formData.password) {
          toast.error('Please fill in all fields.');
          return;
        }
    
        // Here’s the roles array. We'll loop them in order:
        const roles = ['customer', 'sales-manager', 'product-manager', 'admin'];
        let isLoggedIn = false;
        let token = null;
    
        for (const role of roles) {
          try {
            // Attempt a login POST to e.g. /customer/login, /sales-manager/login, etc.
            // This uses your old logic, but now repeated for each role.
            const response = await apiClient.post(`${role}/login`, {
              email: formData.email,
              password: formData.password,
              cart: cart // only relevant for customer, but harmless to pass
            });
            console.log("for role: " + role);
            console.log("response headers are: " + response.headers )
            console.log("response data are: " + response.data )

            // If your back end returns a string token (like the old customer logic):
            // In your old code, "response.data" was the token itself.
            // If you changed it to return { token, message }, adjust accordingly.
            token = response.data.Authorization;
    
            // If we got here, it means login succeeded for this role
            localStorage.setItem('authToken', token);
            console.log("set the jwt as: " + token);
            toast.success(`Logged in as ${role}!`);
            isLoggedIn = true;
    
            // If role is "customer", fetch the cart (as your old code did):
            if (role === 'customer') {
              try {
                const response2 = await apiClient.get('/customer/details/cart', {
                  headers: {
                    'Authorization': `Bearer ${token}`,
                  }
                });
                setCartManually(response2.data.cart);
              } catch (cartError) {
                console.error('Error fetching customer cart:', cartError);
              }
            }
    
            // Once successful, stop trying other roles
            break;
          } catch (error) {
            // This means the current role’s login failed, so we move to the next role
            console.error(`Login attempt for ${role} failed. Trying next...`, error);
          }
        }
    
        // If all roles fail, show a single final error
        if (isLoggedIn) {
          navigate('/login-redirect');
        } else {
          toast.error('Invalid email or password for all roles.');
        }
      };
      
    const [showPassword, setShowPassword] = useState(false);

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    return (
        <div className="signin-container">
            <div className='form-holder-signin'>
                <div className='sign-in-styling'>
                    <div className='top-text'>
                        <p>Welcome back to</p>
                        <h1 className='sign-in-wellmarkt-title'>Wellmarkt!</h1>
                    </div>
                    <p className='bottom-text'>- The path you take is a good one.</p>
                    <img className='image' src='/assets/case-8139_256.gif' alt="Fall animation"/>
                </div>
                <form className="signin-form" onSubmit={handleSubmit}>
                    <h1>Sign In</h1>
                    <Divider sx={{ borderBottomWidth: 1, borderColor: 'black', opacity: 0.1, marginBottom: 3 }} />
                    
                    <div className='email-input-login'>
                        <input
                            type="email"
                            name="email"
                            placeholder="Email *"
                            value={formData.email}
                            onChange={handleChange}
                        />
                    </div>

                    <div className='password-input-login'>
                        <input
                            type={showPassword ? 'text' : 'password'}
                            name="password"
                            placeholder="Password *"
                            value={formData.password}
                            onChange={handleChange}
                        />
                        <Button className='show-hide-btn' 
                                onClick={togglePasswordVisibility}
                                style={{padding: '0.6rem',
                                        fontSize: '1.2rem',
                                        marginLeft: '-4.6rem',
                                        cursor: 'pointer',
                                        color: '#000',
                                        borderTopLeftRadius: '0px',
                                        borderBottomLeftRadius: '0px',
                                        height: '40px'}}>
                            {showPassword ? <FaEyeSlash/> : <FaEye/>}
                        </Button>
                    </div>

                    <Button
                        className='submit-button-login'
                        variant="contained"
                        type="submit"
                        style={{
                            width: '300px',
                            display: 'block',
                            margin: '0 auto',
                            backgroundColor: 'black',
                            
                        }}
                    >
                        Sign In
                    </Button>

                    <Divider sx={{paddingTop: 2, color: 'white'}}>
                        <Chip label="OR" size="small" />
                    </Divider>
                    <Divider sx={{ borderColor: 'black', opacity: 0.15, marginTop: 3 , marginBottom: 3 }} />
                    <p className='already'>Don't have an account? <Link to='/sign-up'>Sign Up</Link></p>
                </form>
            </div>
        </div>
        
    );
};
export default SignIn;