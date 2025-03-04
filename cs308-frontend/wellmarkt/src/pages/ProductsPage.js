import React, { useContext } from "react";
import Navbar from '../components/Navbar/Navbar';
import Footer from '../components/Footer/Footer';
import SearchBar from '../components/SearchBar/SearchBar';
import { FaCartShopping } from "react-icons/fa6";
import { Link, useNavigate } from 'react-router-dom';
import ProductCard from "../components/ProductCard/ProductCard";
import {ProductsContext} from '../contexts/ProductsContext';
import NavbarProfilePageIcon from "../components/NavbarProfilePageIcon/NavbarProfilePageIcon";
import Badge from '@mui/material/Badge';
import { useCart } from '../contexts/CartContext';
import SortOptions from "../components/SortingsDropdown/SortingsDropdown";
import { jwtDecode } from "jwt-decode";

function ProductsPage() {
  const navigate = useNavigate();
  const { cart } = useCart();
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
  const { resetFilteredProducts } = useContext(ProductsContext);
  const authToken = localStorage.getItem('authToken');

  const handleReset = () => {
    resetFilteredProducts();
    const event = new CustomEvent('resetSortDropdown');
    window.dispatchEvent(event);
  };

  let navElements;

  if (authToken) {
    try {
      const decodedToken = jwtDecode(authToken.substring(7));
      const role = decodedToken.role;

      if (role === "customer") {
        const commonNavElements = [
          <Link to="/" className="route">Home</Link>,
          <Link to="/products" className="route" onClick={handleReset}>Products</Link>,
          <Link to="/about" className="route">About</Link>,
          <Link to="/contact" className="route">Contact</Link>,
          <SortOptions />,
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
          <Link to="/products" className="route" onClick={handleReset}>Products</Link>,
          <SortOptions />,
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
          <Link to="/products" className="route" onClick={handleReset}>Products</Link>,
          <SortOptions />,
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
          <Link to="/products" className="route" onClick={handleReset}>Products</Link>,
          <SortOptions />,
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
      <div className="products-page">
        <Navbar navElements={navElements} />
        <ProductCard />
        <Footer /> 
      </div>
  );
}

export default ProductsPage;
