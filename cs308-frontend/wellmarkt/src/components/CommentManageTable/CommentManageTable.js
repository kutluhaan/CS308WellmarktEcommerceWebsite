

import React, { useState, useEffect } from 'react';
import './CommentManageTable.css';
import apiClient from '../../api/axios';

const CommentManagement = () => {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPendingComments = async () => {
      try {
        const response = await apiClient.get('product-manager/comments/pending');
        const comments = response.data.comments;
        const reversedComments = [...comments].reverse(); 
        setComments(reversedComments);
        console.log('Fetched Comments:', response.data.comments);
      } catch (err) {
        setError('Failed to load comments.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchPendingComments();
  }, []);

  const approveComment = async (comID) => {
    try {
      await apiClient.put(`comment/approve/${comID}?isApproved=true`);
      setComments(comments.filter((comment) => comment.comment.comID !== comID));
      alert('Comment approved successfully!');
    } catch (err) {
      alert('Failed to approve comment.');
      console.error(err);
    }
  };

  const declineComment = async (comID) => {
    try {
      await apiClient.delete(`comment/delete-comment/${comID}`);
      setComments(comments.filter((comment) => comment.comment.comID !== comID));
      alert('Comment declined successfully!');
    } catch (err) {
      alert('Failed to decline comment.');
      console.error(err);
    }
  };

  if (loading) return <p>Loading comments...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="comment-management">
      <div className="comment-container">
        <h1 className='comments-table-title'>Pending Comments</h1>
        {comments.length === 0 ? (
          <p className="no-comments">No pending comments.</p>
        ) : (
          <ul className="comment-list">
            {comments.map(({ comment }) => (
              <li key={comment.comID} className="comment-item">
                <div className="comment-text">
                  <p>{comment.text}</p>
                </div>
                <div className="comment-pid">
                  <p><strong>Product ID:</strong> {comment.pID}</p>
                  <p><strong>Product Name:</strong> {comment.productName}</p>
                </div>
                <div className="comment-actions">
                  <button onClick={() => approveComment(comment.comID)} className="approve-button">
                    <i className="fas fa-check-circle"></i> Approve
                  </button>
                  <button onClick={() => declineComment(comment.comID)} className="decline-button">
                    <i className="fas fa-times-circle"></i> Decline
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default CommentManagement;


