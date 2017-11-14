package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import ie.ul.postgrad.socialanxietyapp.R;

public class StepsGraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_graph);

        // ArrayList<Integer> dailySteps = GameManager.getDbHelper().getDailySteps();

        // in this example, a LineChart is initialized from xml
        LineChart chart = (LineChart) findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();

        for (int j = 0; j < 10; j++) {

            // turn your data into Entry objects
            entries.add(new Entry((int) (Math.random() * 100), (int) (Math.random() * 100)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
