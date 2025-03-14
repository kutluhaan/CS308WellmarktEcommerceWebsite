import React from 'react';
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import AboutDialog from '../components/AboutDialog/AboutDialog';
import SearchBar from '../components/SearchBar/SearchBar';
import { FaCartShopping } from 'react-icons/fa6';
import { Link } from 'react-router-dom';
import Badge from '@mui/material/Badge';
import { useCart } from '../contexts/CartContext';
import NavbarProfilePageIcon from '../components/NavbarProfilePageIcon/NavbarProfilePageIcon';


const AboutPage = () => {
  const { cart } = useCart();
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
  const authToken = localStorage.getItem('authToken');
  const commonNavElements = [
    <Link to="/" className="route">Home</Link>,
    <Link to="/products" className="route">Products</Link>,
    <Link to="/about" className="route">About</Link>,
    <Link to="/contact" className="route">Contact</Link>,
    <SearchBar navigation={"/products"} />,
    <div className='badge-and-cart'>
      <Badge badgeContent={totalItems} color="info">
        <a href="/cart" className="shopping-cart"><FaCartShopping /></a>
      </Badge>
    </div>
  ];

  const userNavElements = authToken ? [
    <Link className="route"><NavbarProfilePageIcon/></Link>,
    <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
  ] : [
    <Link to="/sign-in" className="route">Sign In</Link>,
    <Link to='/sign-up' className='route'>Sign Up</Link>,
  ];

  const navElements = [...commonNavElements, ...userNavElements];
      

  return (
    <div className="comments-page">
      <Navbar navElements={navElements} />
      <div style={{ paddingTop: '80px', margin: '0 auto', maxWidth: '1200px' }}>
        <AboutDialog />
      </div>
      <Footer />
    </div>
  );
};

export default AboutPage;
