import React, { useState, useEffect } from "react";
import axios from "axios";
import "./AdminCategories.css";
import AdminAddCategoryForm from "../AdminAddCategoryForm/AdminAddCategoryForm";
import AdminDeleteCategoryForm from "../AdminDeleteCategoryForm/AdminDeleteCategoryForm";

const AdminCategories = () => {
  const [categories, setCategories] = useState([]);
  const [editingCategory, setEditingCategory] = useState(null);
  const [editedCategory, setEditedCategory] = useState({});

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await axios.get("/api/categories/get-all-categories");
      const fetchedCategories = response.data.categories || [];
      setCategories(fetchedCategories);
    } catch (error) {
      console.error("Failed to fetch categories:", error);
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    try {
      await axios.delete(`/api/categories/delete-category/${categoryId}`);
      alert("Category deleted successfully!");
      fetchCategories();
    } catch (error) {
      console.error("Failed to delete category:", error);
      alert("Failed to delete category. Please try again.");
    }
  };

  const handleEditClick = (category) => {
    setEditingCategory(category.catID);
    setEditedCategory({ ...category });
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditedCategory((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditSave = async () => {
    try {
      await axios.post(`/api/categories/save-category`, editedCategory);
      alert("Category updated successfully!");
      setEditingCategory(null);
      fetchCategories();
    } catch (error) {
      console.error("Failed to update category:", error);
      alert("Failed to update category. Please try again.");
    }
  };

  return (
    <div className="admin-categories-container">
      <div className="form-container">
        <AdminAddCategoryForm fetchCategories={fetchCategories} />
        <AdminDeleteCategoryForm fetchCategories={fetchCategories} />
      </div>

      <h1>Categories</h1>
      <table className="admin-categories-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {categories.map((category) => (
            <tr key={category.catID}>
              <td>{category.catID}</td>
              {editingCategory === category.catID ? (
                <td>
                  <input
                    type="text"
                    name="catName"
                    value={editedCategory.catName}
                    onChange={handleEditChange}
                  />
                </td>
              ) : (
                <td>{category.catName}</td>
              )}
              <td>
                {editingCategory === category.catID ? (
                  <>
                    <button className="save-button" onClick={handleEditSave}>
                      Save
                    </button>
                    <button
                      className="cancel-button"
                      onClick={() => setEditingCategory(null)}
                    >
                      Cancel
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      className="edit-button"
                      onClick={() => handleEditClick(category)}
                    >
                      Edit
                    </button>
                    <button
                      className="delete-button"
                      onClick={() => handleDeleteCategory(category.catID)}
                    >
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminCategories;
