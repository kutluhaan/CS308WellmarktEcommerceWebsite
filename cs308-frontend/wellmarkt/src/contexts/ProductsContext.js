import React, { createContext, useState, useEffect } from 'react';
import apiClient from "../api/axios"

export const ProductsContext = createContext();

export const ProductsProvider = ({ children }) => {
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await apiClient.get('products/all-products');
                setProducts(response.data.products); 
                setFilteredProducts(response.data.products); 
            } catch (error) {
                console.error('Failed to fetch products:', error);
            }
        };

        if (products.length === 0) fetchProducts();
    }, [products]);

    const searchProducts = async (query) => {
        try {
            const response = await apiClient.get(`products/search?query=${query}`);
            setFilteredProducts(response.data.products); 
        } catch (error) {
            console.error("Failed to search products:", error);
        }
    };

    const resetFilteredProducts = () => {
        setFilteredProducts(products); // Reset to the full product list
    };

    const sortFilteredProducts = (option) => {
        let sortedProducts = [...filteredProducts];
        if (option === "price-low-to-high") {
            sortedProducts.sort((a, b) => a.price - b.price);
        } else if (option === "price-high-to-low") {
            sortedProducts.sort((a, b) => b.price - a.price);
        } else if (option === "popularity") {
            sortedProducts.sort((a, b) => b.rating - a.rating); // Most to Least Popular
        } else if (option === "popularity-low-to-high") {
            sortedProducts.sort((a, b) => a.rating - b.rating); // Least to Most Popular
        } else {
            sortedProducts = [...products];
        }
        setFilteredProducts(sortedProducts);
    };
    
    return (
        <ProductsContext.Provider
            value={{
                products,
                filteredProducts,
                searchProducts,
                resetFilteredProducts,
                sortFilteredProducts, // Added sorting function
            }}
        >
            {children}
        </ProductsContext.Provider>
    );
};
