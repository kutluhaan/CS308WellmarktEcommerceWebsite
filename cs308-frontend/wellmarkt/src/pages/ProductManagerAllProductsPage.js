import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import { Link } from 'react-router-dom';
import ProductManagerAllProducts from '../components/ProductManagerAllProducts/ProductManagerAllProducts';
import SearchBar from '../components/SearchBar/SearchBar';

const PriceSettingPage = () => {
  const commonNavElements = [
    <Link to="/product-manager" className='route'>Dashboard</Link>,
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
    <div className="product-manager-all-products-page">
        <Navbar navElements={navElements} />
        <ProductManagerAllProducts/>
        <Footer />
    </div>
  );
};

export default PriceSettingPage;
