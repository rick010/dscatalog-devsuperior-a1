package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;

import tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	ProductRepository repository;
	
	private Long existingId;
	
	private Long nonExistingId;
	
	private Long countFinalProduct;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 2000L;
		countFinalProduct = 25L;
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdNull() {
		Product product = Factory.creatProduct();
		product.setId(null);
		product = repository.save(product);
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countFinalProduct + 1, product.getId());
	}
	
	@Test
	public void findByIdShouldReturnNonEmptyOptionaProductlWhenIdExists() {
		Optional<Product> obj = repository.findById(existingId); 
		Assertions.assertTrue(obj.isPresent());;
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionaProductlWhenDoesNotIdExists() {
		Optional<Product> obj = repository.findById(nonExistingId); 
		Assertions.assertFalse(obj.isPresent());;
	}
	
	@Test
	public void deleteShouldDeleteObjetWhenIdExists() {
		
		repository.deleteById(existingId);
		
		Optional<Product> obj = repository.findById(existingId); 
		
		Assertions.assertFalse(obj.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenObjetWhenIdNotexists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}
}
