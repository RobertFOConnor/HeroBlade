package ie.ul.postgrad.socialanxietyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TradeListActivity extends AppCompatActivity {

    public static String TRADE_LOCATION_ID = "trade_loc";
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trade_list);


        Bundle b = getIntent().getExtras();
        ((TextView) findViewById(R.id.title_text)).setText(b.getString(TRADE_LOCATION_ID) + " Trading Post");

        itemList = (ListView) findViewById(R.id.item_list);

        //Example trade deals
        String[] names = {"Mark: LVL 5", "Diarmuid: LVL 3", "Paddy: LVL 4"};
        String[] deals = {"needs 12 Wood for 1 Gold", "needs 4 Rope for 12 Cobblestone", "needs 10 Gold for 20 Coal"};

        itemList.setAdapter(new ArrayAdapter<>(this,
                R.layout.fragment_trade_item, R.id.trader_title, names));
    }
}
