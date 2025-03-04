import React from 'react';
import Navbar from '../components/Navbar/Navbar'; // Navbar component
import Footer from '../components/Footer/Footer'; // Footer component
import AdminPageButtons from '../components/AdminPageButtons/AdminPageButtons'; // Buttons for Admin actions
import { Link } from 'react-router-dom';
import SearchBar from '../components/SearchBar/SearchBar';

const AdminPage = () => {
    const commonNavElements = [
      <Link to="/admin" className='route'>Dashboard</Link>,
      <Link to="/admin/all-products" className="route">Products</Link>,
      <SearchBar navigation={"/admin/set-price"} />,
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
    <div className="admin-page">
      <Navbar navElements={navElements} />
      <AdminPageButtons />
      <Footer />
    </div>
  );
};

export default AdminPage;
