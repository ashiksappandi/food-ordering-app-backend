package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.business.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.junit.experimental.categories.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        List<CategoryEntity> categoryEntityList = categoryService.getAllCategories();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        categoryEntityList.forEach(category ->
            categoriesListResponse.addCategoriesItem(
                    new CategoryListResponse()
                    .id(category.getUuid())
                    .categoryName(category.getCategoryName())
            ));
        return new ResponseEntity<>(categoriesListResponse, HttpStatus.OK);
    }

}
