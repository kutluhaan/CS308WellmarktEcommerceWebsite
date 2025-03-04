// src/components/RefundManagement/RefundManagement.js
import React, { useEffect, useState } from "react";
import apiClient from "../../api/axios";
import "./RefundManagement.css";
import {toast} from 'react-hot-toast';

const RefundManagement = () => {
  const [refunds, setRefunds] = useState([]);

  // Fetch return orders
  useEffect(() => {
    const fetchRefunds = async () => {
      try {
        const response = await apiClient.get("http://localhost:8080/api/orders/all");
        if (response.status === 200) {
          const returnOrders = response.data.filter(order => order.orderType === "return");
          const reversedRefunds = [...returnOrders].reverse(); 
          setRefunds(reversedRefunds);
        }
      } catch (error) {
        console.error("Error fetching refunds:", error);
      }
    };
    fetchRefunds();
  }, []);

  const handleApproveRefund = async (orderId, productId) => {
    try {
      const response = await apiClient.put(
        `http://localhost:8080/api/orders/update-status/${orderId}`,
        null,
        {
          params: {
            productId: productId,
            newStatus: "Refund Accepted",
          },
        }
      );
      if (response.status === 200) {
        toast.success(`Refund for product ${productId} in order ${orderId} approved successfully.`);
        setRefunds(prevRefunds =>
          prevRefunds.map(order =>
            order.orderId === orderId
              ? {
                  ...order,
                  products: order.products.map(product =>
                    product.productId === productId
                      ? { ...product, status: "Refund Accepted" }
                      : product
                  ),
                }
              : order
          )
        );
      }
    } catch (error) {
      console.error("Error approving refund:", error);
      toast.error("Failed to approve refund. Please try again.");
    }
  };

  return (
    <div className="refund-management">
      <h2>Refund Management</h2>
      <table className="refund-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Customer ID</th>
            <th>Product ID</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {refunds.map(order =>
            order.products.map(product => (
              <tr key={`${order.orderId}-${product.productId}`}>
                <td>{order.orderId}</td>
                <td>{order.customerId}</td>
                <td>{product.productId}</td>
                <td>{product.quantity}</td>
                <td>{product.purchasePrice}</td>
                <td>{product.status}</td>
                <td>
                  <button
                    className="approve-button"
                    disabled={product.status === "Refund Accepted"}
                    onClick={() => handleApproveRefund(order.orderId, product.productId)}
                  >
                    {product.status === "Refund Accepted" ? "Approved" : "Approve"}
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default RefundManagement;
