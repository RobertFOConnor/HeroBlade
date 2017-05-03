package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * List adapter for displaying weapon items.
 */

public class WeaponListAdapter extends BaseAdapter {

    private String[] names;
    private Context context;
    private int[] damage;
    private int[] maxHealth;
    private int[] currHealth;
    private int[] imageId;
    private static LayoutInflater inflater = null;

    public WeaponListAdapter(Context context, ArrayList<WeaponItem> weapons) {

        int count = weapons.size();

        names = new String[count];
        damage = new int[count];
        maxHealth = new int[count];
        currHealth = new int[count];
        imageId = new int[count];

        for (int i = 0; i < count; i++) {
            names[i] = weapons.get(i).getName();
            damage[i] = weapons.get(i).getDamage();
            maxHealth[i] = weapons.get(i).getMaxHealth();
            currHealth[i] = weapons.get(i).getCurrHealth();
            imageId[i] = weapons.get(i).getImageID();
        }

        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        TextView tv2;
        TextView tv3;
        ProgressBar pb;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_weapon_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.item_title);
        holder.pb = (ProgressBar) rowView.findViewById(R.id.weapon_health_bar);
        holder.tv2 = (TextView) rowView.findViewById(R.id.damage_field);
        holder.tv3 = (TextView) rowView.findViewById(R.id.health_field);
        holder.img = (ImageView) rowView.findViewById(R.id.item_image);
        holder.tv.setText(names[position]);
        holder.pb.setMax(maxHealth[position]);
        holder.pb.setProgress(currHealth[position]);
        holder.tv2.setText("DMG: " + damage[position]);
        holder.tv3.setText("DU: " + currHealth[position] + "/" + maxHealth[position]);
        holder.img.setImageResource(imageId[position]);
        return rowView;
    }
}
