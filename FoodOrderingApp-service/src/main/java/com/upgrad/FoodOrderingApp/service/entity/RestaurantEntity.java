package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "restaurant")
@NamedQueries({
    @NamedQuery(name = "Restaurants.fetchAll", query = "SELECT r FROM RestaurantEntity r ORDER BY r.customerRating DESC")  })
public class RestaurantEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurantIdGenerator")
    @SequenceGenerator(name = "restaurantIdGenerator", sequenceName = "restaurant_id_seq")
    @ToStringExclude
    @HashCodeExclude
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "restaurant_name")
    @NotNull
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "photo_url")
    @Size(max = 255)
    private String photoUrl;

    @Column(name = "customer_rating")
    @NotNull
    private BigDecimal customerRating;

    @Column(name = "average_price_for_two")
    @NotNull
    private Integer averagePriceForTwo;

    @Column(name = "number_of_customers_rated")
    @NotNull
    private Integer numberOfCustomersRated;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id",referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @ToStringExclude
    @HashCodeExclude
    @EqualsExclude
    private AddressEntity address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "restaurant_category",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")})
    private Set<CategoryEntity> categories = new HashSet<CategoryEntity>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "restaurant_item",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "item_id")})
    private Set<ItemEntity> item = new HashSet<ItemEntity>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public BigDecimal getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(BigDecimal customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getAveragePriceForTwo() {
        return averagePriceForTwo;
    }

    public void setAveragePriceForTwo(Integer averagePriceForTwo) {
        this.averagePriceForTwo = averagePriceForTwo;
    }

    public Integer getNumberOfCustomersRated() {
        return numberOfCustomersRated;
    }

    public void setNumberOfCustomersRated(Integer numberOfCustomersRated) {
        this.numberOfCustomersRated = numberOfCustomersRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }

    public Set<ItemEntity> getItem() {
        return item;
    }

    public void setItem(Set<ItemEntity> item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this,obj,Boolean.FALSE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this,Boolean.FALSE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
