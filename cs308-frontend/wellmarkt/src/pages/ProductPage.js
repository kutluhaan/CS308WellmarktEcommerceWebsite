import React from 'react';
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import SearchBar from '../components/SearchBar/SearchBar';
import { FaCartShopping } from "react-icons/fa6";
import { Link, useNavigate } from 'react-router-dom';
import ProductDetails from '../components/ProductDetails/ProductDetails';
import NavbarProfilePageIcon from '../components/NavbarProfilePageIcon/NavbarProfilePageIcon';
import Badge from '@mui/material/Badge';
import { useCart } from '../contexts/CartContext';
import { jwtDecode } from "jwt-decode";

function ProductPage() {
  const navigate = useNavigate();
  const { cart } = useCart();
  const authToken = localStorage.getItem('authToken');
  let navElements;
  if (authToken) {
    try {
      const decodedToken = jwtDecode(authToken.substring(7));
      const role = decodedToken.role;

      if (role === "customer") {
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
      
        const userNavElements = authToken ? [
          <Link className="route"><NavbarProfilePageIcon/></Link>,
          <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
        ] : [
          <Link to="/sign-in" className="route">Sign In</Link>,
          <Link to='/sign-up' className='route'>Sign Up</Link>,
        ];
      
        navElements = [...commonNavElements, ...userNavElements];
      } else if (role === "ROLE_salesManager") {
        const commonNavElements = [
          <Link to="/sales-manager" className='route'>Dashboard</Link>,
          <Link to="/sales-manager/sm-all-prods" className="route">Products</Link>,
          <SearchBar navigation={"/sales-manager/sm-all-prods"} />,
        ];
      
        const userNavElements = authToken ? [
          <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
        ] : [
          <Link to="/sign-in" className="route">Sign In</Link>,
          <Link to='/sign-up' className='route'>Sign Up</Link>,
        ];
      
        navElements = [...commonNavElements, ...userNavElements];
      } else if (role === "productManager") {
        const commonNavElements = [
          <Link to="/product-manager" className='route'>Dashboard</Link>,
          <Link to="/product-manager/all-products" className="route">Products</Link>,
          <SearchBar navigation={"/product-manager/all-products"} />,
        ];
      
        const userNavElements = authToken ? [
          <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
        ] : [
          <Link to="/sign-in" className="route">Sign In</Link>,
          <Link to='/sign-up' className='route'>Sign Up</Link>,
        ];
      
        navElements = [...commonNavElements, ...userNavElements];
      } else if (role === "admin") {
        const commonNavElements = [
          <Link to="/admin" className='route'>Dashboard</Link>,
          <Link to="/admin/all-products" className="route">Products</Link>,
          <SearchBar navigation={"/admin/all-products"} />,
        ];
      
        const userNavElements = authToken ? [
          <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
        ] : [
          <Link to="/sign-in" className="route">Sign In</Link>,
          <Link to='/sign-up' className='route'>Sign Up</Link>,
        ];
      
        navElements = [...commonNavElements, ...userNavElements];
      }
    } catch (error) {
      navigate("/");
    }
  } else {
    navigate("/");
  }
  return (
    <div className="product-page">
        <Navbar navElements={navElements}/>
        <ProductDetails/>
        <Footer/> 
    </div>
  );
}

export default ProductPage;