package com.maitreya.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.maitreya.weather.API.ApiRequestTask;
import com.maitreya.weather.API.ApiResult;
import com.maitreya.weather.Location.fetch;

public class MainActivity extends AppCompatActivity {
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private TextView bala;
  private Context mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mContext = this;

    // Check for location permissions
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // Request location permissions from the user
      ActivityCompat.requestPermissions(
          this,
          new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
          LOCATION_PERMISSION_REQUEST_CODE);
    } else {
      // Permissions are granted, proceed with location retrieval
      fetchAndBegin(mContext);
    }
  }

  public void fetchAndBegin(Context mCon) {
    fetch.fetchLocation(
        this,
        new fetch.LocationCallback() {
          @Override
          public void onLocationFetched(double latitude, double longitude) {
            ApiRequestTask.executeRequest(
                latitude,
                longitude,
                new ApiRequestTask.ApiRequestCallback() {
                  @Override
                  public void onApiRequestComplete() {
                    updateView();
                  }
                });
          }

          @Override
          public void onLocationFailed(String errorMessage) {
            Toast.makeText(mCon, errorMessage, Toast.LENGTH_LONG).show();
          }
        });
  }

  public void updateView() {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            String info = "Elevation: " + Double.toString(ApiResult.elevation) + "m Temperature: " + Double.toString(ApiResult.temp);
            bala = findViewById(R.id.bala);
            bala.setText(info);
        }
    });
}

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permissions granted, fetch location
        fetchAndBegin(mContext);
      } else {
        Toast.makeText(mContext, "Location permissions denied.", Toast.LENGTH_LONG).show();
      }
    }
  }
}
