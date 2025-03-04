import React, { useContext } from "react";
import { ProductsContext } from "../../contexts/ProductsContext";
import { useWishlist } from "../../contexts/WishlistContext";
import { useCart } from "../../contexts/CartContext";
import { toast } from "react-hot-toast";
import "./MyWishlist.css";

const MyWishList = () => {
  const { products } = useContext(ProductsContext); // All products
  const { wishlist, removeFromWishlist } = useWishlist(); // Wishlist pIDs
  const { addToCart } = useCart(); // Add to cart functionality

  const wishlistProducts = products.filter((product) =>
    wishlist.includes(product.pID)
  ); // Get products from wishlist pIDs

  const handleRemoveFromWishlist = (productID) => {
    removeFromWishlist(productID);
    toast.success("Removed from wishlist!");
  };

  const handleAddToCart = (product) => {
    addToCart(product, 1);
    toast.success(`${product.name} added to cart!`);
  };

  return (
    <div>
      <div className="wishlist-product-cards-container">
        {wishlistProducts.length > 0 ? (
          wishlistProducts.map((product) => {
            // Calculate discounted price
            const discountPercent = product.discountPercent || 0;
            const discountedPrice = (product.price * (1 - discountPercent / 100)).toFixed(2);

            return (
              <div className="wishlist-product-card" key={product.pID}>
                <img
                  className="wishlist-product-card-image"
                  src={product.imageURL}
                  alt={product.name || product.brand}
                />
                <p className="wishlist-product-card-brand">
                  {product.name || product.brand}
                </p>

                <div className="wishlist-product-card-info">
                  {discountPercent > 0 ? (
                    // Show discounted price
                    <p>
                      <strong>Discounted Price: {discountedPrice} TL</strong>{" "}
                      <span style={{ marginLeft: "8px", textDecoration: "line-through" }}>
                        {product.price.toFixed(2)} TL
                      </span>
                    </p>
                  ) : (
                    // Show normal price
                    <p>Price: {product.price ? `${product.price.toFixed(2)} TL` : "0 TL"}</p>
                  )}
                  <p>Stock: {product.stock}</p>
                </div>

                <div className="wishlist-buttons-container">
                  <button
                    className="wishlist-add-to-cart-button"
                    onClick={() => handleAddToCart(product)}
                  >
                    Add to Cart
                  </button>
                  <button
                    className="wishlist-remove-button"
                    onClick={() => handleRemoveFromWishlist(product.pID)}
                  >
                    Remove from Wishlist
                  </button>
                </div>
              </div>
            );
          })
        ) : (
          <p style={{ textAlign: "center", marginTop: "20px" }}>
            Your wishlist is empty.
          </p>
        )}
      </div>
    </div>
  );
};

export default MyWishList;
