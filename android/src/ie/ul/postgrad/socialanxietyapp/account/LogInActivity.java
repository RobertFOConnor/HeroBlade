package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ie.ul.postgrad.socialanxietyapp.FontManager;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.database.WebDBHelper;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.map.MapsActivity;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);


        findViewById(R.id.forgot_password_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);
    }

    private void attemptLogin() {
        email = ((EditText) findViewById(R.id.email_field)).getText().toString();
        password = ((EditText) findViewById(R.id.password_field)).getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            new LoginUserTask().execute(email, password);
        } else {
            Toast.makeText(LogInActivity.this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_password_button:
                Intent intent = new Intent(LogInActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.continue_button:
                attemptLogin();
                break;
        }
    }


    private class LoginUserTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            return PostData(params);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {


                try {
                    JSONObject obj = new JSONObject(result);
                    String id = obj.getString("id");
                    String name = obj.getString("name");
                    GameManager.getInstance().startGame(getApplicationContext(), id, name, email, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    GameManager.getInstance().startGame(getApplicationContext(), "getidfromwebservice", "GetNameFromWebService", email, password);
                }

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Login failed. Please make sure you are connected to the internet and that all your information is correct.", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }


    public String PostData(String[] values) {
        String response = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(WebDBHelper.URL + "LoginUser");
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("email", values[0]));
            list.add(new BasicNameValuePair("password", values[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            httpResponse.getEntity();
            response = WebDBHelper.readResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
