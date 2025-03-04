import React from "react";
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import { Link } from "react-router-dom";
import NavbarProfilePageIcon from "../components/NavbarProfilePageIcon/NavbarProfilePageIcon";
import DiscountSettingForm from "../components/DiscountSettingForm/DiscountSettingForm";
import SearchBar from "../components/SearchBar/SearchBar";

function DiscountSettingPage () {
  const commonNavElements = [
    <Link to="/sales-manager" className='route'>Dashboard</Link>,    
    <Link to='/sales-manager/sm-all-prods' className='route'>Products</Link>,
    <SearchBar navigation={"/sales-manager/set-discount"} />,
  ];

  const authToken = localStorage.getItem('authToken');
  const userNavElements = authToken ? [
    <Link className="route"><NavbarProfilePageIcon/></Link>,
    <Link to="/" className="route" onClick={() => {localStorage.removeItem('authToken');}}>Sign Out</Link>,
  ] : [
    <Link to="/sign-in" className="route">Sign In</Link>,
    <Link to='/sign-up' className='route'>Sign Up</Link>,
  ];

  const navElements = [...commonNavElements, ...userNavElements];
  
  return (
      <div className= "contact-page">
        <Navbar navElements={navElements}/>
        <DiscountSettingForm/>
        <Footer/> 
      </div>
    );
}
export default DiscountSettingPage;