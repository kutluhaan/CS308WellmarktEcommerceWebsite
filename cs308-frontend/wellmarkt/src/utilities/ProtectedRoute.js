import React from "react";
import { useAuth } from "../contexts/AuthContext";
import { Navigate } from "react-router-dom";
import { isTokenExpired } from "../utilities/jwtUtility"; // Import the token expiration check

const ProtectedRoute = ({ allowedRoles, children }) => {
  const { userRole } = useAuth(); // Get the role from context
    console.log("ROLE: "+ userRole);

  // Check if user role exists and matches the allowed roles
  if (!userRole) {
    console.log("No user role found.");
    return <Navigate to="/unauthorized" replace />; // Redirect to login if no role
  }

  // Check if the token is expired
  if (isTokenExpired()) {
    console.log("Token has expired.");
    return <Navigate to="/sign-in" replace />; // Redirect to login if token is expired
  }

  if (!allowedRoles.includes(userRole)) {
    console.log("Redirecting.");
    return <Navigate to="/unauthorized" replace />; // Redirect if role is unauthorized
  }

  return children; // Render the protected page if the role matches
};

export default ProtectedRoute;