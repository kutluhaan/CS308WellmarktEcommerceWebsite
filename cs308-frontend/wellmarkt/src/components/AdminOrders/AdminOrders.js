

import React, { useEffect, useState } from 'react';
import './AdminOrders.css';
import apiClient from '../../api/axios';

const AdminOrders = () => {
  const [orders, setOrders] = useState([]);
  const [statusMap, setStatusMap] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCustomerOrders();
  }, []);

  // Fetch only "customer_order" type orders
  const fetchCustomerOrders = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get('http://localhost:8080/api/orders/all');
      if (response.status === 200) {
        // Filter to include only customer_order type
        const customerOrders = response.data.filter(order => order.orderType === 'customer_order');
        setOrders(customerOrders);

        // Initialize the status map for each product
        const initialStatusMap = {};
        customerOrders.forEach(order => {
          order.products.forEach(product => {
            initialStatusMap[`${order.orderId}-${product.productId}`] = product.status;
          });
        });
        setStatusMap(initialStatusMap);
      }
    } catch (error) {
      console.error('Error fetching customer orders:', error);
      alert('Error fetching orders.');
    } finally {
      setLoading(false);
    }
  };

  // Handle status change in the dropdown
  const handleStatusChange = (orderId, productId, newStatus) => {
    setStatusMap(prevStatusMap => ({
      ...prevStatusMap,
      [`${orderId}-${productId}`]: newStatus,
    }));
  };

  // Save the updated status for a specific product in an order
  const handleSaveStatus = async (orderId, productId) => {
    const newStatus = statusMap[`${orderId}-${productId}`];
    try {
      const response = await apiClient.put(
        `http://localhost:8080/api/orders/update-status/${orderId}`,
        null,
        {
          params: {
            productId,
            newStatus,
          },
        }
      );
      if (response.status === 200) {
        alert(`Status for product ${productId} in order ${orderId} updated to "${newStatus}".`);
        fetchCustomerOrders(); // Refresh data after update
      } else {
        alert(`Failed to update status for product ${productId}.`);
      }
    } catch (error) {
      console.error('Error updating product status:', error);
      alert('Error updating product status. Please try again.');
    }
  };

  if (loading) {
    return <div className="order-management-prodman">Loading orders...</div>;
  }

  return (
    <div className="order-management-prodman">
      <h2>Order Management</h2>
      {orders.length === 0 ? (
        <p>No customer orders available.</p>
      ) : (
        orders.map(order => (
          <div key={order.orderId} className="order-card-prodman">
            {/* Order details */}
            <div className="order-header-prodman">
              <div>
                <strong>Order ID:</strong> {order.orderId}
              </div>
              <div>
                <strong>Customer ID:</strong> {order.customerId}
              </div>
              <div>
                <strong>Order Type:</strong> {order.orderType}
              </div>
              <div>
                <strong>Address:</strong> {order.address}
              </div>
            </div>

            {/* Products in the order */}
            <div className="order-products-prodman">
              {order.products.map(product => (
                <div key={product.productId} className="order-product-prodman">
                  <div>
                    <p><strong>Product ID: </strong>{product.productId}</p>
                  </div>
                  <div>
                    <p><strong>Quantity: </strong>{product.quantity}</p>
                  </div>
                  <div>
                    <strong>Status</strong>&nbsp;
                    <select
                      value={statusMap[`${order.orderId}-${product.productId}`]}
                      onChange={e => handleStatusChange(order.orderId, product.productId, e.target.value)}
                    >
                      <option value="On Process">On Process</option>
                      <option value="In Transit">In Transit</option>
                      <option value="Delivered">Delivered</option>
                    </select>
                  </div>
                  <button
                    className="save-button-prodman"
                    onClick={() => handleSaveStatus(order.orderId, product.productId)}
                  >
                    Save Status
                  </button>
                </div>
              ))}
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default AdminOrders;

