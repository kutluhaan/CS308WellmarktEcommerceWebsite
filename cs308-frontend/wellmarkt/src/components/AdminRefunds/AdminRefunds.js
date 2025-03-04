import React, { useEffect, useState } from "react";
import axios from "axios";
import "./AdminRefunds.css";

const AdminRefunds = () => {
  const [refunds, setRefunds] = useState([]);

  // Fetch all return orders
  useEffect(() => {
    const fetchRefunds = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/orders/all");
        if (response.status === 200) {
          const returnOrders = response.data.filter((order) => order.orderType === "return");
          setRefunds(returnOrders);
        }
      } catch (error) {
        console.error("Error fetching refunds:", error);
        alert("Failed to fetch refund orders.");
      }
    };
    fetchRefunds();
  }, []);

  // Approve a refund
  const handleApproveRefund = async (orderId, productId) => {
    try {
      const response = await axios.put(
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
        alert(`Refund for product ${productId} in order ${orderId} approved successfully.`);
        // Update the refund state locally
        setRefunds((prevRefunds) =>
          prevRefunds.map((order) =>
            order.orderId === orderId
              ? {
                  ...order,
                  products: order.products.map((product) =>
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
      alert("Failed to approve refund. Please try again.");
    }
  };

  return (
    <div className="admin-refunds">
      <h2>Admin Refund Management</h2>
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
          {refunds.map((order) =>
            order.products.map((product) => (
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

export default AdminRefunds;
