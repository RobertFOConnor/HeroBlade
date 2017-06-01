package ie.ul.postgrad.socialanxietyapp.account;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.FontManager;
import ie.ul.postgrad.socialanxietyapp.R;

public class CreateAccountActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        //Get Firebase auth instance

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText) findViewById(R.id.name_field)).getText().toString();
                String email = ((EditText) findViewById(R.id.email_field)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_field)).getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                } else {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
