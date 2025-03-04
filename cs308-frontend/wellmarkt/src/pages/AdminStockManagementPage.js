import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import AdminStockManagement from '../components/AdminStockManagement/AdminStockManagement';
import { Link } from 'react-router-dom';
import SearchBar from '../components/SearchBar/SearchBar';

function AdminStockManagementPage() {
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
    <div className="stock-management-page">
      <Navbar navElements={navElements} />
      <AdminStockManagement />
      <Footer />
    </div>
  );
}

export default AdminStockManagementPage;
