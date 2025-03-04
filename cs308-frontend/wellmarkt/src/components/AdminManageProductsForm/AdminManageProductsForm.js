import React, { useState, useEffect } from "react";
import "./AdminManageProductsForm.css";
import Select from "react-select"; // Import React Select
import apiClient from "../../api/axios";

const AdminManageProductsForm = ({ fetchProducts }) => {
  const [categories, setCategories] = useState([]); // Categories for Select
  const [selectedCategories, setSelectedCategories] = useState([]); // Selected categories
  const [loadingCategories, setLoadingCategories] = useState(true); // Loading state
  const [error, setError] = useState(false); // Error state

  const [productData, setProductData] = useState({
    name: "",
    description: "",
    imageURL: "",
    price: 0.0,
    stock: 0,
    isActive: true,
    model: "",
    serialNumber: "",
    warrantyStatus: false,
    distributorInfo: "",
    rating: 0.0,
    ratingCount: 0,
    category: [], // Backend expects this as IDs
    brand: "",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  });

  // Fetch categories on mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await apiClient.get("/categories/get-all-categories");
        if (response.status === 200) {
          const transformedCategories = transformCategories(response.data.categories);
          setCategories(transformedCategories);
        }
      } catch (error) {
        setError(true);
        console.error("Error fetching categories:", error);
      } finally {
        setLoadingCategories(false);
      }
    };

    fetchCategories();
  }, []);

  // Transform raw categories into a flat structure for React Select
  const transformCategories = (categories) => {
    return categories.map((category) => ({
      label: category.catName,
      value: category.catID,
    }));
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setProductData({
      ...productData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const handleCategoryChange = (selectedOptions) => {
    const transformedSelection = selectedOptions.map((option) => ({
      catID: option.value,
      catName: option.label,
    }));
    setSelectedCategories(transformedSelection);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...productData,
      category: selectedCategories.map((cat) => cat.catID), // Only send category IDs
      createdAt: new Date().toISOString(), // Ensure timestamps are sent
      updatedAt: new Date().toISOString(),
    };

    // Log payload for debugging
    console.log("Payload sent to backend:", payload);

    try {
      const response = await apiClient.post("/admin/products", payload);

      if (response.status === 201) {
        alert("Product added successfully!");
        fetchProducts(); // Refresh the product list
        setProductData({
          name: "",
          description: "",
          imageURL: "",
          price: 0.0,
          stock: 0,
          isActive: true,
          model: "",
          serialNumber: "",
          warrantyStatus: false,
          distributorInfo: "",
          rating: 0.0,
          ratingCount: 0,
          category: [],
          brand: "",
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        });
        setSelectedCategories([]);
      } else {
        alert("Failed to add product.");
        console.error("Error response from backend:", response);
      }
    } catch (error) {
      console.error("Error adding product:", error.response?.data || error.message);
    }
  };

  return (
    <form className="manage-product-form" onSubmit={handleSubmit}>
      <h2>Add Product</h2>

      <label>
        Name:
        <input
          type="text"
          name="name"
          value={productData.name}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Description:
        <textarea
          name="description"
          value={productData.description}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Image URL:
        <input
          type="text"
          name="imageURL"
          value={productData.imageURL}
          onChange={handleChange}
        />
      </label>

      <label>
        Price:
        <input
          type="number"
          step="0.01"
          name="price"
          value={productData.price}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Stock:
        <input
          type="number"
          name="stock"
          value={productData.stock}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Active:
        <input
          type="checkbox"
          name="isActive"
          checked={productData.isActive}
          onChange={handleChange}
        />
      </label>

      <label>
        Brand:
        <input
          type="text"
          name="brand"
          value={productData.brand}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Model:
        <input
          type="text"
          name="model"
          value={productData.model}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Serial Number:
        <input
          type="text"
          name="serialNumber"
          value={productData.serialNumber}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Warranty Status:
        <input
          type="checkbox"
          name="warrantyStatus"
          checked={productData.warrantyStatus}
          onChange={handleChange}
        />
      </label>

      <label>
        Distributor Info:
        <textarea
          name="distributorInfo"
          value={productData.distributorInfo}
          onChange={handleChange}
        />
      </label>

      <label>
        Categories:
        {loadingCategories ? (
          <p>Loading categories...</p>
        ) : error ? (
          <p style={{ color: "red" }}>Failed to load categories.</p>
        ) : (
          <Select
            options={categories}
            isMulti
            onChange={handleCategoryChange}
            value={selectedCategories.map((cat) => ({
              label: cat.catName,
              value: cat.catID,
            }))}
            placeholder="Select categories..."
          />
        )}
      </label>

      <button type="submit" className="submit-button">
        Add Product
      </button>
    </form>
  );
};

export default AdminManageProductsForm;
