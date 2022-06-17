package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existsId;
	private Long nonExistingId;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	private ProductDTO productDTO;
	
	@BeforeEach
	void setup() throws Exception {
		existsId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		product = Factory.creatProduct();
		category = Factory.creatCategory();
		page = new PageImpl<>(List.of(product));
		productDTO = new ProductDTO(product);
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(product));
		Mockito.when(repository.getOne(existsId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(existsId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.doNothing().when(repository).deleteById(existsId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptiontWhenDoesNotIdExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProducDTOtWhenIdExists() {
		
		ProductDTO updateDTO = service.update(existsId, productDTO);
		
		
		Assertions.assertNotNull(updateDTO);
		Mockito.verify(repository, Mockito.times(1)).getOne(existsId);
		Mockito.verify(repository, Mockito.times(1)).save(product);
//		Mockito.verify(categoryRepository, Mockito.times(1)).getOne(existsId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenDoesNotIdExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void findByIdShouldReturnProducDTOtWhenIdExists() {
		
		ProductDTO findByIdProductDTO = service.findById(existsId);
		
		Assertions.assertNotNull(findByIdProductDTO);
		Mockito.verify(repository, Mockito.times(1)).findById(existsId);
	}
	
//	@Test
//	public void insertShouldSaveAndReturnProductDTO() {
//		
//		ProductDTO resultDTO = service.insert(productDTO);
//		
//		Assertions.assertNotNull(resultDTO);
//		Mockito.verify(repository, Mockito.times(1)).save(product);
//	}
	
	@Test
	public void findAllPageShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentIdExists() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenDoesNotIdExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow( () -> {
			service.delete(existsId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existsId);
	}
}
