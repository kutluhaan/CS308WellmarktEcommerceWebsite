import React, { useState, useEffect } from "react";
import Divider from '@mui/material/Divider';
import './CategoryCards.css';
import apiClient from "../../api/axios";

function CategoryCard() {
    const [categories, setCategories] = useState([]);

    // Fetch categories from the backend
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await apiClient.get("categories/get-all-categories");
                if (response.status !== 200) {
                    throw new Error("Failed to fetch categories");
                }
                setCategories(response.data.categories);
            } catch (error) {
                console.error("Error fetching categories:", error);
            }
        };

        fetchCategories();
    }, []);

    return (
        <div className="category-card-container">
            {categories.length === 0 ? (
                <p className="no-category-message">No category available</p>
            ) : (
                categories.map((category, index) => (
                    <div 
                        className="category-card" 
                        key={index}
                    >
                        <div className="card-image-container">
                            <div className={`image-wrapper `}>
                                <img src={category.catImURL} className="category-image" alt={category.catName} />
                            </div>
                        </div>

                        <div className="card-text-container">
                            <a href={`/${category.catName}`}><h2 className="category-name">{category.catName}</h2></a>
                            <Divider />
                            <p className="quote">{category.catQuote}</p>
                            <span className="quote-owner-span">- {category.catQuoteOwner}</span>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}

export default CategoryCard;
