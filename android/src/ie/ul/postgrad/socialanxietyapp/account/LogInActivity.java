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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ie.ul.postgrad.socialanxietyapp.FontManager;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.map.MapsActivity;

import static ie.ul.postgrad.socialanxietyapp.map.MapsActivity.USERNAME_KEY;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;

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
        String email = ((EditText) findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_field)).getText().toString();

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
            if (result.contains("success")) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(USERNAME_KEY, "");
                startActivity(intent);
                finish();
            }
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }


    public String PostData(String[] valuse) {
        String response = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://10.52.226.215:8080/AnxietyWebApp/servlet/LoginUser");
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("email", valuse[0]));
            list.add(new BasicNameValuePair("password", valuse[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            httpResponse.getEntity();
            response = readResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String readResponse(HttpResponse res) {
        InputStream is;
        String return_text = "";
        try {
            is = res.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return_text = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return return_text;
    }
}
