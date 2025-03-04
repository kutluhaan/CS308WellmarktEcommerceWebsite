import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

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
    marginBottom: '1.5rem'
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
  buttonContainer: {
    display: 'flex',
    gap: '1rem',
    marginBottom: '1rem'
  },
  button: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    padding: '0.5rem 1rem',
    backgroundColor: '#2563eb',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    fontSize: '0.875rem',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'background-color 0.15s ease-in-out'
  },
  buttonHover: {
    backgroundColor: '#1d4ed8'
  },
  supportText: {
    fontSize: '0.875rem',
    color: '#6b7280'
  },
  arrowIcon: {
    width: '16px',
    height: '16px',
    marginRight: '0.5rem'
  }
};

const UnauthorizedPage = () => {
  const navigate = useNavigate();
  const [homeHovered, setHomeHovered] = useState(false);
  const [signInHovered, setSignInHovered] = useState(false);

  const HomeIcon = () => (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      style={styles.icon}
    >
      <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
    </svg>
  );

  const ArrowIcon = () => (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      style={styles.arrowIcon}
    >
      <path d="M10 19l-7-7m0 0l7-7m-7 7h18" />
    </svg>
  );

  const SignInIcon = () => (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      style={styles.arrowIcon}
    >
      <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M13.8 12H3" />
    </svg>
  );

  const handleHomeClick = async () => {
    const token = localStorage.getItem("authToken");

    if (token) {
      try {
        const decodedToken = jwtDecode(token.substring(7));
        const role = decodedToken.role;

        if (role) {
          const roleRoutes = {
            customer: "/",
            "ROLE_salesManager": "/sales-manager",
            "productManager": "/product-manager",
            admin: "/admin",
          };

          const targetRoute = roleRoutes[role];
          if (targetRoute) {
            navigate(targetRoute);
          } else {
            navigate("/");
          }
        } else {
          navigate("/");
        }
      } catch (error) {
        navigate("/");
      }
    } else {
      navigate("/");
    }
  };

  const handleSignInClick = () => {
    const token = localStorage.getItem("authToken");
    if (token) {
      localStorage.removeItem("authToken");
    }
    navigate("/sign-in");
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.innerCard}>
          <div style={styles.iconContainer}>
            <HomeIcon />
          </div>
          <h1 style={styles.title}>
            Looks like you've wandered a bit far
          </h1>
          <p style={styles.message}>
            Don't worry! Sometimes we all take unexpected detours.
            Let's get you back somewhere familiar.
          </p>
          <div style={styles.buttonContainer}>
            <button
              onClick={handleHomeClick}
              onMouseEnter={() => setHomeHovered(true)}
              onMouseLeave={() => setHomeHovered(false)}
              style={{
                ...styles.button,
                ...(homeHovered ? styles.buttonHover : {})
              }}
            >
              <ArrowIcon />
              <span>Return to Homepage</span>
            </button>
            <button
              onClick={handleSignInClick}
              onMouseEnter={() => setSignInHovered(true)}
              onMouseLeave={() => setSignInHovered(false)}
              style={{
                ...styles.button,
                ...(signInHovered ? styles.buttonHover : {})
              }}
            >
              <SignInIcon />
              <span>Sign In</span>
            </button>
          </div>
          <p style={styles.supportText}>
            If you believe you should have access to this page,
            please contact our support team for assistance.
          </p>
        </div>
        <p style={styles.supportText}>
          Need help finding something specific? Our support team is always here to help.
        </p>
      </div>
    </div>
  );
};

export default UnauthorizedPage;
