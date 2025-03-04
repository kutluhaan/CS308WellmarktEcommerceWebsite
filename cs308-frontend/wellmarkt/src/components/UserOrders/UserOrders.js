
// src/components/UserOrders/UserOrders.js
import React, { useEffect, useState } from 'react';
import apiClient from '../../api/axios';
import './UserOrders.css';

const UserOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  // For controlling the refund request form
  const [selectedProduct, setSelectedProduct] = useState({});
  const [refundQuantity, setRefundQuantity] = useState(1);

  useEffect(() => {
    fetchUserOrders();
  }, []);

  // ------------------------------------------------
  // Fetch user orders (customer + return + canceled, etc.)
  // ------------------------------------------------
  const fetchUserOrders = async () => {
    setLoading(true);
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert('You must be logged in to view your orders.');
      setLoading(false);
      return;
    }

    try {
      const response = await apiClient.get('orders/my-orders', {
        headers: {
          Authorization: `${token}`,
        },
      });

      if (response.status === 200) {
        // This is the entire JSON array, including both customer_order, return, canceled, etc.
        const reversedOrders = response.data.reverse();
        setOrders(reversedOrders);
      } else {
        alert('Failed to fetch orders.');
      }
    } catch (error) {
      console.error('Error fetching user orders:', error);
      alert('An error occurred while fetching orders.');
    } finally {
      setLoading(false);
    }
  };

  // ------------------------------------------------
  // Cancel entire order
  // ------------------------------------------------
  const handleCancelOrder = async (orderId) => {
    try {
      const response = await apiClient.put(
        `orders/cancel-order/${orderId}`
      );
      if (response.status === 200) {
        alert(`Order ${orderId} has been canceled successfully.`);
        // Refresh to see updated orderType
        fetchUserOrders();
      } else {
        alert(`Failed to cancel order ${orderId}.`);
      }
    } catch (error) {
        // Extract the backend error message
        const errorMessage = error.response?.data || 'An unknown error occurred while canceling the order.';
        alert(`Cannot cancel this order. Reason: ${errorMessage}`);
    }
  };

  // ------------------------------------------------
  // Return how many items have already been requested
  // for a certain product in the original order
  // ------------------------------------------------
  const getAlreadyRefundedQuantity = (originalOrderId, productId) => {
    let refundedCount = 0;

    // Loop over *all* orders (which includes 'return' orders)
    for (let ord of orders) {
      // Only consider if it's a return order referencing oldOrderId == originalOrderId
      if (ord.orderType === 'return' && ord.oldOrderId === originalOrderId) {
        // Inside that return order, sum the quantity of matching product
        for (let p of ord.products) {
          // We rely on the 'product_id' field in the backend transformation
          if (p.product_id === productId) {
            refundedCount += p.quantity;
          }
        }
      }
    }

    return refundedCount;
  };

  // ------------------------------------------------
  // Trigger the POST /request-return endpoint
  // ------------------------------------------------
  const handleRequestRefund = async (order, productId, requestedQty) => {
    // 1) Find the product in the original order
    const productInOriginalOrder = order.products.find(
      (p) => p.product_id === productId
    );
    if (!productInOriginalOrder) {
      alert('Product not found in this order.');
      return;
    }

    // 2) Check how many have already been refunded
    const alreadyRefunded = getAlreadyRefundedQuantity(order.orderId, productId);
    const purchasedQuantity = productInOriginalOrder.quantity;

    // 3) If alreadyRefunded + requestedQty > purchasedQuantity => reject
    if (alreadyRefunded + requestedQty > purchasedQuantity) {
      alert(
        `You have already requested ${alreadyRefunded} of this product.\n` +
        `You only purchased ${purchasedQuantity} in total.\n` +
        `Your new request of ${requestedQty} would exceed the purchased amount.`
      );
      return;
    }

    // 4) If checks pass, send the request
    try {
      const response = await apiClient.post(
        'orders/request-return',
        null,
        {
          params: {
            orderId: order.orderId,
            productId: productId,
            quantity: requestedQty,
          },
        }
      );
      if (response.status === 200) {
        alert('Refund request submitted successfully!');
        // Refresh to see new return order appear (and update local data)
        fetchUserOrders();
      } else {
        alert('Refund request failed: ' + response.data);
      }
    } catch (error) {
      console.error('Error requesting refund:', error);
      const errorMessage = error.response?.data || 'An unknown error occurred while requesting refund.';
      alert(`Error requesting refund. Reason: ${errorMessage}`);

    }
  };

  // ------------------------------------------------
  // Called when the user clicks "Request Refund"
  // ------------------------------------------------
  const handleRefundButtonClick = (order) => {
    // Make sure user selected a product
    if (!selectedProduct[order.orderId]) {
      alert('Please select a product first.');
      return;
    }
    const productId = selectedProduct[order.orderId];
    handleRequestRefund(order, productId, refundQuantity);
  };

  // On dropdown change
  const handleProductSelectChange = (orderId, productId) => {
    setSelectedProduct((prev) => ({
      ...prev,
      [orderId]: productId,
    }));
  };

  // On quantity change
  const handleQuantityChange = (e) => {
    const val = parseInt(e.target.value, 10);
    setRefundQuantity(val > 0 ? val : 1);
  };

  if (loading) {
    return <div className="orders-loading">Loading your orders...</div>;
  }

  return (
    <div className="user-orders-wrapper">
      <h2>Your Orders</h2>
      {orders.length === 0 ? (
        <p>You have no orders.</p>
      ) : (
        orders.map((order) => (
          <div className="order-card" key={order.orderId}>
            
            <div className="order-header">
              <div>
                <strong>Order ID:</strong> {order.orderId}
              </div>
              {order.address && (
      <div className="order-header-line">
        <strong>Address:</strong> {order.address}
      </div>
    )}
    <div className="order-header-line">
              
                <strong>Date:</strong>{' '}
                {order.purchased_at
                  ? new Date(order.purchased_at).toLocaleString()
                  : 'N/A'}
              </div>
              <div>
                <strong>Type:</strong> {order.orderType}
                
              </div>
            </div>

            
            <div className="order-products">
              {order.products.map((prod, index) => (
                <div className="order-product-row" key={index}>
                  <img
                    src={prod.product_photo}
                    alt={prod.product_name}
                    className="product-photo"
                  />
                  <div className="product-info">
                    <div className="product-name">{prod.product_name}</div>
                    <div className="product-quantity">
                      <strong>Quantity:</strong> {prod.quantity}
                    </div>
                    <div className='product-unit-price'>
                      <strong>{prod.total_price / prod.quantity} ₺</strong>
                    </div>
                    <div className="product-total-price">
                      <strong>Total Price:</strong> {prod.total_price} ₺
                    </div>
                    <div className="product-status">
                      <strong>Status:</strong> {prod.status}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            
            <div className="order-footer">
              
            {order.orderType === 'customer_order' && order.products.every(prod => prod.status === 'On Process') ? (
                <button
                  className="cancel-order-button"
                  onClick={() => handleCancelOrder(order.orderId)}
                >
                  Cancel Order
                </button>
              ) : (
                <button className="cancel-order-button" disabled>
                  {order.orderType === 'canceled'
                    ? 'Order Canceled'
                    : 'Not Cancelable'}
                </button>
              )}

              
              <div className="refund-request">
                <select
                  className="refund-product-select"
                  onChange={(e) =>
                    handleProductSelectChange(order.orderId, e.target.value)
                  }
                  disabled={order.orderType !== 'customer_order'}
                >
                  <option value="">Select Product</option>
                  {order.products.map((prod, idx) => (
                    <option value={prod.product_id} key={idx}>
                      {prod.product_name}
                    </option>
                  ))}
                </select>
                <input
                  type="number"
                  min={1}
                  className="refund-quantity-input"
                  value={refundQuantity}
                  onChange={handleQuantityChange}
                  disabled={order.orderType !== 'customer_order'}
                />
                <button
                  className="refund-button"
                  onClick={() => handleRefundButtonClick(order)}
                  disabled={order.orderType !== 'customer_order'}
                >
                  Request Refund
                </button>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default UserOrders;


