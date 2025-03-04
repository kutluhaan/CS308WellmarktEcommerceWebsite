import React from "react";
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import SignIn from "../components/SignIn/SignIn";
import SearchBar from '../components/SearchBar/SearchBar';
import { FaCartShopping } from "react-icons/fa6";
import { Link } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Badge from '@mui/material/Badge';
import { useCart } from '../contexts/CartContext';

function SignInPage() {
  const { cart } = useCart();
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
  const navElements = [
    <Link to="/" className="route">Home</Link>,
    <Link to='/products' className='route'>Products</Link>,
    <Link to="/about" className="route">About</Link>,
    <Link to="/contact" className="route">Contact</Link>,
    <Link to="/sign-up" className="route">Sign Up</Link>,
    <SearchBar navigation={"/products"} />,
    <div className='badge-and-cart'>
    <Badge badgeContent={totalItems} color="info">
      <a href="/cart" className="shopping-cart"><FaCartShopping /></a>
    </Badge>
  </div>
  ];


  return (
    <div className= "sign-in-page" style={{height: "100vh"}}>
      <Navbar navElements={navElements}/>
      <Toaster position='top-center' toastOptions={{duration: 1300}} />
      <SignIn/>
      <Footer/> 
    </div>
  );
}

export default SignInPage;
