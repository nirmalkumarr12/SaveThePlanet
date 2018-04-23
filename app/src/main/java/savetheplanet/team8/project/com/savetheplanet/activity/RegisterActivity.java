package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;
import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.model.User;
import savetheplanet.team8.project.com.savetheplanet.preferences.Preferences;

public class RegisterActivity extends BaseActivity {
    TextView displayName;
    TextInputLayout firstNameLayout, lastNameLayout, companyNameLayout;
    TextInputEditText firstNameEditText, lastNameEditText, companyNameEditText;
    FloatingActionButton doneButton;
    FancyButton galleryButton, cameraButton, removeButton;
    CircularImageView imageView;
    public static final int RC_SIGN_IN = 123;
    private static final int RC_PHOTO_PICKER = 2;
    public static final int RC_CAMERA_CODE = 123;
    private StorageReference storageReference;
    DatabaseReference databaseReference;
    String userId;
    RadioGroup userTypeRadioGroup;
    CheckBox checkBox;
    private FirebaseAuth firebaseAuth;
    String userType = "producer";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        System.out.print(getUid());
        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            storageReference = FirebaseStorage.getInstance().getReference();

        } else {
            onAuthFailure();
        }

            firstNameLayout = findViewById(R.id.firstNameTextInputLayout);
            lastNameLayout = findViewById(R.id.lastNameTextInputLayout);
            companyNameLayout = findViewById(R.id.companyNameTextInputLayout);
            firstNameEditText = findViewById(R.id.firstNameTextEditText);
            lastNameEditText = findViewById(R.id.lastNameTextEditText);
            companyNameEditText = findViewById(R.id.companyNameTextEditText);
        doneButton = findViewById(R.id.doneButton);
        removeButton = findViewById(R.id.removeButton);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        imageView = findViewById(R.id.profilePicture);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        checkBox = findViewById(R.id.agreeToTerms);

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
                Toast.makeText(RegisterActivity.this, "Profile Picture Removed", Toast.LENGTH_SHORT).show();
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
                registerFunction();
            }
        });

        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectRadioButton = userTypeRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectRadioButton);
                Log.d("Entered", "success");
                if (radioButton.getText().toString().equalsIgnoreCase("producer")) {
                    companyNameLayout.setVisibility(View.VISIBLE);
                    userType = "producer";
                } else {
                    companyNameLayout.setVisibility(View.GONE);
                    userType = "consumer";
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            StorageReference photoref = storageReference.child(userId).child(Objects.requireNonNull(selectedImageUri).getLastPathSegment());
            photoref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Picasso.with(RegisterActivity.this).load(taskSnapshot.getDownloadUrl()).into(imageView);
                    databaseReference.child("profile").setValue(Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString());
                    Toast.makeText(RegisterActivity.this, "Profile Picture Set", Toast.LENGTH_SHORT).show();

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
                    databaseReference.child("profile").setValue(Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString());
                    Toast.makeText(RegisterActivity.this, "Profile Picture Set", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("fail", "fail");
                }
            });

        }
    }

    private void registerFunction() {
        showProgressDialog("Saving...");
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String companyName = companyNameEditText.getText().toString().trim();

        if (validateForm(firstName, firstNameLayout)) {
            hideProgressDialog();
            return;
        }
        if (validateForm(lastName, lastNameLayout)) {
            hideProgressDialog();
            return;
        }
        if (!checkBox.isChecked()) {
            hideProgressDialog();
            Toast.makeText(RegisterActivity.this, "Agree to terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("firstName").setValue(firstName);
        databaseReference.child("lastName").setValue(lastName);
        databaseReference.child("userType").setValue(userType);
        if (!companyName.isEmpty()) {
            databaseReference.child("companyName").setValue(companyName);
        } else {
            databaseReference.child("companyName").setValue("");
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.e("key", dataSnapshot.getKey());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                if (user != null) {
                    editor.putString(Preferences.FIRST_NAME, user.getName());
                    editor.putString(Preferences.LAST_NAME, user.getLastName());
                    editor.putString(Preferences.EMAIL, user.getEmail());
                }
                editor.putString(Preferences.USER_ID, getUid());
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        hideProgressDialog();
        Toast.makeText(RegisterActivity.this, "User details saved", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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

    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(RegisterActivity.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (firebaseAuth.getCurrentUser() == null) {
            onAuthFailure();
        }
    }

    private void onAuthSuccess() {
        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        finish();

    }
}
