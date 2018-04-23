package savetheplanet.team8.project.com.savetheplanet.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;
import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.model.Product;

public class ProductRegistration extends BaseActivity {
    TextView displayName;
    TextInputLayout firstNameLayout, lastNameLayout, companyNameLayout;
    TextInputEditText productName, productDescription, productLocation,productTag;
    FloatingActionButton doneButton;
    FancyButton galleryButton, cameraButton, removeButton;
    CircularImageView imageView;
    private StorageReference storageReference;
    DatabaseReference databaseReference;
    String userId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_registration);
        String userId = "RokYULCU3KSwlfhYoCW8OlnOhlq2";
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference();
        doneButton=(FloatingActionButton)findViewById(R.id.doneButton);

        productName= (TextInputEditText) findViewById(R.id.productNameTextEditText);

        productDescription= (TextInputEditText) findViewById(R.id.productDescriptionTextEditText);
        productLocation = (TextInputEditText) findViewById(R.id.productlocation);
        productTag= (TextInputEditText) findViewById(R.id.productTagTextEditText);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerProduct();
            }
        });
    }
    public void registerProduct(){
        String name=productName.getText().toString();
        String description=productDescription.getText().toString();
        String location=productLocation.getText().toString();
        String tag=productTag.getText().toString();
        if(validateForm(name,(TextInputLayout)findViewById(R.id.firstNameTextInputLayout)))
            return;
        else if(validateForm(description,(TextInputLayout)findViewById(R.id.DescTextInputLayout)))
            return;
        else if(validateForm(location,(TextInputLayout)findViewById(R.id.companyNameTextInputLayout)))
            return;
        else if(validateForm(tag,(TextInputLayout)findViewById(R.id.productTagTextInputLayout)))
            return;


        Product product=new Product(name,description,location,tag);



            List<Product> plist=new ArrayList<Product>();



            DatabaseReference dr=databaseReference.child("products").push();
            dr.setValue(product);


        }
    private boolean validateForm(String name, TextInputLayout textInputLayout) {
        boolean valid = true;
        String textOnlyRegex = "^[\\p{L} .'-]+$";
        if (TextUtils.isEmpty(name) || !Pattern.matches(textOnlyRegex, name)) {
            textInputLayout.setError("Enter a valid name");
            valid = false;
        } else {
            textInputLayout.setError(null);
        }

        return !valid;
    }


}
