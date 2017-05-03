package ie.ul.postgrad.socialanxietyapp.account;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ie.ul.postgrad.socialanxietyapp.FontManager;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.map.MapsActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText) findViewById(R.id.name_field)).getText().toString();
                String email = ((EditText) findViewById(R.id.email_field)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_field)).getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(CreateAccountActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CreateAccountActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                        intent.putExtra(MapsActivity.USERNAME_KEY, name);
                                        startActivity(intent);
                                    }
                                }
                            });

                } else {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
