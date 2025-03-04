import React, { useState } from 'react'; 
import './SignUp.css';
import Divider from '@mui/material/Divider';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import apiClient from '../../api/axios';
import { useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { FaEye, FaEyeSlash } from "react-icons/fa";



const SignUp = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        middleName: '',   
        lastName: '',
        email: '',
        confirmEmail: '',
        password: '',
        confirmPassword: '',
        address: '',       
        country: '',       
        phoneNumber: ''    
    });

    const [showPassword2, setShowPassword2] = useState(false);

    const togglePasswordVisibility2 = () => {
        setShowPassword2(!showPassword2);
    };

    const [showPassword1, setShowPassword1] = useState(false);

    const togglePasswordVisibility1 = () => {
        setShowPassword1(!showPassword1);
    };
    
    const navigate = useNavigate();
    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validation logic

        // Validation for empty required fields
        if (!formData.firstName || !formData.lastName || !formData.email || !formData.password) {
            toast.error('Please fill in all required fields.');
            setFormData((prevFormData) => ({
                ...prevFormData,
                password: '',
                confirmPassword: '',
            }));
            return;
        }

        if (formData.email !== formData.confirmEmail) {
            toast.error('Emails do not match.');
            setFormData((prevFormData) => ({
                ...prevFormData,
                password: '',
                confirmPassword: '',
            }));
            return;
        }

        if (formData.password !== formData.confirmPassword) {
            toast.error('Passwords do not match.');
            setFormData((prevFormData) => ({
                ...prevFormData,
                password: '',
                confirmPassword: '',
            }));
            return;
        }

        // API call
        try {
            await apiClient.post('/customer/add-customer', {
                firstName: formData.firstName,
                middleName: formData.middleName,
                lastName: formData.lastName,
                email: formData.email,
                password: formData.password,
                address: formData.address, // Still sent in the payload
                country: formData.country, // Still sent in the payload
                phoneNumber: formData.phoneNumber // Still sent in the payload
            });
            
            // Show notifications together
            toast.success('Signup successful! Welcome to Wellmarkt.', {
                position: 'top-center', // Notification in the middle of the screen
            });
            toast('Redirecting to Sign In...', {
                icon: '➡️',
                position: 'top-center', // Notification in the middle of the screen
            });

            // Redirect after a short delay
            setTimeout(() => {
                navigate('/sign-in'); // Redirect to Sign In
            }, 2000);
            

        } catch (error) {
            console.error(error); // For debugging you can delete later
            if (error.response) {
                toast.error('Error: ' + error.response.data);
            } else {
                toast.error('Error: Unable to connect to the server.');
            }
        }
    };

    return (
        <div className="signup-container">
        <div className='form-holder-signup'>
            <form className="signup-form" onSubmit={handleSubmit}>
                <h1>Sign Up</h1>
                <Divider sx={{ borderBottomWidth: 1, borderColor: 'black', opacity: 0.1, marginBottom: '30px', }} />
                <div className='name-input-signup'>
                    <input
                        type="text"
                        name="firstName"
                        placeholder="First Name *"
                        value={formData.firstName}
                        onChange={handleChange}
                    />
                    <input
                        type="text"
                        name="middleName"
                        placeholder="Middle Name"
                        value={formData.middleName}
                        onChange={handleChange}
                    />
                    <input
                        type="text"
                        name="lastName"
                        placeholder="Last Name *"
                        value={formData.lastName}
                        onChange={handleChange}
                    />
                </div>

                <div className='email-input-signup'>
                    <input
                        type="email"
                        name="email"
                        placeholder="Email *"
                        value={formData.email}
                        onChange={handleChange}
                    />
                    <input
                        type="email"
                        name="confirmEmail"
                        placeholder="Confirm Email *"
                        value={formData.confirmEmail}
                        onChange={handleChange}
                    />
                </div>

                <div className='password-input-signup'>
                    <div className='password-holder'> 
                        <input
                            className='password-input' 
                            name='password'
                            type={showPassword1 ? 'text' : 'password'} 
                            placeholder='Password'
                            value={formData.password}
                            onChange={handleChange}
                        />

                        <Button className='show-hide-btn' 
                                onClick={togglePasswordVisibility1}
                                style={{padding: '0.6rem',
                                        fontSize: '1.2rem',
                                        marginLeft: '-4rem',
                                        cursor: 'pointer',
                                        color: '#000',
                                        borderTopLeftRadius: '0px',
                                        borderBottomLeftRadius: '0px',
                                        height: '40px'}}>
                            {showPassword1 ? <FaEyeSlash/> : <FaEye/>}
                        </Button>
                    </div>
                    
                    <div className='password-holder'>
                        <input
                            className='password-input' 
                            name='confirmPassword'
                            type={showPassword2 ? 'text' : 'password'} 
                            placeholder='Retype Password'
                            value={formData.confirmPassword}
                            onChange={handleChange}
                        />

                        <Button
                            className='show-hide-btn' 
                            onClick={togglePasswordVisibility2}
                            style={{padding: '0.2rem',
                                    fontSize: '1.2rem',
                                    marginLeft: '-4rem',
                                    cursor: 'pointer',
                                    color: '#000',
                                    borderTopLeftRadius: '0px',
                                    borderBottomLeftRadius: '0px',
                                    height: '40px'}}
                        >
                            {showPassword2 ? <FaEyeSlash/> : <FaEye/>}
                        </Button>
                    </div>
                </div>

                <Button
                    type="submit" 
                    className='submit-button-signup'
                    variant="contained"
                    style={{
                        width: '300px',
                        display: 'block',
                        margin: '0 auto',
                        backgroundColor: 'black',
                        marginTop: '50px',
                        marginBottom: '20px'
                    }}
                    onSubmit={handleSubmit}
                >
                    Sign Up
                </Button>

                <Divider sx={{ paddingTop: 2, color: 'white' }}>
                    <Chip label="OR" size="small" />
                </Divider>
                <Divider sx={{ borderColor: 'black', opacity: 0.15, marginTop: 3, marginBottom: 3 }} />
                <p className='already'>Already have an account? <Link to='/sign-in'>Sign In</Link></p>
                
             
            </form>
            <div className='sign-up-styling'>
                <div className='top-text'>
                    <p>Welcome to</p>
                    <h1>Wellmarkt!</h1>
                </div>
                        <p className='bottom-text'>- Your journey begins here.</p>
                        <img className='image' src='/assets/spring-4022_256.gif' alt="Spring animation" />
                    </div>
                </div>
            </div>
    );
};

export default SignUp;
