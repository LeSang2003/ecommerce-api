package com.demo.service;
import com.demo.model.Category;
import com.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository){
    this.categoryRepository = categoryRepository;
  }

  public List<Category> getAll(){
    return categoryRepository.findAll();
  }

  public Category create(Category category){
    return categoryRepository.save(category);
  }

  public Category update(Long id, Category category){
    Category old = categoryRepository.findById(id).orElseThrow();
    old.setName(category.getName());
    return categoryRepository.save(old);
  }

  public void delete(Long id){
    categoryRepository.deleteById(id);
  }
}
