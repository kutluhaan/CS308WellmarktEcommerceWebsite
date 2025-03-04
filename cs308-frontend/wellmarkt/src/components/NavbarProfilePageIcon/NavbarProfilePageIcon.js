import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { IoPersonSharp } from "react-icons/io5";
import './NavbarProfilePageIcon.css';

function NavbarProfilePageIcon() {
  const [isDropdownOpen, setDropdownOpen] = useState(false);

  return (
    <div className="profile-dropdown" onMouseEnter={() => setDropdownOpen(true)} onMouseLeave={() => setDropdownOpen(false)}>
      <IoPersonSharp />
      {isDropdownOpen && (
        <div className="dropdown-content">        
            <Link to="/profile/my-account" className="dropdown-item">My Account</Link>
            <Link to="/profile/my-orders" className="dropdown-item">My Orders</Link>
            <Link to="/profile/my-wishlist" className="dropdown-item">My Wishlist</Link>
        </div>
      )}
    </div>
  );
}

export default NavbarProfilePageIcon;
