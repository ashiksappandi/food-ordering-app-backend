package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CategoryController {
    // TODO :
    //  - Get All Categories - “/category”
    //  - Get Category by Id - “/category/{category_id}”

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET,
            path = "/category",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategoriesOrderedByName() {
        List<CategoryEntity> categoryEntityList = categoryService.getAllCategoriesOrderedByName();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        categoryEntityList.forEach(category ->
            categoriesListResponse.addCategoriesItem(
                    new CategoryListResponse()
                    .id(UUID.fromString(category.getUuid()))
                    .categoryName(category.getCategoryName())
            ));
        return new ResponseEntity<>(categoriesListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
            path = "/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategory(@PathVariable("category_id") final String categoryId ) {

        CategoryEntity categoryEntity = categoryService.getCategory(categoryId);
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                .id(UUID.fromString(categoryEntity.getUuid()))
                .categoryName(categoryEntity.getCategoryName());
        return new ResponseEntity<>(categoryDetailsResponse, HttpStatus.OK);

    }

}
