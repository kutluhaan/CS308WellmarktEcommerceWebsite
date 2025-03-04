import React, { createContext, useState, useEffect, useContext } from "react";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  // Initialize cart from localStorage or default to an empty array
  const [cart, setCart] = useState(() => {
    try {
      const savedCart = localStorage.getItem("cart");
      return savedCart ? JSON.parse(savedCart) : [];
    } catch (error) {
      console.error("Error reading cart from localStorage:", error);
      return [];
    }
  });

  // Persist cart to localStorage whenever it changes
  useEffect(() => {
    try {
      localStorage.setItem("cart", JSON.stringify(cart));
    } catch (error) {
      console.error("Error saving cart to localStorage:", error);
    }
  }, [cart]);

const addToCart = (productID, quantity) => {
  if (!productID) {
    console.error("Invalid productID.");
    return;
  }

  // Ensure productID is stored as a string, not a full object
  const productIDValue = typeof productID === "object" ? productID.pID : productID;

  setCart((prevCart) => {
    const existingProduct = prevCart.find((item) => item.productID === productIDValue);
    if (existingProduct) {
      return prevCart.map((item) =>
        item.productID === productIDValue
          ? { ...item, quantity: item.quantity + quantity }
          : item
      );
    }
    return [...prevCart, { productID: productIDValue, quantity }];
  });
};

const removeFromCart = (productID) => {
  setCart((prevCart) =>
    prevCart.filter((item) => item.productID !== productID)
  );
};


  // Retrieve the current cart
  const getCart = () => cart;

  // Manually set the cart (useful for syncing with the server)
  const setCartManually = (newCart) => {
    if (Array.isArray(newCart)) {
      setCart(newCart);
    } else {
      console.error("Cart must be an array of items.");
    }
  };

  // Clear the cart
  const clearCart = () => {
    setCart([]);
  };

  return (
    <CartContext.Provider value={{ cart, addToCart, getCart, setCartManually, clearCart, removeFromCart }}>
      {children}
    </CartContext.Provider>
  );
};

// Custom hook to access the cart context
export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};