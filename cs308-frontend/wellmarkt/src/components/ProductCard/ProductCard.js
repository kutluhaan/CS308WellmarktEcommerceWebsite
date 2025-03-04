import React, { useContext } from "react";
import "./ProductCard.css";
import { ProductsContext } from "../../contexts/ProductsContext";
import { Link } from "react-router-dom";
import { IoMdHeartEmpty, IoMdHeart } from "react-icons/io";
import { useCart } from '../../contexts/CartContext';
import { useWishlist } from "../../contexts/WishlistContext";
import { toast } from 'react-hot-toast';

const ProductCard = () => {
  // Use the ProductsContext to get the dynamically fetched products
  const { filteredProducts } = useContext(ProductsContext);
  const { addToCart } = useCart();
  const { wishlist, addToWishlist, removeFromWishlist } = useWishlist(); // Use wishlist context

  const handleAddToCart = (product) => {
    addToCart(product, 1); // Assuming quantity is 1 for each product
    toast.success(`${product.name} added to cart!`);
  };

  const toggleWishlist = (productID) => {
    if (wishlist.includes(productID)) {
      removeFromWishlist(productID);
      toast.success("Removed from wishlist");
    } else {
      addToWishlist(productID);
      toast.success("Added to wishlist!");
    }
  };

  return (
    <div className="includes">
      <div className="product-cards-container">
        {filteredProducts.length > 0 ? ( // Check if there are filtered products
          filteredProducts.map((product) => (
            <div
              className="product-card"
              key={product.pID}
            >
              <button
                className={`product-card-btn-wishlist ${
                  wishlist.includes(product.pID) ? "active" : ""
                }`}
                onClick={() => toggleWishlist(product.pID)}
              >
                <IoMdHeartEmpty
                  className={`heart-empty ${
                    wishlist.includes(product.pID) ? "hidden" : ""
                  }`}
                />
                <IoMdHeart
                  className={`heart-filled ${
                    wishlist.includes(product.pID) ? "visible" : ""
                  }`}
                />
              </button>

              {/* The Link component for product details */}
              <Link
                className="product-card-link"
                to={`/product/${product.pID}`}
              >
                <img
                  className="product-card-image"
                  src={product.imageURL}
                  alt={product.name || product.brand}
                />
              </Link>
              <p className="product-card-brand">{product.name || product.brand}</p>

              <div>
                {product.discountPercent && product.discountPercent > 0 ? (
                  <div>
                      <p>
                      <span style={{ fontWeight: 'bold', color: 'white'}}>
                        {`${(product.price * (1 - product.discountPercent / 100)).toFixed(2)} TL`}
                      </span>
                      {' '}
                      <span style={{ textDecoration: 'line-through', color: 'red', fontSize: '10x' }}>
                        {`${product.price.toFixed(2)} TL`}
                      </span> 
                      {' '}
                    </p>
                    <p style={{color: 'white', fontWeight: 'bold'}}>({product.discountPercent}% off)</p>
                  </div>
                ) : (
                  <p className="product-card-price">
                  {product.price ? `${product.price.toFixed(2)} TL` : "$0.00"}
                </p>
                )}
              </div>
              

              {/* Add to Cart Button */}
              <button 
                className="add-to-cart-button-products-page" 
                onClick={() => handleAddToCart(product)} // Pass product as argument
                disabled={product.stock === 0} // Disable if stock is 0
              >
                {product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
              </button>
            </div>
          ))
        ) : (
          // Display fallback message if no products are found
          <p style={{ textAlign: "center", marginTop: "20px" }}>
            No products found for your search.
          </p>
        )}
      </div>
    </div>
  );
};

export default ProductCard;
