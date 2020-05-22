package com.example.owner_test;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.owner_test.Model.ProductModel;
import com.example.owner_test.View.CartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CustomerProductPage extends AppCompatActivity implements com.example.owner_test.Adapter.ProductAdapter.CallBackUs, com.example.owner_test.Adapter.ProductAdapter.HomeCallBack {

    public static ArrayList<ProductModel> arrayList = new ArrayList<>();
    public static ArrayList<String> items = new ArrayList<String>();
    public static ArrayList<Long> quantity = new ArrayList<Long>();
    public static Map<String,Object> stock = new HashMap<>();

    public static int cart_count = 0;
    FirebaseFirestore fStore;
    com.example.owner_test.Adapter.ProductAdapter productAdapter;
    RecyclerView productRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_page);
        fStore = FirebaseFirestore.getInstance();
        //linkDatabase();

        addProduct();
        productAdapter = new com.example.owner_test.Adapter.ProductAdapter(arrayList, this, this);
        productRecyclerView = findViewById(R.id.product_recycler_view);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productRecyclerView.setAdapter(productAdapter);

    }

    public void linkDatabase(){
        fStore.collection("OwnerStock").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.exists() && document!=null) {
                            //String item_name = document.getString("apple");
                            long qty = document.getLong("Apple");
                            long qty2 = document.getLong("Banana");
                            if(document.getLong("Apple") != null){
                               items.add("apple");
                               quantity.add(qty);
                               stock.put("apple",qty);
                            }
                            else{
                                Toast.makeText(CustomerProductPage.this,"Fail",Toast.LENGTH_SHORT).show();
                            }
                            if(document.getLong("Banana") != null){
                                items.add("banana");
                                quantity.add(qty2);
                                stock.put("banana",qty);
                            }
                            else{
                                Toast.makeText(CustomerProductPage.this,"Fail",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                } else {
                    Log.i("TAG", "Error getting documents: ");
                }
            }
        });



    }


    private void addProduct() {
        fStore.collection("OwnerStock").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.exists() && document!=null) {
                            //String item_name = document.getString("apple");
                            long qty = document.getLong("Apple");
                            long qty2 = document.getLong("Banana");
                            if(document.getLong("Apple") != null){
                                items.add("apple");
                                quantity.add(qty);
                           }
                            else{
                                Toast.makeText(CustomerProductPage.this,"Fail",Toast.LENGTH_SHORT).show();
                            }
                            if(document.getLong("Banana") != null){
                                items.add("banana");
                                quantity.add(qty2);
                                stock.put("banana",qty);
                            }
                            else{
                                Toast.makeText(CustomerProductPage.this,"Fail",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                } else {
                    Log.i("TAG", "Error getting documents: ");
                }
            }
        });

        ProductModel productModel1 = new ProductModel(items.get(0), "20", "10", R.drawable.grocery);
        arrayList.add(productModel1);
        ProductModel productModel2 = new ProductModel(items.get(0), "20","20", R.drawable.grocery);
        arrayList.add(productModel2);

    }

    @Override
    public void addCartItemView() {
        //addItemToCartMethod();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        menuItem.setIcon(Converter.convertLayoutToImage(CustomerProductPage.this, cart_count, R.drawable.ic_shopping_cart_white_24dp));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.cart_action:
                if (cart_count < 1) {
                    Toast.makeText(this, "there is no item in cart", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, CartActivity.class));
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void updateCartCount(Context context) {
        invalidateOptionsMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }
}
