import React from "react";
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import { Link } from "react-router-dom";
import SalesManagerDashBoard from "../components/SalesManagerDashBoard/SalesManagerDashBoard";
import SearchBar from "../components/SearchBar/SearchBar";

function SalesManagerDashBoardPage () {
  const commonNavElements = [
    <Link to="/sales-manager/sm-all-prods" className='route'>Products</Link>,
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
      <div className= "sales-manager-page">
        <Navbar navElements={navElements}/>
        <SalesManagerDashBoard/>
        <Footer/> 
      </div>
    );
}
export default SalesManagerDashBoardPage;