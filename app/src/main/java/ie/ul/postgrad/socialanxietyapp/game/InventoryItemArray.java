package ie.ul.postgrad.socialanxietyapp.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;

/**
 * Created by Robert on 07-Mar-17.
 */

public class InventoryItemArray extends SparseIntArray implements Parcelable {

    public static Parcelable.Creator<InventoryItemArray> CREATOR = new Parcelable.Creator<InventoryItemArray>() {
        @Override
        public InventoryItemArray createFromParcel(Parcel source) {
            InventoryItemArray read = new InventoryItemArray();
            int size = source.readInt();

            int[] keys = new int[size];
            int[] values = new int[size];

            source.readIntArray(keys);
            source.readIntArray(values);

            for (int i = 0; i < size; i++) {
                read.put(keys[i], values[i]);
            }

            return read;
        }

        @Override
        public InventoryItemArray[] newArray(int size) {
            return new InventoryItemArray[size];
        }
    };


    protected InventoryItemArray() {

    }


    public InventoryItemArray(InventoryItemArray inventoryItemArray) {
        for (int i = 0; i < inventoryItemArray.size(); i++) {
            this.put(inventoryItemArray.keyAt(i), inventoryItemArray.valueAt(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int[] keys = new int[size()];
        int[] values = new int[size()];

        for (int i = 0; i < size(); i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        dest.writeInt(size());
        dest.writeIntArray(keys);
        dest.writeIntArray(values);
    }
}
