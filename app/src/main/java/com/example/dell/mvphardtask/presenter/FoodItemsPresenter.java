package com.example.dell.mvphardtask.presenter;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dell.mvphardtask.model.FoodItems;
import com.example.dell.mvphardtask.view.adapter.FoodItemAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.constraint.Constraints.TAG;

public class FoodItemsPresenter  {
    private static final String TAG = FoodItemsPresenter.class.getSimpleName();

    public List<FoodItems> mFoodItemsList;
    public Context context;

    public FoodItemsPresenter(List<FoodItems> mFoodItemsList, Context context) {
        this.mFoodItemsList = mFoodItemsList;
        this.context = context;
    }

    public interface FoodItemsCallBack {
        void onDoneLoadingFoodData(List<FoodItems> foodItemsList);
    }

    public void getFoodItems(final FoodItemsCallBack callBack) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        FoodApi api = retrofit.create(FoodApi.class);

        Call<List<FoodItems>> call = api.getFoods();
        call.enqueue(new Callback<List<FoodItems>>() {
            @Override
            public void onResponse(Call<List<FoodItems>> call, Response<List<FoodItems>> response) {
                mFoodItemsList = response.body();
                callBack.onDoneLoadingFoodData(mFoodItemsList);

            }

            @Override
            public void onFailure(Call<List<FoodItems>> call, Throwable t) {
                mFoodItemsList = null;
                Log.v(TAG, "On_Failure" + mFoodItemsList);
                callBack.onDoneLoadingFoodData(mFoodItemsList);

            }

        });

    }

}