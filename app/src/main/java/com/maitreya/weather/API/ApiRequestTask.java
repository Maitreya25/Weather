package com.maitreya.weather.API;

import android.icu.text.SimpleDateFormat;
import android.util.Log;
import com.maitreya.weather.API.ApiResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiRequestTask {

  public static void executeRequest(
      Double latitude, Double longitude, ApiRequestCallback callback) {

    Date currentDate = new Date();

    // Create a SimpleDateFormat to format the date and time
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:00");

    // Format the date and time as a string
    String formattedDateTime = dateFormat.format(currentDate);

    Executor executor = Executors.newSingleThreadExecutor();
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            try {
              String api =
                  "https://api.open-meteo.com/v1/forecast?latitude="
                      + latitude
                      + "&longitude="
                      + longitude
                      + "&hourly=temperature_2m";
              Log.e("hs", api);
              JSONObject json = new JSONObject(readUrl(api));
              ApiResult.elevation = json.getDouble("elevation");

              // Get the temperature array from JSON
              JSONObject hourlyObject = json.getJSONObject("hourly");

              JSONArray temperatureArray = hourlyObject.getJSONArray("temperature_2m");

              // Find the index of the current time in the "time" array
              JSONArray timeArray = hourlyObject.getJSONArray("time");
              int currentIndex = -1;
              for (int i = 0; i < timeArray.length(); i++) {
                if (timeArray.getString(i).equals(formattedDateTime)) {
                  currentIndex = i;
                  break;
                }
              }
              if (currentIndex != -1) {
                ApiResult.temp = temperatureArray.getDouble(currentIndex);
              } else {
                // nothing
              }

              callback.onApiRequestComplete();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }

  public static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuilder buffer = new StringBuilder();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1) {
        buffer.append(chars, 0, read);
      }
      return buffer.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  public interface ApiRequestCallback {
    void onApiRequestComplete();
  }
}
