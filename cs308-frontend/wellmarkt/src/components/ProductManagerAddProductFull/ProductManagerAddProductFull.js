import React, { useState, useEffect } from "react";
import axios from "axios";
import "./ProductManagerAddProductFull.css";
import AddProductForm from "../AddProductForm/AddProductForm";
const ProductManagerAddProductFull = () => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await axios.get("/api/product-manager/products");
      setProducts(response.data.products);
    } catch (error) {
      console.error("Failed to fetch products:", error);
    }
  };

  const handleDelete = async (productId) => {
    try {
      await axios.delete(`/api/product-manager/products/${productId}`);
      alert("Product deleted successfully!");
      fetchProducts(); // Refresh the product list
    } catch (error) {
      console.error("Failed to delete product:", error);
      alert("Failed to delete product. Please try again.");
    }
  };

  return (
    <div className="admin-page-products">
    <div className="admin-products-container">
      {/* Forms Section */}
      <div className="form-container">
        <AddProductForm fetchProducts={fetchProducts} />
      </div>

      {/* Products Table */}
      <h1>Products</h1>
      <table className="admin-products-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Price (TL)</th>
            <th>Stock</th>
            <th>Category</th>
            <th>Actions</th>
          </tr>
        </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.pID}>
                <td>{product.pID}</td>
                <td>{product.name}</td>
                <td>{product.description}</td>
                <td>{product.price} TL</td>
                <td>{product.stock}</td>
                <td>
                  {/* Check if category exists */}
                  {product.category ? (
                    <>
                      {/* Display main category */}
                      <strong>{product.category.catName}</strong>
                      {/* Display subcategories if they exist */}
                      {product.category.subCats && product.category.subCats.length > 0 ? (
                        <span> ({product.category.subCats.join(", ")})</span>
                      ) : null}
                    </>
                  ) : (
                    <span>No categories</span> // Fallback if category is null/undefined
                  )}
                </td>
                <td>
                  <button
                    className="delete-button"
                    onClick={() => handleDelete(product.pID)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
      </table>
    </div>
    </div>
  );
};

export default ProductManagerAddProductFull;
