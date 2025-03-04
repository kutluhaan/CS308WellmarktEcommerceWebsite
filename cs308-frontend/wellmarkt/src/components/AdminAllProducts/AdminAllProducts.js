import React, { useContext } from "react";
import { ProductsContext } from "../../contexts/ProductsContext";
import "./AdminAllProducts.css";

const AdminAllProducts = () => {
  const { filteredProducts } = useContext(ProductsContext);


  return (
    <div>
      <div className="product-cards-container-admin-all-prods">
        {filteredProducts.length > 0 ? ( // Check if there are filtered products
          filteredProducts.map((product) => (
            <div
              className="product-card-admin-all-prods"
              key={product.pID}
            >
              
            <img
                className="product-card-image-admin-all-prods"
                src={product.imageURL}
                alt={product.name || product.brand}
            />

            
            <p className="product-card-brand-admin-all-prods">{product.name || product.brand}</p>
            <div className="product-card-info-admin-all-prods">

            <div className='admin-all-prods-price-display'>
                {product.discountPercent && product.discountPercent > 0 ? (
                  <div>
                      <p>
                      Price: <span style={{ fontWeight: 'bold', color: 'white'}}>
                        {`${(product.price * (1 - product.discountPercent / 100)).toFixed(2)} TL`}
                      </span>
                      {' '}
                      <span style={{ textDecoration: 'line-through', color: 'red', fontSize: '15px' }}>
                        {`${product.price.toFixed(2)} TL`}
                      </span> 
                      {' '}
                    </p>
                    <p>({product.discountPercent}% off)</p>
                  </div>
                ) : (
                  <p>Price: {product.price ? `${product.price.toFixed(2)} TL` : '0 TL'}</p>
                )}
              </div>
              <p>Product ID: {product.pID}</p>
              <p>Stock: {product.stock}</p>
            </div>
          </div>
          ))
        ) : (
          // Display fallback message if no products are found
          <p style={{ textAlign: "center", marginTop: "20px" }}>
            No products available on the shop now.
          </p>
        )}
      </div>
    </div>
  );
};

export default AdminAllProducts;
