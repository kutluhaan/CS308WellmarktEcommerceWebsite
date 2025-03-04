
import Navbar from '../components/Navbar/Navbar'
import Footer from '../components/Footer/Footer';
import { Link } from "react-router-dom";
import React from "react";
import AdminProfitLossCharts from '../components/AdminProfitLossCharts/AdminProfitLossCharts';
import SearchBar from '../components/SearchBar/SearchBar';

function AdminRevenueCalculationPage() {
  
    const commonNavElements = [
      <Link to="/admin" className='route'>Dashboard</Link>,
      <Link to="/admin/all-products" className="route">Products</Link>,
      <SearchBar navigation={"/admin/all-products"} />,
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
        {/* Our main component that shows the two charts (plus/minus logic) */}
        <AdminProfitLossCharts />
        <Footer />
      </div>
    );
}
export default AdminRevenueCalculationPage;