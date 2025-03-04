import React, { useContext, useState } from "react";
import { ProductsContext } from "../../contexts/ProductsContext";
import { MdOutlineDoneOutline } from "react-icons/md";
import "./AdminPriceSettingForm.css";
import { toast } from 'react-hot-toast';
import apiClient from "../../api/axios";

const AdminPriceSettingForm = () => {
  const { filteredProducts } = useContext(ProductsContext);

  const [prices, setPrices] = useState({});

  const handlePriceChange = (productId, value) => {
    if (value >= 0) {
        setPrices((prev) => ({ ...prev, [productId]: value }));
    } else {
      toast.error("Price cannot be negative");
    }
  };

  const setPrice = async (productId, newPrice) => {
    
      const response = await apiClient.put('/admin/set-price', null, {
        params: {
            productId,
            newPrice,
        },
        headers: {
          'Authorization': `${localStorage.getItem('authToken')}`, // Include token if using JWT for authorization
        },
    });
  
      if (response.status === 200) {
        toast.success(`Price applied successfully: ${response.data.newPrice}TL`);

        setTimeout(() => {
          window.location.reload();
        }, 2000);
      } else {
        toast.error(`Error: ${response.status}`);
        setTimeout(() => {
          window.location.reload();
        }, 2000);
      }
  };
  

  return (
    <div>
      <div className="product-cards-container-sm-set-price">
        {filteredProducts.length > 0 ? ( // Check if there are filtered products
          filteredProducts.map((product) => (
            <div
              className="product-card-sm-set-price"
              key={product.pID}
            >
              
            <img
                className="product-card-image-sm-set-price"
                src={product.imageURL}
                alt={product.name || product.brand}
            />

            
            <p className="product-card-brand-sm-set-price">{product.name || product.brand}</p>
            <div className="product-card-info-sm-set-price">

            <div className='sm-set-price-price-display'>
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

            <p className="set-set-price-text">
                Set Price
            </p>
            <div className="input-container">
                <input 
                    id="set-price" 
                    type="number" 
                    className="set-price-input" 
                    min="0" 
                    max="100" 
                    placeholder="Price" 
                    value={prices[product.pID] || ""}
                    onChange={(e) => handlePriceChange(product.pID, e.target.value)}
                />
            </div>
                
            <button
              className="submit-set-price-button"
              onClick={() => setPrice(product.pID, prices[product.pID] || 0)}       
            >
              <MdOutlineDoneOutline />
            </button>
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

export default AdminPriceSettingForm;
