import React, { useEffect, useState } from "react";
import { useCart } from "../../contexts/CartContext";
import { useContext } from "react";
import { ProductsContext } from "../../contexts/ProductsContext";
import { useNavigate } from "react-router-dom";
import "./ShoppingCart.css";
import apiClient from "../../api/axios";

const ShoppingCart = () => {
  const { cart, clearCart, addToCart, removeFromCart } = useCart();
  const { products } = useContext(ProductsContext);
  const [cartDetails, setCartDetails] = useState([]);
  const [removingItem, setRemovingItem] = useState(null); // Track the item being removed
  const navigate = useNavigate();

  useEffect(() => {
    console.group("ShoppingCartPage Logs");
    console.log("Cart from context (productID & quantity only):", cart);
    console.log("Products from context:", products);

    // Enrich cart with product details (only on this page)
    const enrichedCart = cart.map((item) => {
      const product = products.find((prod) => prod.pID === item.productID);

      if (!product) {
        console.warn(`Product not found for cart item:`, item);
      }

      return product
        ? { ...item, ...product } // Merge product details
        : { ...item, name: "Unavailable", price: 0, stock: 0 }; // Fallback if product is not found
    });

    console.log("Enriched cart details after merge:", enrichedCart);
    setCartDetails(enrichedCart);
    console.groupEnd();
  }, [cart, products]);

  const calculateTotal = () => {
    const total = cartDetails.reduce((sum, item) => {
      const discount = item.discountPercent || 0;
      const discountedPrice = item.price * (1 - discount / 100);
      return sum + discountedPrice * item.quantity;
    }, 0);
    console.log("Calculated total cart value:", total);
    return total.toFixed(2);
  };

  const updateCartItemCount = (existingQuantity, newQuantity, productID) => {
    console.log(`Updating cart item (productID: ${productID}) to quantity: ${newQuantity}`);
    if (newQuantity > 0) {
      if (existingQuantity !== 0){
        const quantityToAdd = newQuantity - existingQuantity;
        console.log(`Calling addToCart with quantity: ${quantityToAdd}`);
        addToCart(productID, quantityToAdd); // Call addToCart to update the context
      } else {
        return;
      }
    } else if (newQuantity === 0) {
      console.log(`Removing product (productID: ${productID}) from cart`);
      setRemovingItem(productID); // Set the product ID to be removed
      setTimeout(() => {
        removeFromCart(productID); // Remove after the animation finishes
      }, 500); // Duration of the animation
    } else {
      console.warn("Attempted to set quantity to negative, ignoring.");
    }
  };

  const handleCheckout = async () => {
    console.log("Starting cart sync before checkout...");
    console.log("current cart: ", cart);
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        alert("Please sign in to proceed to checkout.");
        navigate("/sign-in");
        return;
      }
      
      // Sync the cart with the server
      const response = await apiClient.post(
        "customer/set-cart",
        { cart : cart }, // Send the current cart
        {
          headers: {
            Authorization: `${token}`,
          },
        }
      );

      console.log("Cart synced successfully:", response.data);
      navigate("/purchase"); // Proceed to the purchase page
    } catch (error) {
      console.error("Error syncing cart before checkout:", error);
      alert("Failed to sync cart. Please try again.");
    }
  };

  return (
    <div className="shopping-cart-content">
      <h1 className="heading-of-cart">Your Cart</h1>
      {cartDetails.length === 0 ? (
        <p className="empty-cart-message">Your cart is empty</p>
      ) : (
        <div>
          <div className="cart-list">
            {cartDetails.map((item) => {
              const discount = item.discountPercent || 0;
              const discountedPrice = (item.price * (1 - discount / 100)).toFixed(2);

              return (
                <div
                  key={item.productID}
                  className={`cart-item ${removingItem === item.productID ? "removing" : ""}`}
                >
                  <img
                    src={item.imageURL || "placeholder.jpg"}
                    alt={item.name}
                    className="cart-item-image"
                  />
                  <div className="cart-item-details">
                    <h3>{item.name}</h3>
                    {discount > 0 ? (
                      <p>
                        <strong>Price: {discountedPrice} TL</strong>{" "}
                        <s>{item.price} TL</s>
                      </p>
                    ) : (
                      <p>Price: {item.price} TL</p>
                    )}
                    <p>Stock Available: {item.stock}</p>

                    <div className="cart-item-actions">
                      <button
                        className="quantity-decrement-button"
                        onClick={() =>
                          updateCartItemCount(item.quantity, item.quantity - 1, item.productID)
                        }
                      >
                        -
                      </button>
                      <input
                        className="manual-quantity-input"
                        type="number"
                        value={item.quantity}
                        onChange={(e) =>
                          updateCartItemCount(item.quantity, Number(e.target.value), item.productID)
                        }
                        min="1"
                        max={item.stock}
                        readOnly
                      />
                      <button
                        className="quantity-increment-button"
                        disabled={(item.stock <= item.quantity)}
                        onClick={() =>
                          updateCartItemCount(item.quantity, item.quantity + 1, item.productID)
                        }
                      >
                        +
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          <div className="cart-summary">
            <h3 className="cart-total-text">Total: {calculateTotal()} TL</h3>
            <button className="checkout-button" onClick={handleCheckout}>
              Proceed to Checkout
            </button>
            <button
              className="clear-cart-button"
              onClick={() => {
                console.log("Clearing the cart...");
                clearCart();
              }}
            >
              Clear Cart
            </button>
          </div>
        </div>
      )}
    </div>
  );

};

export default ShoppingCart;
