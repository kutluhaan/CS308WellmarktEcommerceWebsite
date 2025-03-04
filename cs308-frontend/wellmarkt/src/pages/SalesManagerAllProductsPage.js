import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import { Link } from 'react-router-dom';
import SalesManagerAllProducts from '../components/SalesManagerAllProducts/SalesManagerAllProducts';
import SearchBar from '../components/SearchBar/SearchBar';

const PriceSettingPage = () => {
  const commonNavElements = [
    <Link to="/sales-manager" className='route'>Dashboard</Link>,
    <SearchBar navigation={"/sales-manager/sm-all-prods"} />,
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
        <SalesManagerAllProducts/>
        <Footer />
    </div>
  );
};

export default PriceSettingPage;
