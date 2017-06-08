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
import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.FontManager;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.map.MapsActivity;

import static ie.ul.postgrad.socialanxietyapp.map.MapsActivity.USERNAME_KEY;

public class CreateAccountActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = ((EditText) findViewById(R.id.name_field)).getText().toString();
                String email = ((EditText) findViewById(R.id.email_field)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_field)).getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);
                    new RegisterUserTask().execute(UUID.randomUUID().toString(), name, email, password);

                } else {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class RegisterUserTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            return PostData(params);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.contains("success")) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(USERNAME_KEY, name);
                startActivity(intent);
                finish();
            }
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(CreateAccountActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    public String PostData(String[] valuse) {
        String response = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://10.52.226.215:8080/AnxietyWebApp/servlet/RegisterUser");
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("id", valuse[0]));
            list.add(new BasicNameValuePair("name", valuse[1]));
            list.add(new BasicNameValuePair("email", valuse[2]));
            list.add(new BasicNameValuePair("password", valuse[3]));
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
