import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useAuth } from "../contexts/AuthContext";

const styles = {
  container: {
    minHeight: '100vh',
    background: 'linear-gradient(to bottom, #c4dcfe, #FFCEE6)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '1rem'
  },
  card: {
    maxWidth: '440px',
    width: '100%',
    textAlign: 'center'
  },
  innerCard: {
    background: 'white',
    borderRadius: '8px',
    padding: '2rem',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
    marginBottom: '1.5rem'
  },
  iconContainer: {
    marginBottom: '1.5rem',
    animation: 'spin 2s linear infinite'
  },
  icon: {
    width: '64px',
    height: '64px',
    color: '#2563eb'
  },
  title: {
    fontSize: '1.5rem',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '1rem'
  },
  message: {
    color: '#4b5563',
    marginBottom: '1.5rem',
    lineHeight: '1.5'
  },
  '@keyframes spin': {
    '0%': { transform: 'rotate(0deg)' },
    '100%': { transform: 'rotate(360deg)' }
  }
};

const RoleResolver = () => {
  const navigate = useNavigate();
  const { updateRole } = useAuth();
  const [status, setStatus] = useState("Preparing your workspace...");

  const LoadingIcon = () => (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      style={styles.icon}
    >
      <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z" />
      <path d="M12 6v2" strokeLinecap="round" />
    </svg>
  );

  useEffect(() => {
    const resolveRole = async () => {
      const token = localStorage.getItem("authToken");
      
      if (!token) {
        setStatus("No active session found. Redirecting to login...");
        setTimeout(() => navigate("/sign-in"), 1500);
        return;
      }

      try {
        setStatus("Verifying your credentials...");
        const decodedToken = jwtDecode(token.substring(7));
        const role = decodedToken.role;
        console.log(role);

        if (!role) {
          setStatus("Unable to verify access level. Redirecting...");
          setTimeout(() => navigate("/unauthorized"), 1500);
          return;
        }

        updateRole(role);
        setStatus(`Welcome back! Just a blink and you’ll be there…`);

        const roleRoutes = {
          customer: "/",
          "ROLE_salesManager": "/sales-manager",
          "productManager": "/product-manager",
          admin: "/admin",
        };

        const targetRoute = roleRoutes[role];
        if (targetRoute) {
          setTimeout(() => navigate(targetRoute), 1000);
        } else {
          setStatus("Access level not recognized. Redirecting...");
          setTimeout(() => navigate("/unauthorized"), 1500);
        }
      } catch (error) {
        setStatus("Session expired. Redirecting to login...");
        setTimeout(() => navigate("/sign-in"), 1500);
      }
    };

    resolveRole();
  }, [navigate, updateRole]);

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.innerCard}>
          <div style={styles.iconContainer}>
            <LoadingIcon />
          </div>
          <h1 style={styles.title}>
            Just a moment...
          </h1>
          <p style={styles.message}>
            {status}
          </p>
        </div>
      </div>
    </div>
  );
};

export default RoleResolver;