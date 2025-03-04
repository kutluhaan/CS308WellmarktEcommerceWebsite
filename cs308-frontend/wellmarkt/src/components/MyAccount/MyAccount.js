import React, { useState, useEffect } from 'react';
import apiClient from '../../api/axios';
import './MyAccount.css';

const MyAccount = () => {
    const [customer, setCustomer] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCustomerInformation = async () => {
            try {
                const token = localStorage.getItem("authToken");
                if (!token) {
                    setError("No authentication token found. Please log in.");
                    setLoading(false);
                    return;
                }

                const response = await apiClient.get("customer/details", {
                    headers: {
                        Authorization: `${token}`,
                    },
                });

                setCustomer(response.data.customer);
            } catch (err) {
                setError("Failed to fetch customer details.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchCustomerInformation();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    return (
        <div className="my-account-container">
            <h1 className='my-account-title'>My Account</h1>
            <div className="account-details">
                <div className="detail-row"><span className="label">Customer ID:</span> {customer.cID}</div>
                <div className="detail-row"><span className="label">First Name:</span> {customer.firstName}</div>
                <div className="detail-row"><span className="label">Middle Name:</span> {customer.middleName || "N/A"}</div>
                <div className="detail-row"><span className="label">Last Name:</span> {customer.lastName}</div>
                <div className="detail-row"><span className="label">Email:</span> {customer.email}</div>
                <div className="detail-row"><span className="label">Tax ID:</span> {customer.taxID || "N/A"}</div>
                <div className="detail-row"><span className="label">Address:</span> {customer.address || "N/A"}</div>
                <div className="detail-row"><span className="label">Country:</span> {customer.country || "N/A"}</div>
                <div className="detail-row"><span className="label">Phone Number:</span> {customer.phoneNumber || "N/A"}</div>
                <div className="detail-row"><span className="label">Account Created At:</span> {new Date(customer.createdAt).toLocaleString()}</div>
                <div className="detail-row"><span className="label">Last Login:</span> {new Date(customer.lastLogin).toLocaleString()}</div>
            </div>
        </div>
    );
};

export default MyAccount;
