package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.map.MapsActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        // Buttons
        findViewById(R.id.log_in_button).setOnClickListener(this);
        findViewById(R.id.sign_up_button).setOnClickListener(this);


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Check if user is already signed in
        if (mFirebaseUser != null) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra(MapsActivity.USERNAME_KEY, "");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_in_button:
                startIntent(LogInActivity.class);
                break;
            case R.id.sign_up_button:
                startIntent(CreateAccountActivity.class);
                break;
        }
    }

    private void startIntent(Class activity) {
        Intent i = new Intent(this, activity);
        startActivity(i);
    }
}
