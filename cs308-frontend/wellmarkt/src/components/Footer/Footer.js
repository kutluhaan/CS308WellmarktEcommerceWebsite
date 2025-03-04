import React from 'react';
import './Footer.css'; 
import { ImLocation } from "react-icons/im";
import { MdEmail } from "react-icons/md";

const Footer = () => {
  return (
    <footer>
        <h1>Wellmarkt</h1>
        <p className='address'><ImLocation /> Orta Mahalle, Üniversite Caddesi No:27 Tuzla, 34956 İstanbul</p>
        <p className='email'><MdEmail /> info@wellmarkt.com</p>
        <p className='classic'>© {new Date().getFullYear()} Wellmarkt. All rights reserved.</p>
    </footer>
  );
};

export default Footer;
