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
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.preferences.Preferences;

public class SignUpEmailActivity extends BaseActivity implements View.OnFocusChangeListener {

    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    TextInputEditText confirmPasswordEditText;
    TextInputLayout confirmPasswordLayout;
    FloatingActionButton button;
    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);
        emailLayout = findViewById(R.id.emailTextInputLayout);
        passwordLayout = findViewById(R.id.passwordTextInputLayout);
        emailEditText = findViewById(R.id.emailTextEditText);
        passwordEditText = findViewById(R.id.passwordTextEditText);
        confirmPasswordEditText = findViewById(R.id.confirmpasswordTextEditText);
        confirmPasswordLayout = findViewById(R.id.confirmpasswordTextInputLayout);
        button = findViewById(R.id.nextButton);
        toolbar = findViewById(R.id.toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFunction();
            }
        });

        emailEditText.addTextChangedListener(new MyTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new MyTextWatcher(passwordEditText));
        confirmPasswordEditText.addTextChangedListener(new MyTextWatcher(confirmPasswordEditText));

        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        confirmPasswordEditText.setOnFocusChangeListener(this);

    }

    private void registerFunction() {
        String emailText = emailEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();
        String confirmPasswordText = confirmPasswordEditText.getText().toString().trim();

        if (!validateEmail(emailText)) {
            return;
        }
        if (!validateSetPass(passwordText)) {
            return;
        }
        if (!validateConfirmPass(passwordText, confirmPasswordText)) {
            return;
        }

        showProgressDialog("Signing up...");


        firebaseAuth.createUserWithEmailAndPassword(emailText, confirmPasswordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    hideProgressDialog();
//                    Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    onAuthSuccess(task.getResult().getUser());
                } else {
                    hideProgressDialog();
                    Toast.makeText(SignUpEmailActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateSetPass(String setPass) {
        if (setPass.length() < 8) {
            passwordLayout.setError("Minimum 8 character");
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }

    private boolean validateConfirmPass(String setPass, String confirmPass) {
        if (confirmPass.compareTo(setPass) != 0) {
            confirmPasswordLayout.setError("Passwords don't match");
            return false;
        } else {
            confirmPasswordLayout.setError(null);
        }
        return true;
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Write new user
        writeNewUser(user.getUid(), user.getEmail());
        startActivity(new Intent(SignUpEmailActivity.this, RegisterActivity.class));
        finish();
    }

    private void writeNewUser(String userId, String email) {

        databaseReference.child("users").child(userId);
        databaseReference.child("users").child(userId).child("email").setValue(email);
        Log.d("USERID", userId);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpEmailActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Preferences.USER_ID, userId);
        editor.apply();
        startActivity(new Intent(SignUpEmailActivity.this, RegisterActivity.class));
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (firebaseAuth.getCurrentUser() != null) {
            onAuthSuccess(firebaseAuth.getCurrentUser());
        }
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

            case R.id.confirmpasswordTextEditText:
                if (!hasFocus) {
                    validateConfirmPass(passwordEditText.getText().toString().trim(), confirmPasswordEditText.getText().toString().trim());
                } else {
                    confirmPasswordLayout.setError(null);
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
                case R.id.confirmpasswordTextEditText:
                    confirmPasswordLayout.setError(null);
                    break;

            }
        }
    }
}
