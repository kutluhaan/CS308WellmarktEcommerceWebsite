import React, { useState, useEffect } from 'react';
import "./Navbar.css";
import { Link } from 'react-router-dom';

const Navbar = ({ navElements = [] }) => {
    const [color, setColor] = useState(false);

    // Define changeColor outside of useEffect to ensure it's accessible
    const changeColor = () => {
        if (window.scrollY >= 90) {
            setColor(true);
        } else {
            setColor(false);
        }
    };

    useEffect(() => {
        // Add scroll event listener
        window.addEventListener('scroll', changeColor);
        // Cleanup function to remove event listener
        return () => window.removeEventListener('scroll', changeColor);
    }, []); // Empty dependency array ensures this runs only once

    return (
        <header className={color ? 'header header-bg' : 'header'}>
            <div className='logo-brand'>
                <Link to="/" className="brand">Wellmarkt</Link>
                <Link to="/" className="logo"></Link> 
            </div>
            <nav className='navbar'>
                {navElements.map((element, index) => (
                    <React.Fragment key={index}>{element}</React.Fragment>
                ))}
            </nav>
        </header>
    );
};

export default Navbar;