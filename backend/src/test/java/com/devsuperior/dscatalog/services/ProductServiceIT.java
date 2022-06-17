package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long existsId;
	private Long nonExistingId;
	private Long countFinalProduct;
	
	@BeforeEach
	void setup() throws Exception {
		existsId = 1L;
		nonExistingId = 2000L;
		countFinalProduct = 25L;
	}
	
	@Test
	public void findAllPageShouldReturnSortedPageWhenSortedByName() {
		
		PageRequest page = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAllPaged(page);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
		Assertions.assertEquals("PC Gamer Boo", result.getContent().get(3).getName());
	}
	
	@Test
	public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExists() {
		
		PageRequest page = PageRequest.of(50, 10);
		
		Page<ProductDTO> result = service.findAllPaged(page);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPageShouldReturnPageWhenPage0a10() {
		
		PageRequest page = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(page);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countFinalProduct, result.getTotalElements());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenDoesNotIdExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		service.delete(existsId);
		
		Assertions.assertEquals(countFinalProduct -1, repository.count());
	}
}
