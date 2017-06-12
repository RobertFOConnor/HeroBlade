package ie.ul.postgrad.socialanxietyapp.database;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Robert on 08-Jun-17.
 *
 * Helper for reading http response from post request.
 */

public class WebDBHelper {

    public static final String URL = "http://10.52.226.215:8080/AnxietyWebApp/servlet/";

    public static String readResponse(HttpResponse res) {
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
