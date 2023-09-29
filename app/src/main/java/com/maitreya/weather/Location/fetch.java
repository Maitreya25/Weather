package com.maitreya.weather.Location;

import android.content.Context;
import android.location.Location;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class fetch {

    public static void fetchLocation(Context context, final LocationCallback callback) {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            callback.onLocationFetched(latitude, longitude);
                        } else {
                            callback.onLocationFailed("Location is null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onLocationFailed(e.getMessage());
                    }
                });
    }

    public interface LocationCallback {
        void onLocationFetched(double latitude, double longitude);

        void onLocationFailed(String errorMessage);
    }
}
