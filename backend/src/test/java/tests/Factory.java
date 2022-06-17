package tests;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class Factory {
	
	public static Product creatProduct() {
	Product product = new Product(1L, "SmartPhone", "MotorolaG20",5000.0, "https://img.com/img.png", Instant.parse("2022-06-26T18:00:00Z"));
		product.getCategories().add(creatCategory());
		return product;
	}
	
	public static Category creatCategory() {
		Category category = new Category(1L, "Eletronics");
		return category;
	}
	
	public static ProductDTO creatProductDTO() {
		Product newProduct = creatProduct();
		return new ProductDTO(newProduct, newProduct.getCategories());
	}
}
