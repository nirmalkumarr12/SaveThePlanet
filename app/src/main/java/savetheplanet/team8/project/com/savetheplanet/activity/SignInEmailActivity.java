package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.model.User;
import savetheplanet.team8.project.com.savetheplanet.preferences.Preferences;

public class SignInEmailActivity extends BaseActivity implements View.OnFocusChangeListener {
    FirebaseAuth firebaseAuth;
    protected TextInputLayout emailLayout;
    protected TextInputEditText emailEditText;
    protected TextInputLayout passwordLayout;
    protected TextInputEditText passwordEditText;
//    @BindView(R.id.btn_reset_password)
//    protected Button reset_pass;
    protected FloatingActionButton doneButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email);
        firebaseAuth = FirebaseAuth.getInstance();
        emailLayout = findViewById(R.id.emailTextInputLayout);
        passwordLayout = findViewById(R.id.passwordTextInputLayout);
        emailEditText = findViewById(R.id.emailTextEditText);
        passwordEditText = findViewById(R.id.passwordTextEditText);
        doneButton = findViewById(R.id.doneButton);

        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        emailEditText.addTextChangedListener(new MyTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new MyTextWatcher(passwordEditText));

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

//        reset_pass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent resetPassIntent = new Intent(SignInActivity.this, ResetPassActivity.class);
//                resetPassIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(resetPassIntent);
//                finish();
//            }
//        });


    }

    private void signInUser() {
        String emailText = emailEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();

        if (!validateEmail(emailText)) {
            return;
        }
        if (!validateSetPass(passwordText)) {
            return;
        }

        showProgressDialog("Signing in...");
        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Toast.makeText(SignInEmailActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            hideProgressDialog();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    Log.e("key", dataSnapshot.getKey());
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignInEmailActivity.this);
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

                            Intent intent = new Intent(SignInEmailActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean validateEmail(String emailText) {

        if (emailText.isEmpty() || !isValidEmail(emailText)) {
            emailLayout.setError("Enter a valid email");
            return false;
        } else {
            emailLayout.setError(null);
        }

        return true;
    }

    private boolean validateSetPass(String setPass) {
        if (TextUtils.isEmpty(setPass)) {
            passwordLayout.setError("Password cannot be empty");
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {

            case R.id.emailTextEditText:
                if (!hasFocus) {
                    validateEmail(emailEditText.getText().toString().trim());
                } else {
                    emailLayout.setError(null);
                }

                break;
            case R.id.passwordTextEditText:
                if (!hasFocus) {
                    validateSetPass(passwordEditText.getText().toString().trim());
                } else {
                    passwordLayout.setError(null);
                }

                break;
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.emailTextEditText:
                    emailLayout.setError(null);
                    break;
                case R.id.passwordTextEditText:
                    passwordLayout.setError(null);
                    break;

            }
        }
    }
}
