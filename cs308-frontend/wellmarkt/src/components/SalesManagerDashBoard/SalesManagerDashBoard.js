import React from 'react';
import { Link } from "react-router-dom";
import './SalesManagerDashBoard.css';

const SalesManagerDashBoard = () => {
  return (
    <div className="dashboard-container">
      <div className="title-container">
        <h1 className="dashboard-title">Sales Manager Dashboard</h1>
      </div>
      
      <div className="dashboard-content">
        <section className="task-section">
          <h2>Manage Product Prices</h2>
          <Link to="/sales-manager/set-price">
            <button className="dashboard-btn">Set Product Prices</button>
          </Link>
        </section>

        <section className="task-section">
          <h2>Revenue and Profit Calculation</h2>
          <Link to="/sales-manager/profit-loss">
            <button className="dashboard-btn">Calculate Revenue & Profit</button>
          </Link>
        </section>
        
        <section className="task-section">
          <h2>Set Product Discounts</h2>
          <Link to="/sales-manager/set-discount">
            <button className="dashboard-btn">Set Discounts</button>
          </Link>
        </section>
        
        <section className="task-section">
          <h2>View Invoices</h2>
          <Link to='/sales-manager/view-all-invoices'>
            <button className="dashboard-btn">View Invoices</button>
          </Link>
        </section>
        
        <section className="task-section">
          <h2>Refund Management</h2>
          <Link to='/sales-manager/refund-requests'>
            <button className="dashboard-btn">View Refund Requests</button>
          </Link>
        </section>
        
        <section className="task-section">
          <h2>Products</h2>
          <Link to='/sales-manager/sm-all-prods'>
            <button className="dashboard-btn">View All Products</button>
          </Link>
        </section>
      </div>
    </div>
  );
};

export default SalesManagerDashBoard;