import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import { Link } from 'react-router-dom';
import SearchBar from '../components/SearchBar/SearchBar';
import ProductManagerAddProductFull from '../components/ProductManagerAddProductFull/ProductManagerAddProductFull';

const AddProductPage = () => {
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
    <div className="add-product-page">
        <Navbar navElements={navElements} />
        <ProductManagerAddProductFull/>
        <Footer />
    </div>
  );
};

export default AddProductPage;
