package savetheplanet.team8.project.com.savetheplanet.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.RecyclerTouchListener;
import savetheplanet.team8.project.com.savetheplanet.model.Product;

public class activity_list_products extends AppCompatActivity {

    private List<Product> productsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProductAdaptor mAdapter;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_products);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ProductAdaptor(productsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

// set the adapter
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = productsList.get(position);
                Toast.makeText(getApplicationContext(), product.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ///Dummy Data
        //prepareProductData();

        Log.d("TEST", "INSIDWE");
        //Real Data
        populateAllProducts();

    }

    private void populateAllProducts()
    {
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        //populateAllProducts((Map<String,Object>) dataSnapshot.getValue());
                        Log.d("TEST", "on Data Change");
                        Product product;
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
//                            Log.d("TEST", dsp.toString());
                            for(DataSnapshot dp : dsp.child("products").getChildren())
                            {
                                Log.d("TEST", dp.toString());
                                //product = dp.getValue(Product.class);
                                Map<String, String> prod = (Map<String, String>) dp.getValue();
                                product = new Product(prod.get("name"), prod.get("description"), prod.get("location"),prod.get("tag"));
                                productsList.add(product);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void prepareProductData() {
        Product product = new Product("Mad Max: Fury Road", "Action & Adventure", "2015","dummy");
        productsList.add(product);

        product = new Product("Inside Out", "Animation, Kids & Family", "2015","custom");
        productsList.add(product);

        product = new Product("Star Wars: Episode VII - The Force Awakens", "Action", "2015","custom");
        productsList.add(product);

        product = new Product("Shaun the Sheep", "Animation", "2015","custom");
        productsList.add(product);

        product = new Product("The Martian", "Science Fiction & Fantasy", "2015","dummy");
        productsList.add(product);

        product = new Product("Mission: Impossible Rogue Nation", "Action", "2015","dummy");
        productsList.add(product);

        product = new Product("Up", "Animation", "2009","dummy");
        productsList.add(product);

        product = new Product("Star Trek", "Science Fiction", "2009","dummy");
        productsList.add(product);

        product = new Product("The LEGO Product", "Animation", "2014","dummy");
        productsList.add(product);

        product = new Product("Iron Man", "Action & Adventure", "2008","dummy");
        productsList.add(product);

        product = new Product("Aliens", "Science Fiction", "1986","dummy");
        productsList.add(product);

        product = new Product("Chicken Run", "Animation", "2000","dummy");
        productsList.add(product);

        product = new Product("Back to the Future", "Science Fiction", "1985","dummy");
        productsList.add(product);

        product = new Product("Raiders of the Lost Ark", "Action & Adventure", "1981","dummy");
        productsList.add(product);

        product = new Product("Goldfinger", "Action & Adventure", "1965","dummy");
        productsList.add(product);

        product = new Product("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014","dummy");
        productsList.add(product);

        mAdapter.notifyDataSetChanged();
    }
}
