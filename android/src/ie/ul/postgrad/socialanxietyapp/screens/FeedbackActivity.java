package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;

import static ie.ul.postgrad.socialanxietyapp.Constants.APP_PATH;
import static ie.ul.postgrad.socialanxietyapp.Constants.FEEDBACK_SURVEY_LINK;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        try {
            SharedPreferences prefs = getSharedPreferences(APP_PATH, Context.MODE_PRIVATE);
            ((TextView) findViewById(R.id.app_code)).setText(prefs.getString("firstTimeUID", "").substring(0, 5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.survey_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FEEDBACK_SURVEY_LINK));
                startActivity(browserIntent);
            }
        });
        setupBars();
    }

    private void setupBars() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        App.setStatusBarColor(this);
    }
}
