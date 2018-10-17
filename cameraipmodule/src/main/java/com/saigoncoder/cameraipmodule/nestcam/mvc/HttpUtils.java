package com.saigoncoder.cameraipmodule.nestcam.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
    private static final HttpUtils ourInstance = new HttpUtils();

    public static HttpUtils getInstance() {
        return ourInstance;
    }

    private HttpUtils() {
    }

    public static String getURL(String link){
        HttpURLConnection connection;
        BufferedReader reader;
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line).append("\n");
            }
            return buffer.toString();
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return null;
    }


}
