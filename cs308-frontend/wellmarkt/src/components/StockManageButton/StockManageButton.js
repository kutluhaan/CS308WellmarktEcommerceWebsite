import React, { useContext } from 'react';
import { ProductsContext } from "../../contexts/ProductsContext";
import axios from 'axios';
import './StockManageButton.css';

const StockManagement = () => {
  const { products } = useContext(ProductsContext);

  const updateStock = async (productId, quantity, action) => {
    try {
      if (action === 'add') {
        await axios.put(`/api/products/update-stock/add/${productId}?quantity=${quantity}`);
      } else if (action === 'remove') {
        await axios.put(`/api/products/update-stock/remove/${productId}?quantity=${quantity}`);
      }
      alert('Stock updated successfully!');
      window.location.reload(); // Refresh the product list
    } catch (err) {
      alert('Failed to update stock. Check your input.');
      console.error(err);
    }
  };

  return (
    <div className="stock-management-dashboard">
      <h1>Stock Management</h1>
      {products.length === 0 ? (
        <p>No product so far or some error occurred</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Product Code</th>
              <th>Stock</th>
              <th>Update</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.pID}>
                <td>{product.name}</td>
                <td>{product.pID}</td>
                <td>{product.stock}</td>
                <td>
                  <input
                    className='manual-input'
                    type="number"
                    min="1"
                    placeholder="Enter quantity"
                    id={`quantity-${product.pID}`}
                  />
                  <button
                    className="add-button"
                    onClick={() =>
                      updateStock(product.pID, document.getElementById(`quantity-${product.pID}`).value, 'add')
                    }
                  >
                    Add
                  </button>
                  <button
                    className="remove-button"
                    onClick={() =>
                      updateStock(product.pID, document.getElementById(`quantity-${product.pID}`).value, 'remove')
                    }
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default StockManagement;
