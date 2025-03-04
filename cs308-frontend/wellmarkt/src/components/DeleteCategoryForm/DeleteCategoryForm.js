import React, { useState, useEffect } from "react";
import "./DeleteCategoryForm.css";
import apiClient from "../../api/axios";

const DeleteCategoryForm = ({ fetchCategories }) => {
  const [categories, setCategories] = useState([]); // List of all categories
  const [selectedCategory, setSelectedCategory] = useState(""); // Selected main category
  const [selectedSubCategory, setSelectedSubCategory] = useState(""); // Selected subcategory
  const [subCategories, setSubCategories] = useState([]); // Subcategories of the selected main category
  const [error, setError] = useState(""); // Error message

  // Fetch categories on mount
  useEffect(() => {
    const fetchAllCategories = async () => {
      try {
        const response = await apiClient.get("/categories/get-all-categories");
        setCategories(response.data.categories);
      } catch (err) {
        console.error("Error fetching categories:", err);
        setError("Failed to load categories.");
      }
    };
    fetchAllCategories();
  }, []);

  // Handle category selection and update subcategories
  const handleCategoryChange = (e) => {
    const categoryId = e.target.value;
    setSelectedCategory(categoryId);

    // Find the selected category and set its subcategories
    const category = categories.find((cat) => cat.catID === categoryId);
    if (category) {
      setSubCategories(category.subCats || []);
    } else {
      setSubCategories([]);
    }

    // Reset selected subcategory
    setSelectedSubCategory("");
  };

  // Handle deleting the main category
  const handleDeleteCategory = async () => {
    if (!selectedCategory) {
      setError("Please select a main category to delete.");
      return;
    }
    try {
      await apiClient.delete(`/categories/delete-category/${selectedCategory}`);
      alert("Category deleted successfully!");
      fetchCategories(); // Refresh the categories list
      setSelectedCategory("");
      setSubCategories([]);
    } catch (err) {
      console.error("Error deleting category:", err);
      //setError("Failed to delete the main category.");
    }
  };

  // Handle deleting the selected subcategory
  const handleDeleteSubCategory = async () => {
    if (!selectedCategory || !selectedSubCategory) {
      setError("Please select a category to delete.");
      return;
    }

    try {
      const updatedSubCategories = subCategories.filter(
        (subCat) => subCat !== selectedSubCategory
      );

      // Update the category with the new subcategories list
      const categoryToUpdate = categories.find((cat) => cat.catID === selectedCategory);
      categoryToUpdate.subCats = updatedSubCategories;

      await apiClient.post("/categories/save-category", categoryToUpdate);
      alert("Subcategory deleted successfully!");

      // Refresh categories and reset selection
      fetchCategories();
      setSubCategories(updatedSubCategories);
      setSelectedSubCategory("");
    } catch (err) {
      console.error("Error deleting subcategory:", err);
      //setError("Failed to delete the subcategory.");
    }
  };

  return (
    <div className="admin-delete-category-form">
    <h2>Delete Category</h2>

    {error && <p className="error-message">{error}</p>}

    <label>
        Select a Category:
        <select
        value={selectedCategory}
        onChange={handleCategoryChange}
        required
        >
        <option value="">Select a category</option>
        {categories.map((category) => (
            <option key={category.catID} value={category.catID}>
            {category.catName}
            </option>
        ))}
        </select>
    </label>

    {subCategories.length > 0 && (
        <label>
        Select a Subcategory (Optional):
        <select
            value={selectedSubCategory}
            onChange={(e) => setSelectedSubCategory(e.target.value)}
        >
            <option value="">Select a subcategory</option>
            {subCategories.map((subCat, index) => (
            <option key={index} value={subCat}>
                {subCat}
            </option>
            ))}
        </select>
        </label>
    )}

    <div className="delete-buttons">
        <button
        className="delete-main-category-button"
        onClick={handleDeleteCategory}
        >
        Delete Category
        </button>
        {subCategories.length > 0 && (
        <button
            className="delete-subcategory-button"
            onClick={handleDeleteSubCategory}
        >
            Delete Subcategory
        </button>
        )}
    </div>
    </div>

  );
};

export default DeleteCategoryForm;
