import React, { createContext, useContext, useState } from "react";
import { getUserRole } from "../utilities/jwtUtility";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  // Initialize the user role once from the token
  const [userRole, setUserRole] = useState(() => {
    try {
      return getUserRole() || null;
    } catch {
      return null;
    }
  });

  // Provide a method to update the role when needed
  const updateRole = (newRole) => {
    setUserRole(newRole);
  };

  return (
    <AuthContext.Provider value={{ userRole, updateRole }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use authentication context
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export default AuthProvider;