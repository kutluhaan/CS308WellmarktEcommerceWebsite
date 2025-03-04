package wellmarkt_cs308.wellmarkt_cs308_ecommerce.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wellmarkt_cs308.wellmarkt_cs308_ecommerce.DAO.ProductDAO;
import wellmarkt_cs308.wellmarkt_cs308_ecommerce.Entities.Product;

@Service
public class ProductServices {
	@Autowired 
	private final ProductDAO prodDAO;
	
    public ProductServices(ProductDAO prodDAO) { 
    	this.prodDAO = prodDAO; 
    }
    
    public Product findProductById(String pID) {
    	return prodDAO.findById(pID).get();
    }
	
    public Optional<Product> findProductByIdOptional(String pID) {
        return prodDAO.findById(pID);
    }
    
    public List<Product> allProducts() {
        return prodDAO.findAll();
    }
    
    public List<Product> allProductsById(List<String> allProductIds){
    	List<Product> productsOnDelivery = new ArrayList<>();
    	for (String pID: allProductIds) {
    		productsOnDelivery.add(findProductById(pID));
    	}
    	return productsOnDelivery;
    }
    
	public Product saveProduct(Product product) {
        return prodDAO.save(product);
    }
	
    public List<Product> saveAllProducts(List<Product> products) {
        return prodDAO.saveAll(products);
    }
    
    public boolean isProductExists(String pID) {
        return prodDAO.findById(pID).isPresent();
    }
    
    public List<Product> getAllProducts() {
    	return prodDAO.findAll();
    }
    
    public Product updateProductById(String pID, Product updatedProduct) {
        Product product = findProductById(pID);
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setImageURL(updatedProduct.getImageURL());
        product.setStock(updatedProduct.getStock());
        product.setActive(updatedProduct.isActive());
        product.setRating(updatedProduct.getRating());
        product.setCategory(updatedProduct.getCategory());
        product.setBrand(updatedProduct.getBrand());
        product.setUpdatedAt(updatedProduct.getUpdatedAt());
        product.setModel(updatedProduct.getModel());//
        product.setSerialNumber(updatedProduct.getSerialNumber());//
        product.setPrice(updatedProduct.getPrice()); //careful
        product.setWarrantyStatus(updatedProduct.getWarrantyStatus());//
        product.setDistributorInfo(updatedProduct.getDistributorInfo()); //
        return saveProduct(product); // Save and return updated product
    }
    
    public void deleteProductById(String pID) {
		prodDAO.deleteById(pID);
    }
    
    public List<Product> findByCategory(String category) { // we will going to use that code 
    	return prodDAO.findByCategoryContains(category);
    }
    

    public Product updateStock(String pID, Integer stock) {
    	Product product = findProductById(pID);
        product.setStock(stock); // Update stock
        return updateProductById(pID, product); // Save updated product to the database
    }

    public List<Product> searchProductsByQuery(String query) {
        return prodDAO.searchByQuery(query);
    }

}
