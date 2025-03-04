import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import './CategoryProductCarts.css';
import SortOptions from '../SortingsDropdown/SortingsDropdown';
import Rating from '@mui/material/Rating';
import apiClient from '../../api/axios';

const CategoryProducts = () => {
    const { categoryName } = useParams();
    const [products, setProducts] = useState([]);
    const [sortedProducts, setSortedProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await apiClient.get(`products/category/${categoryName}`);
                setProducts(response.data.products);
                setSortedProducts(response.data.products); // Initialize sortedProducts
                setLoading(false);
            } catch (error) {
                console.error("Error fetching products:", error);
                setError("Error fetching products.");
                setLoading(false);
            }
        };

        fetchProducts();
    }, [categoryName]);

    const handleSort = (option) => {
        let sorted = [...products]; // Work with the original fetched products
        if (option === "price-low-to-high") {
            sorted.sort((a, b) => a.price - b.price);
        } else if (option === "price-high-to-low") {
            sorted.sort((a, b) => b.price - a.price);
        } else if (option === "popularity") {
            sorted.sort((a, b) => b.rating - a.rating); // Most to Least Popular
        } else if (option === "popularity-low-to-high") {
            sorted.sort((a, b) => a.rating - b.rating); // Least to Most Popular
        } else {
            sorted = [...products]; // Reset to default order
        }
        setSortedProducts(sorted);
    };

    if (loading) {
        return <div className="loading">Loading...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    return (
        <div className="category-products-container">
            <h2>Products in {categoryName}</h2>
            <SortOptions onSortChange={handleSort} /> {/* Pass handleSort to SortOptions */}
            <div className="product-grid">
                {sortedProducts.map((product) => (
                    <Link to={`/product/${product.pID}`} key={product.pID} className="product-link">
                        <div className="product-card">
                            <img
                                src={product.imageURL}
                                alt={product.name}
                                className="product-image"
                            />
                            <h3 className="product-name">{product.name}</h3>
                            <p className="product-price">{`${product.price} TL`}</p> 
                            <div className="product-rating">
                                <Rating
                                    name={`product-rating-${product.pID}`}
                                    value={product.rating}
                                    precision={0.5} // Half-star precision
                                    readOnly
                                />
                                <span className="rating-value">{`(${product.rating})`}</span>
                            </div>
                            <p className="product-description">{product.description}</p>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default CategoryProducts;
