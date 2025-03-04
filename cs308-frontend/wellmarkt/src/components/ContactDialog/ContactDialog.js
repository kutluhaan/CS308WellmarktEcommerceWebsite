import React, { useState } from 'react';
import './ContactDialog.css';
import { toast } from 'react-hot-toast';
import ReCAPTCHA from 'react-google-recaptcha';
import Divider from '@mui/material/Divider';


const Contact = () => {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        phone: '',
        country: '',
        message: ''
    });

    const [captchaToken, setCaptchaToken] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleCaptchaChange = (token) => {
        setCaptchaToken(token); // Save the token when captcha is solved
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.fullName || !formData.email || !formData.message) {
            toast.error('Please fill in all required fields.');
            return;
        }

        if (!captchaToken) {
            toast.error('Please complete the reCAPTCHA to verify you are human.');
            return;
        }

        setIsSubmitting(true);

        try {
            // Mock backend API call
            await new Promise((resolve) => setTimeout(resolve, 1500));

            toast.success('Your message has been sent successfully!', {
                position: 'top-center',
            });

            setFormData({
                fullName: '',
                email: '',
                phone: '',
                country: '',
                message: ''
            });
            setCaptchaToken(null); // Reset captcha
        } catch (error) {
            toast.error('Something went wrong. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="contact-container">
            <div className="form-holder">
                <form className="contact-form" onSubmit={handleSubmit}>
                    <h1>Contact Us</h1>
                    <Divider sx={{ borderColor: 'black', opacity: 0.15, marginTop: 3, marginBottom: 3 }} />
                    <div className="name-email-input">
                        <input
                            type="text"
                            name="fullName"
                            placeholder="Full Name *"
                            value={formData.fullName}
                            onChange={handleChange}
                        />
                        <input
                            type="email"
                            name="email"
                            placeholder="Email *"
                            value={formData.email}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="phone-country-input">
                        <input
                            type="tel"
                            name="phone"
                            placeholder="Phone Number"
                            value={formData.phone}
                            onChange={handleChange}
                        />
                        <input
                            type="text"
                            name="country"
                            placeholder="Country"
                            value={formData.country}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="message-input">
                        <textarea
                            name="message"
                            placeholder="Write your message here *"
                            value={formData.message}
                            onChange={handleChange}
                            rows="5"
                        ></textarea>
                    </div>
                    <ReCAPTCHA
                        sitekey="6LcC14EqAAAAAOpRat9avD53772Pk7EeFvQ9V4Ju" 
                        onChange={handleCaptchaChange}
                    />
                    <button type="submit" className="submit-button" disabled={isSubmitting}>
                        {isSubmitting ? "Sending..." : "Submit"}
                    </button>
                
                    
                </form>

                
            </div>
        </div>
    );
};

export default Contact;
