import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import CommentManagement from '../components/CommentManageTable/CommentManageTable';
import { Link } from 'react-router-dom';
import SearchBar from '../components/SearchBar/SearchBar';

const CommentManagementPage = () => {
  const commonNavElements = [
    <Link to="/product-manager" className='route'>Dashboard</Link>,
    <Link to="/product-manager/all-products" className="route">Products</Link>,
    <SearchBar navigation={"/product-manager/all-products"} />,
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
    <div className="comments-page">
      <Navbar navElements={navElements} />
      <CommentManagement />
      <Footer />
    </div>
  );
};

export default CommentManagementPage;
