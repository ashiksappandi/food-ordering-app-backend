package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;



@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

     @Autowired
    private RestaurantService restaurantService;

    // TODO :
    //  - Get All Restaurants - "/restaurant"
    //  - Get Restaurant/s by Name - “/restaurant/name/{reastaurant_name}”
    //  - Get Restaurants by Category Id “/restaurant/category/{category_id}”
    //  - Get Restaurant by Restaurant ID - “/api/restaurant/{restaurant_id}”
    //  - Update Restaurant Details- “/api/restaurant/{restaurant_id}”

    @RequestMapping(path = "/restaurant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurantDetails(){
        List<RestaurantList> restaurantList = new ArrayList<RestaurantList>();
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByRating();

        for (RestaurantEntity restaurantEntity: restaurantEntityList) {
             RestaurantList restaurant = new RestaurantList();
             restaurant.setId(UUID.fromString(restaurantEntity.getUuid()));
             restaurant.setRestaurantName(restaurantEntity.getRestaurantName());
             restaurant.setPhotoURL(restaurantEntity.getPhotoUrl());
             restaurant.setCustomerRating(restaurantEntity.getCustomerRating());
             restaurant.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());

             RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress();
             address.setId(UUID.fromString((restaurantEntity.getAddress().getUuid())));
             address.setFlatBuildingName(restaurantEntity.getAddress().getFlatBuilNo());
             address.setLocality(restaurantEntity.getAddress().getLocality());
             address.setCity(restaurantEntity.getAddress().getCity());
             address.setPincode(restaurantEntity.getAddress().getPincode());
             RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState();
             state.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
             state.setStateName(restaurantEntity.getAddress().getState().getStateName());
             address.setState(state);
             restaurant.setAddress(address);

             Set<CategoryEntity> categoryEntityList = restaurantEntity.getCategories();
             List<String> categoryNames = new ArrayList<>();
             for (CategoryEntity category: categoryEntityList) {
                categoryNames.add(category.getCategoryName());
             }
             Collections.sort(categoryNames);
             String categoryString = String.join(", ",categoryNames);
             restaurant.setCategories(categoryString);

            restaurantList.add(restaurant);

        }
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        restaurantListResponse.setRestaurants(restaurantList);
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

}
