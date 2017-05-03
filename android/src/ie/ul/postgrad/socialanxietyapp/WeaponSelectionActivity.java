package ie.ul.postgrad.socialanxietyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;

public class WeaponSelectionActivity extends AppCompatActivity {

    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        itemList = (ListView) findViewById(R.id.item_list);
        WeaponListAdapter adapter = new WeaponListAdapter(this, GameManager.getInstance().getInventory().getWeapons());
        itemList.setAdapter(adapter);

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedItem = (int) itemList.getItemAtPosition(position);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", selectedItem);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
