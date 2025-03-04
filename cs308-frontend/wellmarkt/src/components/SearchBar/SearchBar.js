import React, { useContext, useState } from "react";
import "./SearchBar.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from "@fortawesome/free-solid-svg-icons";
import { ProductsContext } from "../../contexts/ProductsContext";
import { useNavigate } from "react-router-dom";

const SearchBar = ({navigation}) => {
  const { searchProducts } = useContext(ProductsContext);
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  const handleSearch = (event) => {
    if (event) {
      event.preventDefault(); 
    }
    if (query.trim() === "") return; // Do nothing if the query is empty
    searchProducts(query); // Perform the search
    navigate(navigation); // Redirect to Products page
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter") {
      handleSearch(event); 
    }
  };

  return (
    <div className="box">
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyDown={handleKeyDown} 
        placeholder="Search..."
      />
      <button onClick={handleSearch} className="search-icon" aria-label="Search">
        <FontAwesomeIcon icon={faSearch} />
      </button>
    </div>
  );
};

export default SearchBar;