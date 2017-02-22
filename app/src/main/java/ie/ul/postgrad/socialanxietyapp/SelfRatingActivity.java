package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.VerticalSeekBar;

public class SelfRatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_self_rating);

        final VerticalSeekBar verticalSeekBar = (VerticalSeekBar) findViewById(R.id.rating_bar);
        final TextView ratingDisplay = ((TextView) findViewById(R.id.rating_display));
        final TextView detailDisplay = ((TextView) findViewById(R.id.detail_display));

        verticalSeekBar.setMax(100);
        verticalSeekBar.setProgress(0);
        verticalSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int rating = ((VerticalSeekBar) v).getProgress();
                String s = "Rating: " + rating;
                ratingDisplay.setText(s);


                if (rating >= 80) {
                    detailDisplay.setText(getString(R.string.anxiety_scale_lvl_5));
                } else if (rating >= 60) {
                    detailDisplay.setText(getString(R.string.anxiety_scale_lvl_4));
                } else if (rating >= 40) {
                    detailDisplay.setText(getString(R.string.anxiety_scale_lvl_3));
                } else if (rating >= 20) {
                    detailDisplay.setText(getString(R.string.anxiety_scale_lvl_2));
                } else {
                    detailDisplay.setText(getString(R.string.anxiety_scale_lvl_1));
                }

                return false;
            }
        });

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
