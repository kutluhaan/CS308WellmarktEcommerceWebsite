
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import { Link } from "react-router-dom";
import RevenueCalculation from "../components/RevenueCalculation/RevenueCalculation";
import React from "react";
import SearchBar from '../components/SearchBar/SearchBar';


function RevenueCalculationPage() {
    const commonNavElements = [
      <Link to="/sales-manager" className='route'>Dashboard</Link>,
      <Link to="/sales-manager/sm-all-prods" className="route">Products</Link>,
      <SearchBar navigation={"/sales-manager/sm-all-prods"} />,
    ];
  
    const authToken = localStorage.getItem("authToken");
    const userNavElements = authToken
      ? [
          <Link to="/" className="route" onClick={() => { localStorage.removeItem("authToken"); }}>Sign Out</Link>,
        ]
      : [
          <Link to="/sign-in" className="route">Sign In</Link>,
          <Link to="/sign-up" className="route">Sign Up</Link>,
        ];
  
    const navElements = [...commonNavElements, ...userNavElements];
  
    return (
      <div className="contact-page">
        <Navbar navElements={navElements} />
        <RevenueCalculation />
        <Footer />
      </div>
    );
}
export default RevenueCalculationPage;