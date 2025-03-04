import React from 'react';
import Navbar from '../components/Navbar/Navbar'; // Navbar component
import Footer from '../components/Footer/Footer'; // Footer component
import AdminProducts from '../components/AdminProducts/AdminProducts';
import { Link } from 'react-router-dom';
import SearchBar from '../components/SearchBar/SearchBar';

const AdminProductsPage = () => {
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
      <div className="admin-page-products">
        <Navbar navElements={navElements} />
        <AdminProducts />
        <Footer />
      </div>
    );
  };
export default AdminProductsPage;
