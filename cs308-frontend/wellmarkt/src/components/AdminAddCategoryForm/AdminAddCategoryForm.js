import React, { useState } from 'react';
import './AdminAddCategoryForm.css';
import apiClient from '../../api/axios';

const AdminAddCategoryForm = ({ fetchCategories }) => {
  const [categoryData, setCategoryData] = useState({
    catID: '',
    catName: '',
    catImURL: '',
    catQuote: '',
    catQuoteOwner: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCategoryData({
      ...categoryData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await apiClient.post('/admin/categories/save-category', categoryData);

      if (response.status === 200) {
        alert('Category added successfully!');
        setCategoryData({
          catID: '',
          catName: '',
          catImURL: '',
          catQuote: '',
          catQuoteOwner: '',
        });
        fetchCategories(); // Refresh categories after successful addition
      } else {
        alert('Failed to add category.');
      }
    } catch (error) {
      console.error('Error adding category:', error.response?.data || error.message);
    }
  };

  return (
    <form className="admin-add-category-form" onSubmit={handleSubmit}>
      <h2>Add Category</h2>

      <label>
        Category ID:
        <input
          type="text"
          name="catID"
          value={categoryData.catID}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Category Name:
        <input
          type="text"
          name="catName"
          value={categoryData.catName}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Image URL:
        <input
          type="text"
          name="catImURL"
          value={categoryData.catImURL}
          onChange={handleChange}
        />
      </label>

      <label>
        Quote:
        <input
          type="text"
          name="catQuote"
          value={categoryData.catQuote}
          onChange={handleChange}
        />
      </label>

      <label>
        Quote Owner:
        <input
          type="text"
          name="catQuoteOwner"
          value={categoryData.catQuoteOwner}
          onChange={handleChange}
        />
      </label>

      <button type="submit" className="submit-button">
        Add Category
      </button>
    </form>
  );
};

export default AdminAddCategoryForm;
