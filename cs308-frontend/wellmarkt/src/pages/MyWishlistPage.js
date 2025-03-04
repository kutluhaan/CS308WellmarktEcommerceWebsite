import React from 'react';
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import SearchBar from '../components/SearchBar/SearchBar';
import { FaCartShopping } from "react-icons/fa6";
import { Link } from 'react-router-dom';
import Badge from '@mui/material/Badge';
import { useCart } from '../contexts/CartContext';
import NavbarProfilePageIcon from '../components/NavbarProfilePageIcon/NavbarProfilePageIcon';
import MyWishList from '../components/MyWishlist/MyWishlist';

function MyWishListPage() {
  const { cart } = useCart();
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);

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

  const authToken = localStorage.getItem('authToken');
  const userNavElements = authToken ? [
    <Link className="route"><NavbarProfilePageIcon/></Link>,
    <Link to="/" className="route" onClick={() => { localStorage.removeItem('authToken'); window.location.reload();}}>Sign Out</Link>,
  ] : [
    <Link to="/sign-in" className="route">Sign In</Link>,
    <Link to="/sign-up" className="route">Sign Up</Link>,
  ];

  const navElements = [...commonNavElements, ...userNavElements];

  return (
    <div className="home-page">
        <Navbar navElements={navElements}/>
        <MyWishList/>
        <Footer/> 
    </div>
  );
}

export default MyWishListPage;