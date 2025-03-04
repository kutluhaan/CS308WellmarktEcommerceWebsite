import React, { useEffect, useState } from 'react';
import { useCart } from '../../contexts/CartContext';
import { useContext } from 'react';
import { ProductsContext } from '../../contexts/ProductsContext';
import { FaRegTrashCan, FaPlus, FaMinus } from "react-icons/fa6";
import './OrderSummaryCart.css';

const OrderSummaryCart = () => {
  const { cart, removeFromCart, addToCart } = useCart(); // Get cart from context
  const { products } = useContext(ProductsContext); // Get products from context
  const [cartDetails, setCartDetails] = useState([]);

  useEffect(() => {
    // Enrich cart with product details
    const enrichedCart = cart.map((item) => {
      const product = products.find((prod) => prod.pID === item.productID);
      return product
        ? { ...item, ...product } // Merge product details
        : { ...item, name: "Unavailable", price: 0, stock: 0 }; // Fallback if product is not found
    });

    setCartDetails(enrichedCart);
  }, [cart, products]);

  const totalPrice = cartDetails.reduce((total, item) => total + item.quantity * (item.price * (1 - (item.discountPercent || 0) / 100)), 0).toFixed(2);

  const handleRemoveItem = (productID) => {
    removeFromCart(productID);
  };

  const handleIncreaseQuantity = (productID) => {
    const item = cartDetails.find((item) => item.pID === productID);
    if (item) {
      addToCart(productID, 1); // Increase quantity
    }
  };

  const handleDecreaseQuantity = (productID) => {
    const item = cartDetails.find((item) => item.pID === productID);
    if (item && item.quantity > 1) {
      addToCart(productID, -1); // Decrease quantity
    }
  };

  return (
    <div className="order-summary">
      <h3>Summary of Your Order</h3>
      <ul className="order-items">
        {cartDetails.map((item, index) => (
          <li key={index} className="order-item">
            <span>{item.name}</span>
            <span>Amount: {item.quantity}</span>
            <span className="item-price" style={{ textAlign: 'right' }}>Price: {(
              (item.price * (1 - (item.discountPercent || 0) / 100)).toFixed(2)
              )} TL</span>
            <div className="order-item-actions">
              <FaPlus 
                className="action-button" 
                onClick={item.stock > item.quantity ? () => handleIncreaseQuantity(item.pID) : null}
                title="Increase Quantity"
              />
              {item.quantity > 1 && (
                <FaMinus
                  className="action-button" 
                  onClick={() => handleDecreaseQuantity(item.pID)} 
                  title="Decrease Quantity"
                />
              )}
              <FaRegTrashCan 
                className="action-button" 
                onClick={() => handleRemoveItem(item.pID)} 
                title="Remove Item"
              />
            </div>
          </li>
        ))}
      </ul>
      <div className="order-total">
        <span>Total:</span>
        <span>{totalPrice} TL</span>
      </div>
    </div>
  );
};

export default OrderSummaryCart;