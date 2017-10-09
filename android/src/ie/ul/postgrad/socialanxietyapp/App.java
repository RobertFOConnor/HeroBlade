package ie.ul.postgrad.socialanxietyapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Robert on 14-Aug-17.
 *
 * Application object available throughout the app
 * for setting styles and showing toasts.
 */

public class App extends Application {
    private static App mInstance;
    private static Toast mToast;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public static void setStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.bg_color));
        }
    }

    public static void showToast(Context context, String s) {
        if (mToast != null) {
            mToast.cancel();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(s);

        mToast = new Toast(context);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(layout);
        mToast.show();
    }
}