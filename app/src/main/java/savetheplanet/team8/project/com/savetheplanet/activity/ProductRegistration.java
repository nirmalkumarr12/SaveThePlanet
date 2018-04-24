package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;
import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.model.Product;

public class ProductRegistration extends BaseActivity {
    TextInputLayout firstNameLayout, lastNameLayout, companyNameLayout;
    TextInputEditText productName, productDescription, productLocation, productTag;
    FloatingActionButton doneButton;
    FancyButton galleryButton, cameraButton, removeButton;
    CircularImageView imageView;
    private StorageReference storageReference;
    DatabaseReference databaseReference;
    public static final int RC_SIGN_IN = 123;
    private static final int RC_PHOTO_PICKER = 2;
    public static final int RC_CAMERA_CODE = 123;
    String userId;
    private FirebaseAuth firebaseAuth;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_registration);
        userId = getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference();
        doneButton = findViewById(R.id.doneButton);
        removeButton = findViewById(R.id.productremove);
        cameraButton = findViewById(R.id.productCamera);
        galleryButton = findViewById(R.id.productgallery);
        imageView = findViewById(R.id.productPicture);
        productName = findViewById(R.id.productNameTextEditText);

        productDescription = findViewById(R.id.productDescriptionTextEditText);
        productLocation = findViewById(R.id.productlocation);
        productTag = findViewById(R.id.productTagTextEditText);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "remove");
                databaseReference.child("profile").setValue("https://firebasestorage.googleapis.com/v0/b/phms-65aa3.appspot.com/o/ic_account_circle_black_48dp.png?alt=media&token=20dba348-4406-4117-86ee-d2b0a06280d5");
                Toast.makeText(ProductRegistration.this, "Profile Picture Removed", Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RC_CAMERA_CODE);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerProduct();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            final StorageReference photoref = storageReference.child(userId).child(selectedImageUri.getLastPathSegment());
            photoref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Picasso.with(ProductRegistration.this).load(taskSnapshot.getDownloadUrl()).into(imageView);
                    imageUrl = taskSnapshot.getDownloadUrl().toString();
//                    databaseReference.child("profile").setValue(Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString());
                    Toast.makeText(ProductRegistration.this, "Profile Picture Set", Toast.LENGTH_SHORT).show();

                }
            });

        } else if (requestCode == RC_CAMERA_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            imageView.setImageBitmap(imageBitmap);

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] d = baos.toByteArray();

            UploadTask uploadTask = storageReference.child(userId).putBytes(d);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUrl = taskSnapshot.getDownloadUrl().toString();
//                    databaseReference.child("profile").setValue(taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(ProductRegistration.this, "Profile Picture Set", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("fail", "fail");
                }
            });

        }
    }

    public void registerProduct() {
        showProgressDialog("Registering product...");
        String name = productName.getText().toString();
        String description = productDescription.getText().toString();
        String location = productLocation.getText().toString();
        String tag = productTag.getText().toString();
        if (validateForm(name, (TextInputLayout) findViewById(R.id.firstNameTextInputLayout))) {
            hideProgressDialog();
            return;
        } else if (validateForm(description, (TextInputLayout) findViewById(R.id.DescTextInputLayout))) {
            hideProgressDialog();
            return;
        } else if (validateUrl(location, (TextInputLayout) findViewById(R.id.companyNameTextInputLayout))) {
            hideProgressDialog();
            return;
        } else if (validateForm(tag, (TextInputLayout) findViewById(R.id.productTagTextInputLayout))) {
            hideProgressDialog();
            return;
        }

        if (!location.startsWith("http://") && !location.startsWith("https://"))
            location = "http://" + location;

        Product product = new Product(name, description, location, tag, imageUrl);

        DatabaseReference dr = databaseReference.child("products").push();
        dr.setValue(product);

        hideProgressDialog();
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProductRegistration.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    private boolean validateForm(String name, TextInputLayout textInputLayout) {
        boolean valid = true;
        String textOnlyRegex = "^[\\p{L} .'-]+$";
        if (TextUtils.isEmpty(name)) {
            textInputLayout.setError("Enter a valid name");
            valid = false;
        } else {
            textInputLayout.setError(null);
        }

        return !valid;
    }

    private boolean validateUrl(String url, TextInputLayout textInputLayout) {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            textInputLayout.setError("Enter a valid url");
            return true;
        }
        return false;
    }


}
