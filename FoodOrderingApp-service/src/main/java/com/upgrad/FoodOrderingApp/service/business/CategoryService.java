package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        List<CategoryEntity> categoryList =categoryDao.getAllCategoriesOrderedByName();
        return categoryList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryEntity getCategory(String categoryId) {
        CategoryEntity categoryEntity = categoryDao.getCategory(categoryId);
        return categoryEntity;
    }
}
