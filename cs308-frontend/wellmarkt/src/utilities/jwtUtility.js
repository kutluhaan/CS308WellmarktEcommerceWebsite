import { jwtDecode } from "jwt-decode";

export const getDecodedToken = () => {
  const token = localStorage.getItem("authToken");
  //console.log("token is: " + token);
  if (!token) return null;

  try {
    const decoded = jwtDecode(token.substring(7));
    //console.log("decoded is:" + decoded);
    return decoded;
  } catch (error) {
    console.error("Invalid JWT:", error.message);
    return null;
  }
};

export const isTokenExpired = () => {
  const decodedToken = getDecodedToken();
  if (!decodedToken) return true;

  const currentTime = Math.floor(Date.now() / 1000); // Current time in seconds
  return decodedToken.exp && decodedToken.exp < currentTime; // Check expiration
};

export const getUserRole = () => {
  const decodedToken = getDecodedToken();
  return decodedToken ? decodedToken.role : null;
};