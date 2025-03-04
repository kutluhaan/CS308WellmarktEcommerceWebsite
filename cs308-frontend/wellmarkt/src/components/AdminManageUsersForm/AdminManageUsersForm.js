import React, { useState, useEffect } from "react";
import "./AdminManageUsersForm.css";
import apiClient from "../../api/axios";

const AdminManageUsersForm = () => {
  const [users, setUsers] = useState([]);
  const [formData, setFormData] = useState({
    firstName: "",
    middleName: "",
    lastName: "",
    email: "",
    role: "CUSTOMER",
    password: "",
  });

  // Fetch users when component loads
  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await apiClient.get("admin/users"); // Replace with your backend endpoint
      setUsers(response.data); // Assuming backend sends an array of users
    } catch (error) {
      console.error("Failed to fetch users:", error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await apiClient.post("admin/users", formData); // Replace with your backend endpoint
      if (response.status === 201) {
        alert("User created successfully!");
        fetchUsers(); // Refresh the user list
        setFormData({
          firstName: "",
          middleName: "",
          lastName: "",
          email: "",
          role: "CUSTOMER",
          password: "",
        });
      } else {
        alert("Failed to create user.");
      }
    } catch (error) {
      console.error("Failed to create user:", error);
      alert("Error creating user. Please try again.");
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await apiClient.delete(`admin/users/${id}`); // Replace with your backend endpoint
      if (response.status === 200) {
        alert("User deleted successfully!");
        fetchUsers(); // Refresh the user list
      } else {
        alert("Failed to delete user.");
      }
    } catch (error) {
      console.error("Failed to delete user:", error);
      alert("Error deleting user. Please try again.");
    }
  };

  return (
    <div>
      {/* Form for creating users */}
      <form className="admin-userform-card" onSubmit={handleSubmit}>
        <h2 className="admin-userform-title">Manage Users</h2>

        <label className="admin-userform-label">
          <input
            type="text"
            className="admin-userform-input"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            placeholder="First Name"
            required
          />
        </label>

        <label className="admin-userform-label">
          <input
            type="text"
            className="admin-userform-input"
            name="middleName"
            value={formData.middleName}
            onChange={handleChange}
            placeholder="Middle Name"
          />
        </label>

        <label className="admin-userform-label">
          <input
            type="text"
            className="admin-userform-input"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            placeholder="Last Name"
            required
          />
        </label>
        
        <label className="admin-userform-label">
          <input
            type="email"
            className="admin-userform-input"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="Email"
            required
          />
        </label>


        <label className="admin-userform-label">
          <input
            type="password"
            className="admin-userform-input"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Password"
            required
          />
        </label>

        <label className="admin-userform-label">
          <select
            className="admin-userform-input"
            name="role"
            value={formData.role}
            onChange={handleChange}
          >
            <option value="CUSTOMER">Customer</option>
            <option value="PRODUCT_MANAGER">Product Manager</option>
            <option value="SALES_MANAGER">Sales Manager</option>
            <option value="ADMIN">Admin</option>
          </select>
        </label>

        <button type="submit" className="admin-userform-submit-button">
          Create User
        </button>
      </form>

      {/* Table of users */}
      <table className="admin-userform-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Middle Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.length > 0 ? (
            users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.firstName}</td>
                <td>{user.middleName || "-"}</td>
                <td>{user.lastName}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>
                  <button
                    className="admin-userform-action-button"
                    onClick={() => handleDelete(user.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" className="admin-userform-no-users">
                No users found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AdminManageUsersForm;
