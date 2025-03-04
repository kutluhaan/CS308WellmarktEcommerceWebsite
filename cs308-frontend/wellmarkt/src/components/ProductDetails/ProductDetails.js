import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './ProductDetails.css';
import apiClient from '../../api/axios'; 
import Rating from '@mui/material/Rating';
import { useCart } from '../../contexts/CartContext';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import { Divider } from '@mui/material';
import { toast } from 'react-hot-toast';
import { IoStar } from "react-icons/io5";
import { IoMdHeartEmpty, IoMdHeart } from "react-icons/io";
import { useWishlist } from "../../contexts/WishlistContext"; // <-- bring in your wishlist context


const ProductDetails = () => {
    const { productId: paramProductId } = useParams();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [rating, setRating] = useState(0);
    const [hover, setHover] = useState(-1);
    const [newComment, setNewComment] = useState('');  // Store the text input for new comment
    const [showCommentForm, setShowCommentForm] = useState(false);  // Track whether the comment form should be visible
    const [currentProductComments, setCurrentProductComments] = useState([]);  // Assuming you already have this state populated
    const { addToCart } = useCart();
    const { wishlist, addToWishlist, removeFromWishlist } = useWishlist();

    const labels = {
        1: 'Useless',
        2: 'Poor',
        3: 'Ok',
        4: 'Good',
        5: 'Excellent!',
    };

    function getLabelText(value) {
        return `${value} Star${value !== 1 ? 's' : ''}, ${labels[value]}`;
    }

    useEffect(() => {
        const fetchProductDetails = async () => {
            try {
                const response = await apiClient.get(`products/${paramProductId}`);
                setProduct(response.data);
                setRating(response.data.rating);
                setLoading(false);
            } catch (error) {
                console.error("Error fetching product details:", error);
                setError("Failed to load product details");
                setLoading(false);
            }
        };
        const fetchComments = async () => {
            try {
                const commentsResponse = await apiClient.get(`/comment/product/${paramProductId}/approved-comments`);
                
                // Check if the 'comments' field exists and is an array
                if (Array.isArray(commentsResponse.data.comments)) {
                    setCurrentProductComments(commentsResponse.data.comments);
                } else {
                    // Handle the case when 'comments' is not an array (or not present)
                    setCurrentProductComments([]);
                }
            } catch (error) {
                console.error("Error fetching comments:", error);
                setCurrentProductComments([]);  // Default to an empty array in case of an error
            }
        };
        

        fetchProductDetails();
        fetchComments();
    }, [paramProductId]);

    const checkIfProductBought = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                return false;
            }
            const response = await apiClient.get(`customer/products/check-bought/${paramProductId}`, {
                headers: { Authorization: `${token}` },
            });
            return response.data.isBought;
        } catch (error) {
            console.error("Error checking product purchase status:", error);
            toast.error("Failed to verify purchase. Please try again.");
            return false;
        }
    };

    const ifRatedBefore = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                return false;
            }
            const response = await apiClient.get(`rating-mapping/get-rate-map`, {
                headers: { Authorization: `${token}` },
            });
            return response.data.rm;
        } catch (error) {
            console.error("Error checking product rate status for you:", error);
            toast.error("Failed to verify rating. Please try again.");
            return false;
        }
    };

    const saveRatingMapping = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                return false;
            }
            const response = await apiClient.post(`rating-mapping/save-rate-map/${paramProductId}`, {}, {
                headers: { Authorization: `${token}` },
            });
            return response.data.rm;
        } catch (error) {
            console.error("Error checking product rate status for you:", error);
            toast.error("Failed to save rating. Please try again.");
            return false;
        }
    };

    if (loading) return <div className="loading">Loading...</div>;
    if (error) return <div className="error">{error}</div>;
    if (!product) return <div className="error">Product not found.</div>;

    const handleQuantityChange = (type) => {
        if (type === 'increment') {
            setQuantity(quantity + 1);
        } else if (type === 'decrement' && quantity > 1) {
            setQuantity(quantity - 1);
        }
    };

    const handleAddToCart = () => {
        addToCart(product, quantity);
        toast.success(`${product.name} added to cart!`);
    };

    const handleRatingChange = async (event, newRatingValue) => {
        console.log("New rating value received: ", newRatingValue); // Debugging
        if (!newRatingValue || newRatingValue < 1) {
          console.warn("Invalid rating ignored:", newRatingValue);
          return; // Ignore invalid ratings
        }
        

      
        setRating(newRatingValue); // Update the UI
      
        try {
            
          const isBought = await checkIfProductBought();
          if (!isBought) {
            toast.error("You need to buy this product to give a rating.");
            return;
          }
      
          const token = localStorage.getItem('authToken');
          if (!token) {
            toast.error("You need to be signed in to give a rating.");
            return;
          }

          const isRated = await ifRatedBefore();
          if (isRated){
            toast.error("You have rated this product before");
            return;
          } else {
            saveRatingMapping();
            
            await apiClient.post(
                `customer/give-rating`,
                { pID: paramProductId, rating: newRatingValue },
                { headers: { Authorization: `${token}` } }
            );
        
            toast.success("Thank you for your rating!");
          }
          
        } catch (error) {
          console.error("Error submitting rating:", error);
          toast.error("Failed to submit rating. Please try again.");
        }
      };
                
    const handleLabelChange = (event, newHover) => {
        setHover(newHover);
    };

    const handleCommentChange = (event) => {
        setNewComment(event.target.value);
    };

    const isWishlisted = wishlist.includes(product?.pID);

    // Toggle wishlist handler
    const handleToggleWishlist = () => {
    if (!product) return; // Guard if product isn't loaded yet

    if (isWishlisted) {
        removeFromWishlist(product.pID);
        toast.success("Removed from wishlist");
    } else {
        addToWishlist(product.pID);
        toast.success("Added to wishlist!");
    }
    };


    const handleSubmitComment = async () => {
        try {
            const isBought = await checkIfProductBought(); // Check if the user bought the product
            if (!isBought) {
                toast.error("You need to buy this product to leave a comment.");
                return;
            }

            const token = localStorage.getItem('authToken');
            if (!token) {
                toast.error("You need to be signed in to leave a comment.");
                return;
            }

            const commentData = {
                pID: paramProductId, // Replace with actual customer ID from auth
                text: newComment,
                createdAt: new Date().toISOString(),
            };

            await apiClient.post('comment/add-comment', commentData, {
                headers: { Authorization: `${token}` },
            });
            toast.success("Your comment has been submitted and will appear after approval.");
            window.location.reload();
        } catch (error) {
            console.error("Error submitting comment:", error);
            toast.error("Failed to submit comment. Please try again.");
        }
    };

    return (
        <div>
            <div className="product-details-container">
                <div className="product-image-section">
                    <img
                        src={product?.imageURL || '/images/fallback.jpg'}
                        alt={product?.name || 'Product Image'}
                        className="product-details-image"
                        onError={(e) => (e.target.src = '/images/fallback.jpg')}
                    />
                    <div className="stock-rating-wishlist">
                        <p><strong>Stock:</strong> {product.stock}</p>
                        <Stack>
                            <p><strong>Rating:</strong> {product.rating?.toFixed(2)} / 5</p>

                        </Stack>
                        <button 
                            className={`wishlist-icon ${isWishlisted ? "active" : ""}`} 
                            onClick={handleToggleWishlist}
                            >
                            <IoMdHeartEmpty
                                className={`heart-empty ${isWishlisted ? "hidden" : "visible"}`}
                            />
                            <IoMdHeart
                                className={`heart-filled ${isWishlisted ? "visible" : "hidden"}`}
                            />
                        </button>

                    </div>
                    <Box sx={{ height: 50, width: 250, display: 'flex', alignItems: 'center' }}>
                    <Rating
                        value={rating || 0}
                        precision={1}
                        getLabelText={getLabelText}
                        onChange={(event, newValue) => {
                            // If newValue is null, fallback to the event target's value to get the last clicked star
                            const validRating = newValue ?? Math.ceil(event.target.getAttribute('aria-posinset') || rating);
                            console.log("Valid rating value (forced last colored star):", validRating);
                            handleRatingChange(event, validRating); 
                        }}
                        onChangeActive={handleLabelChange}
                        icon={<IoStar style={{ fontSize: '2rem', color: '#FFD700', marginRight: '5px' }} />}
                        emptyIcon={<IoStar style={{ fontSize: '2rem', color: '#ddd', marginRight: '5px'  }} />} 
                        />


                        {rating !== null && (
                            <Box sx={{ ml: 2, fontSize: '1rem' }}>
                                {labels[hover !== -1 ? hover : rating]}
                            </Box>
                        )}
                    </Box>
                </div>
                <Divider orientation="vertical" flexItem />
                <div className="product-details-info">
                    <h1 className='product-info-name-text'>{product.name}</h1>
                    <div className="product-details-card">
                        <p className='product-description'>{product.description}</p>
                        <p><strong>Product ID:</strong> {product.pID}</p>
                        <p><strong>Category:</strong> {product.category.catName}</p>
                        <div className="additional-details">
                            <p><strong>Model:</strong> {product.model || 'N/A'}</p>
                            <p><strong>Serial Number:</strong> {product.serialNumber || 'N/A'}</p>
                            <p><strong>Distributor Info:</strong> {product.distributorInfo || 'N/A'}</p>
                            <p>
                                <strong>Warranty Status:</strong> {product.warrantyStatus ? '12 months Warranty' : 'No warranty'}
                            </p>
                        </div>
                        {product.discountPercent && product.discountPercent > 0 ? (
                            <div>
                                <h3 style={{fontSize: '45px;'}}>
                                    <span className='price'>
                                        {`${(product.price * (1 - product.discountPercent / 100)).toFixed(2)} TL`}
                                    </span>
                                    {' '}
                                    <span style={{ textDecoration: 'line-through', color: 'red', fontSize: '15px' }}>
                                        {`${product.price.toFixed(2)} TL`}
                                    </span> 
                                    {' '}
                                    <p>({product.discountPercent}% off)</p>
                                </h3> 
                            </div>
                            ) : (
                            <h3 className='price'>{product.price ? `${product.price} TL` : 'Price not available'}</h3>
                            )}
                    </div>

          
                    {/* Add to Cart Section */}
                    <div className="add-to-cart-container">
                        <div className="quantity-selector">
                            <button onClick={() => handleQuantityChange('decrement')} disabled={product.stock === 0}>
                                -
                            </button>
                            <input type="text" value={quantity} readOnly />
                            <button onClick={() => handleQuantityChange('increment')} disabled={(product.stock === 0)||(product.stock <= quantity)}>
                                +
                            </button>
                        </div>
                        <button 
                            className="add-to-cart-button" 
                            onClick={handleAddToCart}
                            disabled={(product.stock === 0)} // Disable if stock is 0
                        >
                            {product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
                        </button>
                    </div>
                </div>
            </div>

            <div className="comments-section">
                <h2>Customer Reviews</h2>
                <div
                    className={`add-comment-form-container ${showCommentForm ? 'visible' : 'hidden'}`}
                >
                    <textarea
                        value={newComment}
                        onChange={handleCommentChange}
                        placeholder="Write your comment here..."
                    />
                    <button onClick={handleSubmitComment}>Submit Comment</button>
                </div>

                <button
                    className={`add-comment-button ${showCommentForm ? 'cancel' : 'add'}`}
                    onClick={() => setShowCommentForm(!showCommentForm)}
                >
                    {showCommentForm ? 'Cancel' : 'Add Comment'}
                </button>
                {currentProductComments.length === 0 ? (
                    <p>No reviews yet. Be the first to review this product!</p>
                ) : (
                    currentProductComments.map((comment) => (
                        <div key={comment.comID} className="comment">
                            <p>{comment.text}</p>
                            <p>{new Date(comment.createdAt).toLocaleDateString()}</p>
                        </div>
                    ))
                )}

                
            </div>
        </div>
    );
};

export default ProductDetails;
