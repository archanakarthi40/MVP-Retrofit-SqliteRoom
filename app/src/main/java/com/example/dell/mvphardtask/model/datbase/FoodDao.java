package com.example.dell.mvphardtask.model.datbase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.example.dell.mvphardtask.model.FoodItems;

import java.util.List;

@Dao
public interface FoodDao {

    //insert cart data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insertFoodItem(List<FoodItems> foodItem);

    @Query("SELECT * FROM food_table")
    List<FoodItems> getAllFoodItems();

    @Update(onConflict = OnConflictStrategy.REPLACE)
     void updateFoodItem(FoodItems foodItem);

    @Query("SELECT count  FROM food_table where item_name LIKE :itemName")
    int getCartItemCount(String itemName);

    @Query("SELECT * FROM food_table WHERE count > 0")
    List<FoodItems> getFoodItemsInCart() ;


}

