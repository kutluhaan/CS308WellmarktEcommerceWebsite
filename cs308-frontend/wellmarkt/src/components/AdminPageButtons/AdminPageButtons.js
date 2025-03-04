import React from 'react';
import { Link } from 'react-router-dom';
import './AdminPageButtons.css';

const AdminPageButtons = () => {
  return (
    <div className="admin-content">
      <h1 className="welcome">Welcome, Admin</h1>
      <div className="admin-buttons-container">
        <Link to="/admin/users">
          <button className="admin-button">Manage Users</button>
        </Link>
        <Link to="/admin/products">
          <button className="admin-button">Manage Products</button>
        </Link>
        <Link to="/admin/categories">
          <button className="admin-button">Manage Categories</button>
        </Link>
        <Link to="/admin/orders">
          <button className="admin-button">Order Management</button>
        </Link>
        <Link to="/admin/refunds">
          <button className="admin-button">Refund Requests</button>
        </Link>
        <Link to="/admin/stocks">
          <button className="admin-button">Stock Management</button>
        </Link>
        <Link to="/admin/comments">
          <button className="admin-button">Comment Management</button>
        </Link>
        <Link to="/admin/set-price">
          <button className="admin-button">Manage Product Prices</button>
        </Link>
        <Link to="/admin/set-discount">
          <button className="admin-button">Set Product Discounts</button>
        </Link>
        <Link to="/admin/profit-loss">
          <button className="admin-button">Revenue and Profit Calculation</button>
        </Link>
        <Link to="/admin/all-products">
          <button className="admin-button">All Products</button>
        </Link>
      </div>
    </div>
  );
};

export default AdminPageButtons;
