import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import { Link } from 'react-router-dom';
import AdminOrders from '../components/AdminOrders/AdminOrders';
import SearchBar from '../components/SearchBar/SearchBar';

const AdminOrdersPage = () => {
  const commonNavElements = [
    <Link to="/admin" className='route'>Dashboard</Link>,
    <Link to="/admin/all-products" className="route">Products</Link>,
    <SearchBar navigation={"/admin/all-products"} />,
  ];

  const authToken = localStorage.getItem('authToken');
  const userNavElements = authToken ? [
    <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
  ] : [
    <Link to="/sign-in" className="route">Sign In</Link>,
    <Link to='/sign-up' className='route'>Sign Up</Link>,
  ];

  const navElements = [...commonNavElements, ...userNavElements];
  
  return (
    <div className="price-setting-page">
        <Navbar navElements={navElements} />
        <AdminOrders/>
        <Footer />
    </div>
  );
};

export default AdminOrdersPage;
