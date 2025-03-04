// src/components/Purchase/CreditCard.js
import React from 'react';
import './CreditCard.css';

const CreditCard = ({ cardNumber, cardHolder, expirationDate }) => {
    const formattedCardNumber = cardNumber
        ? cardNumber.replace(/(\d{4})(?=\d)/g, '$1 ')
        : '                    ';

    return (
        <div className="credit-card">
            <div className="card-chip"></div>
            <div className="card-number">{formattedCardNumber}</div>
            <div className="card-details">
                <div className="card-holder">
                    <span style={{fontSize: '15px'}}>Card Holder</span>
                    <span>{cardHolder || 'Full Name'}</span>
                </div>
                <div className="card-expiration">
                    <span style={{fontSize: '15px'}}>Expires</span>
                    <span>{expirationDate || 'MM/YY'}</span>
                </div>
            </div>
        </div>
    );
};

export default CreditCard;
