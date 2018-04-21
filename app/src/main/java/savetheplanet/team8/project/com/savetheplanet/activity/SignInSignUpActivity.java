package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mehdi.sakout.fancybuttons.FancyButton;
import savetheplanet.team8.project.com.savetheplanet.R;

public class SignInSignUpActivity extends AppCompatActivity {

    FancyButton signInButton;
    FancyButton signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignInSignUpActivity.this, SignInEmailActivity.class));

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignInSignUpActivity.this, SignUpEmailActivity.class));
            }
        });
    }
}
