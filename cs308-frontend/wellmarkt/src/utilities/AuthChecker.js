import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const useAuthRedirect = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Check if authToken exists in localStorage
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
      // Redirect to the sign-in page if no authToken
      navigate('/sign-in');
    }
  }, [navigate]); // Dependency on navigate to ensure hook updates if needed
};

export default useAuthRedirect;