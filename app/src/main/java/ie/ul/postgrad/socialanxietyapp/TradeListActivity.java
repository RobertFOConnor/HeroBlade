package ie.ul.postgrad.socialanxietyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.WorldItem;

public class TradeListActivity extends AppCompatActivity {

    public static String TRADE_LOCATION_ID = "trade_loc";
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_trade_list);


        Bundle b = getIntent().getExtras();
        ((TextView) findViewById(R.id.title_text)).setText(b.getString(TRADE_LOCATION_ID) + " Trading Post");

        itemList = (ListView) findViewById(R.id.item_list);

        String[] names = {"Mark: LVL 5", "Diarmuid: LVL 3", "Paddy: LVL 4"};
        String[] deals = {"needs 12 Wood for 1 Gold", "needs 4 Rope for 12 Cobblestone", "needs 10 Gold for 20 Coal"};

        TradeListAdapter adapter = new TradeListAdapter(this, names, deals);
        itemList.setAdapter(adapter);
    }
}
