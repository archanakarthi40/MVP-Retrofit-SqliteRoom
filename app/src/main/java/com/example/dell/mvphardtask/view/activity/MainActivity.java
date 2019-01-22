package com.example.dell.mvphardtask.view.activity;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.mvphardtask.R;
import com.example.dell.mvphardtask.model.FoodItems;
import com.example.dell.mvphardtask.model.datbase.FoodDatabase;
import com.example.dell.mvphardtask.presenter.FoodItemsPresenter;
import com.example.dell.mvphardtask.view.adapter.FoodItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FoodItemsPresenter.FoodItemsCallBack,FoodItemAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    List<FoodItems> foodItems;
    FoodItemsPresenter foodItemsPresenter;
    FoodItemAdapter adapter;
    FoodDatabase db;
    TextView textCartItemCount;
    int mCartItemCount = 0;
    private static final int RADIO_RATING = 111;
    private static final int RADIO_LOW = 122;
    private static final int RADIO_HIGH = 133;

    private int buttonCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new FoodItemAdapter(foodItems,this,MainActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemsPresenter=new FoodItemsPresenter(foodItems,this);
        db= FoodDatabase.getDatabase(MainActivity.this);

    }

    protected void onStart() {
        super.onStart();
        foodItemsPresenter.getFoodItems(this);
    }

    @Override
    public void onDoneLoadingFoodData(List<FoodItems> foodItemsList) {
        if (foodItemsList == null) {
            foodItems = foodItemsList;
            adapter.setFoodItems(foodItemsList);
        }
        else {
            foodItems = foodItemsList;
            adapter.setFoodItems(foodItemsList);
            recyclerView.setAdapter(adapter);
            db.foodDao().insertFoodItem(foodItemsList);
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge(mCartItemCount);

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_cart:
                Intent i= new Intent(MainActivity.this,CartActivity.class);
                startActivity(i);
                break;
            case R.id.action_filter:
                showPopUp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_layout, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final RadioButton radioRating = dialogView.findViewById(R.id.radio_rating);
        final RadioButton radioLow = dialogView.findViewById(R.id.radio_sort_low);
        final RadioButton radioHigh = dialogView.findViewById(R.id.radio_sort_high);
        Button applyButton = dialogView.findViewById(R.id.btn_apply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioRating.isChecked())
                    buttonCode = RADIO_RATING;
                else if (radioHigh.isChecked())
                    buttonCode = RADIO_HIGH;
                else if (radioLow.isChecked())
                    buttonCode = RADIO_LOW;

                changeSort(buttonCode);
                alertDialog.dismiss();
            }
        });

        Button closeButton = (Button) dialogView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    private void changeSort(int radioButtonCode) {
        if (foodItems != null) {
            ArrayList<FoodItems> foodItemArrayList = new ArrayList<>(foodItems.size());
            foodItemArrayList.addAll(foodItems);
            switch (radioButtonCode) {

                case RADIO_RATING:
                    Collections.sort(foodItems, new Comparator<FoodItems>() {

                        @Override
                        public int compare(FoodItems foodItem1, FoodItems foodItem2) {
                            double rating1=Double.parseDouble(foodItem1.getAverage_rating());
                            double rating2=Double.parseDouble(foodItem2.getAverage_rating());
                            return Double.valueOf(rating1).compareTo(Double.valueOf(rating2));
                        }
                    });
                    break;
                case RADIO_HIGH:
                    Collections.sort(foodItems, new Comparator<FoodItems>() {
                        @Override
                        public int compare(FoodItems foodItem1, FoodItems foodItem2) {
                            return Double.valueOf(foodItem2.getAverage_rating()).compareTo(Double.valueOf(foodItem1.getItem_price()));
                        }
                    });
                    break;
                case RADIO_LOW:
                    Collections.sort(foodItems, new Comparator<FoodItems>() {
                        @Override
                        public int compare(FoodItems foodItem1, FoodItems foodItem2) {
                            double price1=Double.parseDouble(foodItem1.getItem_price());
                            double price2=Double.parseDouble(foodItem2.getItem_price());
                            return Double.valueOf(price1).compareTo(Double.valueOf(price2));
                        }
                    });
            }
            adapter.setFoodItems(foodItems);
           // getfoods();
        }
    }
    private void setupBadge(int mCartItemCount) {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.INVISIBLE) {
                    textCartItemCount.setVisibility(View.INVISIBLE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onFoodItemClick(FoodItems item) {
        db.foodDao().updateFoodItem(item);
        List<FoodItems> foodItems =db.foodDao().getFoodItemsInCart();
        Log.e("foodupdate",""+db.foodDao().getCartItemCount(String.valueOf(item.getItem_name())));
        setupBadge(foodItems.size());

    }
}
