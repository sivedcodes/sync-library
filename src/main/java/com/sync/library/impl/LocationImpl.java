package com.sync.library.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.sync.library.callbacks.LocationCallback;
import com.sync.library.models.LocationInfo;

public class LocationImpl {

    public static void getLocation(Context context, LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError(new SecurityException("Location permission not granted"));
            return;
        }
        FusedLocationProviderClient fusedClient =
                LocationServices.getFusedLocationProviderClient(context);
        @SuppressLint("MissingPermission")
        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                callback.onResult(new LocationInfo(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getTime()
                ));
            } else {
                callback.onError(new Exception("Location is null"));
            }
        });
        task.addOnFailureListener(callback::onError);
    }
}
