import React, { useContext, useState } from "react";
import { MenuItem } from "@mui/material";
import { ProductsContext } from "../../contexts/ProductsContext";
import { FaSortDown } from "react-icons/fa6";

const SortOptions = () => {
  // Access ProductsContext to get the function that handles sorting
  const { sortFilteredProducts } = useContext(ProductsContext);
  const [isDropdownVisible, setIsDropdownVisible] = useState(false);
  const [sortOption, setSortOption] = useState("");

  // Handle sorting when an option is clicked
  const handleSortChange = (value) => {
    setSortOption(value);

    if (sortFilteredProducts) {
      sortFilteredProducts(value); // Call the context function to apply sorting
    }
  };

  return (
    <div className="route">
    <div className="sort-options"
      style={{
        position: "relative", // For dropdown positioning
        display: "inline-block",
      }}
      onMouseEnter={() => setIsDropdownVisible(true)} // Show dropdown on hover
      onMouseLeave={() => setIsDropdownVisible(false)} // Hide dropdown on mouse leave
    >
      <p
        style={{
          margin: 0,
          cursor: "pointer",
          display: "inline-flex",
          alignItems: "center",
        }}
      >
        Sort <FaSortDown style={{ marginLeft: "5px" }} />
      </p>

      {isDropdownVisible && (
        <div
        style={{
          position: "absolute",
          backgroundColor: "#2C2F33", // Darker background for better contrast
          borderRadius: "8px", // Softer border radius
          padding: "8px", // Slightly larger padding for better spacing
          boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.3)", // Smooth shadow for a floating effect
        }}
      >
        <MenuItem
          onClick={() => handleSortChange("price-low-to-high")}
          style={{
            fontSize: "16px",
            color: "#F8F9FA", // Light text for dark background
            padding: "10px 16px", // Larger padding for better clickability
            cursor: "pointer",
            transition: "all 0.3s ease", // Smooth transition for hover
            borderRadius: "6px", // Soft corner radius
            margin: "4px 0", // Vertical spacing between items
            backgroundColor: "transparent", // Default background
          }}
          onMouseEnter={(e) => (e.target.style.backgroundColor = "#8f919c")} // Smooth hover color
          onMouseLeave={(e) => (e.target.style.backgroundColor = "transparent")}
        >
          Price Low to High
        </MenuItem>
        <MenuItem
          onClick={() => handleSortChange("price-high-to-low")}
          style={{
            fontSize: "16px",
            color: "#F8F9FA",
            padding: "10px 16px",
            cursor: "pointer",
            transition: "all 0.3s ease",
            borderRadius: "6px",
            margin: "4px 0",
            backgroundColor: "transparent",
          }}
          onMouseEnter={(e) => (e.target.style.backgroundColor = "#8f919c")}
          onMouseLeave={(e) => (e.target.style.backgroundColor = "transparent")}
        >
          Price High to Low
        </MenuItem>
        <MenuItem
          onClick={() => handleSortChange("popularity")}
          style={{
            fontSize: "16px",
            color: "#F8F9FA",
            padding: "10px 16px",
            cursor: "pointer",
            transition: "all 0.3s ease",
            borderRadius: "6px",
            margin: "4px 0",
            backgroundColor: "transparent",
          }}
          onMouseEnter={(e) => (e.target.style.backgroundColor = "#8f919c")}
          onMouseLeave={(e) => (e.target.style.backgroundColor = "transparent")}
        >
          Most to Least Popular
        </MenuItem>
        <MenuItem
          onClick={() => handleSortChange("popularity-low-to-high")}
          style={{
            fontSize: "16px",
            color: "#F8F9FA",
            padding: "10px 16px",
            cursor: "pointer",
            transition: "all 0.3s ease",
            borderRadius: "6px",
            margin: "4px 0",
            backgroundColor: "transparent",
          }}
          onMouseEnter={(e) => (e.target.style.backgroundColor = "#8f919c")}
          onMouseLeave={(e) => (e.target.style.backgroundColor = "transparent")}
        >
          Least to Most Popular
        </MenuItem>
      </div>
      
      )}
    </div>
    </div>
  );
};

export default SortOptions;
