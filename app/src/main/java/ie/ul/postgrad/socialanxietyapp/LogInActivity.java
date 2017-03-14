package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        findViewById(R.id.forgot_password_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);
    }

    private void attemptLogin() {
        String email = ((EditText) findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_field)).getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {

            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(LogInActivity.this, getString(R.string.authentication_failed) + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(LogInActivity.this, MapsActivity.class));
                                Toast.makeText(LogInActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

        } else {
            Toast.makeText(LogInActivity.this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_password_button:
                Intent intent = new Intent(LogInActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.continue_button:
                attemptLogin();
                break;
        }
    }
}
