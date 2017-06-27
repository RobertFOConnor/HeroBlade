package ie.ul.postgrad.socialanxietyapp.screens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ie.ul.postgrad.socialanxietyapp.R;

public class StepCounterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_counter);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
