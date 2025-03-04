import React, { useState, useEffect } from 'react';
import CreditCard from '../CreditCard/CreditCard';
import OrderSummaryCart from '../OrderSummaryCart/OrderSummaryCart';
import CircularProgress from '@mui/material/CircularProgress'; // Import MUI spinner
import './PurchaseForm.css';
import apiClient from '../../api/axios';
import { useCart } from "../../contexts/CartContext";

const PurchaseForm = () => {
    const [formData, setFormData] = useState({
        cardNumber: '',
        cardHolder: '',
        expirationDate: '',
        cvv: '',
        address: '',
        city: '',
        zipCode: ''
    });
    const [isFormValid, setIsFormValid] = useState(false);
    const [loading, setLoading] = useState(false); // Loading state for spinner
    const [purchaseSuccess, setPurchaseSuccess] = useState(false);
    const [invoiceData, setInvoiceData] = useState(null);

    const { clearCart } = useCart();

    useEffect(() => {
        const isValid = Object.values(formData).every(value => value.trim() !== '');
        setIsFormValid(isValid);
    }, [formData]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    const handlePurchase = async () => {
        if (isFormValid) {
            setLoading(true); // Show spinner
            try {
                const token = localStorage.getItem('authToken');
                if (!token) {
                    console.error('Kullanıcı giriş yapmamış.');
                    return;
                }

                const response = await apiClient.post('/purchase/cart-purchase', {
                    cardNumber: formData.cardNumber,
                    cardHolder: formData.cardHolder,
                    expirationDate: formData.expirationDate,
                    cvv: formData.cvv,
                    address: `${formData.address}, ${formData.city}, ${formData.zipCode}`
                }, {
                    headers: {
                        'Authorization': `${token}`
                    }
                });

                if (response.status === 200) {
                    clearCart();
                    setPurchaseSuccess(true);
                    setInvoiceData(response.data);
                } else {
                    alert('Satın alma sırasında bir sorun oluştu.');
                }
            } catch (error) {
                console.error('Satın alma işlemi sırasında hata oluştu:', error);
                alert('Satın alma işlemi başarısız oldu. Lütfen tekrar deneyin.');
            } finally {
                setLoading(false); // Hide spinner after response
            }
        } else {
            alert('Lütfen formu eksiksiz doldurun.');
        }
    };

    if (loading) {
        return (
            <div className="spinner-container">
                <CircularProgress color="success" size={60} />
                <p className="loading-message">Wait a while for the approval of the transaction...</p>
            </div>
        );
    }

    return (
        <div className="purchase-container">
            {!purchaseSuccess ? (
                <>
                    <form className="purchase-form">
                        <div className="form-group">
                            <label htmlFor="cardNumber">Card Number</label>
                            <input
                                type="text"
                                id="cardNumber"
                                name="cardNumber"
                                value={formData.cardNumber}
                                onChange={handleInputChange}
                                placeholder="#### #### #### ####"
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="cardHolder">Card Holder</label>
                            <input
                                type="text"
                                id="cardHolder"
                                name="cardHolder"
                                value={formData.cardHolder}
                                onChange={handleInputChange}
                                placeholder="Cardholder Name"
                            />
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="expirationDate">Expiration Date</label>
                                <input
                                    type="text"
                                    id="expirationDate"
                                    name="expirationDate"
                                    value={formData.expirationDate}
                                    onChange={handleInputChange}
                                    placeholder="MM/YY"
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="cvv">CVV</label>
                                <input
                                    type="text"
                                    id="cvv"
                                    name="cvv"
                                    value={formData.cvv}
                                    onChange={handleInputChange}
                                    placeholder="CVV"
                                />
                            </div>
                        </div>
                        <div className="form-group">
                            <label htmlFor="address">Address</label>
                            <input
                                type="text"
                                id="address"
                                name="address"
                                value={formData.address}
                                onChange={handleInputChange}
                                placeholder="Address Line"
                            />
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="city">City</label>
                                <input
                                    type="text"
                                    id="city"
                                    name="city"
                                    value={formData.city}
                                    onChange={handleInputChange}
                                    placeholder="City"
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="zipCode">Zip Code</label>
                                <input
                                    type="text"
                                    id="zipCode"
                                    name="zipCode"
                                    value={formData.zipCode}
                                    onChange={handleInputChange}
                                    placeholder="Zip Code"
                                />
                            </div>
                        </div>
                        <button
                            type="button"
                            className="purchase-button"
                            onClick={handlePurchase}
                            disabled={!isFormValid}
                        >
                            Satın Al
                        </button>
                    </form>
                    <div className='order-summary-cart-holder'>
                        <OrderSummaryCart />
                    </div>
                    <CreditCard
                        cardNumber={formData.cardNumber}
                        cardHolder={formData.cardHolder}
                        expirationDate={formData.expirationDate}
                    />
                </>
            ) : (
                <div className="invoice-container">
                    <div className="invoice-header">
                        <h3>Satın Alma Başarılı!</h3>
                        <p>Teşekkür ederiz, siparişiniz onaylandı.</p>
                    </div>
                    <div className="invoice-details">
                        <h4>Fatura Bilgileri</h4>
                        <p><strong>Ad:</strong> {invoiceData.name}</p>
                        <p><strong>Soyad:</strong> {invoiceData.surname}</p>
                        <p><strong>Adres:</strong> {invoiceData.address}</p>
                        <p><strong>E-posta:</strong> {invoiceData.email}</p>
                        <p><strong>Kart Numarası:</strong> **** **** **** {invoiceData.cardNumber.slice(-4)}</p>
                        <p><strong>Toplam Tutar:</strong> {invoiceData.totalAmount} ₺</p>
                    </div>
                    <div className="invoice-products">
                        <h4>Satın Alınan Ürünler</h4>
                        <ul>
                            {invoiceData.purchasedItems && invoiceData.purchasedItems.map((item, index) => (
                                <li key={index}>{item}</li>
                            ))}
                        </ul>
                    </div>
                    <button className="redirect-button" onClick={() => window.location.href = '/products'}>
                        Alışverişe Devam Et
                    </button>
                </div>
            )}
        </div>
    );
};

export default PurchaseForm;
