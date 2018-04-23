package savetheplanet.team8.project.com.savetheplanet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.Objects;

import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.RecyclerTouchListener;
import savetheplanet.team8.project.com.savetheplanet.activity.ProductAdaptor;
import savetheplanet.team8.project.com.savetheplanet.activity.ProductRegistration;
import savetheplanet.team8.project.com.savetheplanet.model.Product;
import savetheplanet.team8.project.com.savetheplanet.preferences.Preferences;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProductListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProductListFragment extends Fragment {

    private List<Product> productsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProductAdaptor mAdapter;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton productsAddButton;
    Toolbar toolbar;

    public MyProductListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_all_products, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString(Preferences.USER_ID, null);
        
        toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        toolbar.setTitle("My Products");
        toolbar.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        productsAddButton = view.findViewById(R.id.addProductsButton);
        recyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new ProductAdaptor(productsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = productsList.get(position);
                Toast.makeText(getContext(), product.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        productsAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProductRegistration.class);
                startActivity(intent);
            }
        });

        ///Dummy Data
        //prepareProductData();

        Log.d("TEST", "INSIDWE");
        //Real Data

        populateAllProducts();

        return view;
    }

    private void populateAllProducts() {
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        //populateAllProducts((Map<String,Object>) dataSnapshot.getValue());
                        Log.d("TEST", "on Data Change");
                        Product product;

                        for (DataSnapshot dp : dataSnapshot.child("products").getChildren()) {
                            Log.d("TEST", dp.toString());
                            //product = dp.getValue(Product.class);
                            Map<String, String> prod = (Map<String, String>) dp.getValue();
                            product = new Product(prod.get("name"), prod.get("description"), prod.get("location"), prod.get("tag"), prod.get("imageUrl"));
                            productsList.add(product);
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
}
