package ie.ul.postgrad.socialanxietyapp.screens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ie.ul.postgrad.socialanxietyapp.R;

public class TimeToWalkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_to_walk);

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
