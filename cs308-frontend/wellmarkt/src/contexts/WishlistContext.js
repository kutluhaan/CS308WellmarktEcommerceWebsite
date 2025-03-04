import React, { createContext, useState, useEffect, useContext } from "react";
import apiClient from "../api/axios"; // Adjust the path as needed

const WishlistContext = createContext();

export const WishlistProvider = ({ children }) => {
  const [wishlist, setWishlist] = useState(() => {
    try {
      const savedWishlist = localStorage.getItem("wishlist");
      return savedWishlist ? JSON.parse(savedWishlist) : [];
    } catch (error) {
      console.error("Error reading wishlist from localStorage:", error);
      return [];
    }
  });


  const token = localStorage.getItem("authToken");
  useEffect(() => {
    const fetchWishlist = async () => {
      try {
        const authToken = localStorage.getItem("authToken");
        const response = await apiClient.get("wishlist/get-user-wishlist", {
          headers: {
            Authorization: `${authToken}`,
          },
        });

        if (response.status === 200 || response.status === 201) {
          setWishlist(response.data.wishlist|| []);
        } else {
          console.error("Failed to fetch wishlist. Status:", response.status);
        }
      } catch (error) {
        console.error("Error fetching wishlist:", error);
      }
    };

    // If wishlist is empty, fetch it from the backend
    fetchWishlist();
    
  }, []);

  useEffect(() => {
    try {
      localStorage.setItem("wishlist", JSON.stringify(wishlist));
    } catch (error) {
      console.error("Error saving wishlist to localStorage:", error);
    }
  }, [wishlist]);

  const addToWishlist = (productID) => {
    if (!productID) {
      console.error("Invalid productID.");
      return;
    }

    setWishlist((prevWishlist) => {
      if (prevWishlist.includes(productID)) {
        console.warn("Product already in wishlist.");
        return prevWishlist;
      }

      apiClient.post(

        "wishlist/add", null,
        
        {params: {'productId':productID},
        
        headers: {
        
        'Authorization': `${token}`,
        
        },
        
        }
        
        );

      return [...prevWishlist, productID];
    });
  };

  const removeFromWishlist = (productID) => {
    console.log("deleting"+`${productID}`)
    apiClient.delete(

      "wishlist/remove",
      
      {params: {'productId':productID},
      
      headers: {
      
      'Authorization': `${token}`,
      
      },
      
      }
      
      );
    setWishlist((prevWishlist) =>
      prevWishlist.filter((id) => id !== productID)
    );
  };

  const getWishlist = async () => {
    // If wishlist is empty, fetch it from the backend first
    const fetchWishlist = async () => {
      try {
        const authToken = localStorage.getItem("authToken");
        const response = await apiClient.get("wishlist/get-user-wishlist", {
          headers: {
            Authorization: `${authToken}`,
          },
        });

        if (response.status === 200 || response.status === 201) {
          setWishlist(response.data.wishlist || []);
        } else {
          console.error("Failed to fetch wishlist. Status:", response.status);
        }
      } catch (error) {
        console.error("Error fetching wishlist:", error);
      }
    };  // Wait for the fetch to complete
    fetchWishlist();
    return wishlist;  // Now return the updated wishlist
  };

  const clearWishlist = () => {
    setWishlist([]);
  };

  return (
    <WishlistContext.Provider
      value={{ wishlist, addToWishlist, removeFromWishlist, getWishlist, clearWishlist }}
    >
      {children}
    </WishlistContext.Provider>
  );
};

// Custom hook to access the wishlist context
export const useWishlist = () => {
  const context = useContext(WishlistContext);
  if (!context) {
    throw new Error("useWishlist must be used within a WishlistProvider");
  }
  return context;
};
