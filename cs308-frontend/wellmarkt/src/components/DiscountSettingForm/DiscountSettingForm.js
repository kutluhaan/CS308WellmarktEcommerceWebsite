import React, { useContext, useState } from "react";
import { ProductsContext } from "../../contexts/ProductsContext";
import { RiDiscountPercentLine } from "react-icons/ri";
import { MdOutlineDoneOutline } from "react-icons/md";
import "./DiscountSettingForm.css";
import { toast } from 'react-hot-toast';
import apiClient from "../../api/axios";

const DiscountSettingForm = () => {
  const { filteredProducts } = useContext(ProductsContext);

  const [discounts, setDiscounts] = useState({});

  const handleDiscountChange = (productId, value) => {
    if (value >= 0 && value <= 100) {
      setDiscounts((prev) => ({ ...prev, [productId]: value }));
    } else {
      toast.error("Discount must be between 0 and 100");
    }
  };

  const setDiscount = async (productId, discount) => {
    
      const tokenPhrase = localStorage.getItem('authToken');
      const response = await apiClient.put('/sales-manager/set-discount', null, {
        params: {
            productId,
            discount,
        },
        headers: {
          Authorization: tokenPhrase, // Include token if using JWT for authorization
        },
    });
  
      if (response.status === 200) {
        toast.success(`Discount applied successfully: ${response.data.discountPercent}%`);

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
      <div className="product-cards-container-sm-discount">
        {filteredProducts.length > 0 ? ( // Check if there are filtered products
          filteredProducts.map((product) => (
            <div
              className="product-card-sm-discount"
              key={product.pID}
            >
              
            <img
                className="product-card-image-sm-discount"
                src={product.imageURL}
                alt={product.name || product.brand}
            />

            
            <p className="product-card-brand-sm-discount">{product.name || product.brand}</p>
            <div className="product-card-info-sm-discount">

            <div className='sm-discount-price-display'>
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

            <p className="set-discount-text">
                <RiDiscountPercentLine /> Set Discount
            </p>
            <div className="input-container">
                <input 
                    id="discount" 
                    type="number" 
                    className="discount-input" 
                    min="0" 
                    max="100" 
                    placeholder="Discount" 
                    value={discounts[product.pID] || ""}
                    onChange={(e) => handleDiscountChange(product.pID, e.target.value)}
                />
            </div>
                
            <button
              className="submit-discount-button"
              onClick={() => setDiscount(product.pID, discounts[product.pID] || 0)}       
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

export default DiscountSettingForm;
