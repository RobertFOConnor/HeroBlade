package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * List adapter for displaying weapon items.
 */

public class WeaponListAdapter extends ArrayAdapter<WeaponItem> {

    private ArrayList<WeaponItem> weaponItems;
    private Context context;
    private String currWeaponUUID;

    public WeaponListAdapter(Context context, ArrayList<WeaponItem> weapons, String selectedWeaponUUID) {
        super(context, R.layout.fragment_weapon_item, weapons);
        weaponItems = weapons;
        this.currWeaponUUID = selectedWeaponUUID;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        WeaponItem weaponItem = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_weapon_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.healthBar = (ProgressBar) convertView.findViewById(R.id.weapon_health_bar);
            viewHolder.damageText = (TextView) convertView.findViewById(R.id.damage_field);
            viewHolder.durabilityText = (TextView) convertView.findViewById(R.id.health_field);
            viewHolder.UUIDText = (TextView) convertView.findViewById(R.id.uuid);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.bg = (LinearLayout) convertView.findViewById(R.id.item_bg);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameText.setText(weaponItem.getName());
        viewHolder.UUIDText.setText(weaponItem.getUUID());
        viewHolder.healthBar.setMax(weaponItem.getMaxHealth());
        viewHolder.healthBar.setProgress(weaponItem.getCurrHealth());
        viewHolder.damageText.setText("DMG: " + weaponItem.getDamage());
        viewHolder.durabilityText.setText("DU: " + weaponItem.getCurrHealth() + "/" + weaponItem.getMaxHealth());
        viewHolder.img.setImageResource(weaponItem.getImageID());

        if (weaponItem.getUUID().equals(currWeaponUUID)) {
            viewHolder.bg.setBackgroundColor(Color.BLUE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView nameText;
        TextView damageText;
        TextView durabilityText;
        TextView UUIDText;
        ProgressBar healthBar;
        ImageView img;
        LinearLayout bg;
    }
}
