import React from 'react';
import { Link } from 'react-router-dom';
import './ProductManagerButtons.css';

const ProductManagerButtons = () => {
  return (
    <div className="product-manager-page">
      <div className="manager-content">
        <h1 className="welcome">Welcome, Product Manager</h1>
        <div className="manager-content-buttons">
          <Link to="/product-manager/comments">
            <button className="product-manager-button">Check Comments</button>
          </Link>
          <Link to="/product-manager/stocks">
            <button className="product-manager-button">Stock Control</button>
          </Link>
          <Link to="/product-manager/add-category">
            <button className="product-manager-button">Add Category</button>
          </Link>
          <Link to="/product-manager/delete-category">
            <button className="product-manager-button">Delete Category</button>
          </Link>
          <Link to="/product-manager/add-product">
            <button className="product-manager-button">Add Product</button>
          </Link>
          <Link to="/product-manager/delete-product">
            <button className="product-manager-button">Delete Product</button>
          </Link>
          <Link to="/product-manager/orders-management">
            <button className="product-manager-button">Manage Orders</button>
          </Link>
          <Link to="/product-manager/view-invoices">
            <button className="product-manager-button">View Invoices</button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ProductManagerButtons;
