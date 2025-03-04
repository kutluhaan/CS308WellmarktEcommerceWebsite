import React, { useState } from "react";
import "./DeleteProductForm.css";
import axios from "axios";

const DeleteProductForm = ({ fetchProducts }) => {
    const [productId, setProductId] = useState("");

    const handleDelete = async (e) => {
        e.preventDefault();
        try {
            await axios.delete(`/api/product-manager/products/${productId}`);
            alert("Product deleted successfully!");
            setProductId("");
            fetchProducts();
        } catch (error) {
            console.error("Failed to delete product:", error);
            alert("Failed to delete product. Please check the product ID.");
        }
    };

    return (
        <form className="admin-delete-product-form" onSubmit={handleDelete}>
            <h2>Delete Product</h2>
            <label>
                Product ID:
                <input
                    type="text"
                    value={productId}
                    onChange={(e) => setProductId(e.target.value)}
                    required
                />
            </label>
            <button type="submit" className="admin-delete-button">
                Delete Product
            </button>
        </form>

    );
};

export default DeleteProductForm;
