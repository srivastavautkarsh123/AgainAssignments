package com.example.myapplicationloc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {



    static String battery_Percentage = null;
    static String battery_charging = null;
    String time_stamp = null;
    String locaTion = null;
    String internet_connection = null;
    int view = R.layout.activity_main;
    Button BatteryCharge;
    Button Location;
    Button ManualDataRefresh;
    Button Timestamp;
    LocationManager locationManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timestamp = findViewById(R.id.Timestamp);
        Location = findViewById(R.id.Location);
        BatteryCharge = findViewById(R.id.BatteryCharge);
        ManualDataRefresh = findViewById(R.id.ManualDataRefresh);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        databaseReference.setValue("internetconnection");
        databaseReference.setValue("Timestamp");
        databaseReference.setValue("BatteryCharge");
        databaseReference.setValue("Location");

        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("contacts");

        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        isCharging();
        BatteryCharge.setText("Battery Percentage is " + percentage + " %");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDateandTime = sdf.format(new Date());
        Timestamp.setText(currentDateandTime);
        time_stamp = currentDateandTime;

        Button btnstatus = (Button) findViewById(R.id.InternetConnectivityStatus);

        btnstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {
                    Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show();
                    internet_connection = "ON";
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                    internet_connection = "OFF";
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(".info/connected");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {
                            System.out.println("Connected");
                            Toast.makeText(MainActivity.this, "YES OK", Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println("Not Connected");
                            Toast.makeText(MainActivity.this, "NOT NO", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("Listener Was Cancelled");

                    }
                });

            }

            public boolean isConnected() {
                boolean connected = false;
                try {
                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                    connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
                    return connected;
                } catch (Exception e) {
                    Log.e("Connectivity Exception", e.getMessage());
                }
                return connected;
            }


        });


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);

        }

        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


    }




    @SuppressLint("MissingPermission")

    private  void getLocation(){
        try{
            locationManager  = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MainActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();



        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            String lat,lonG;
            lat= String.valueOf(location.getLatitude());
            lonG= String.valueOf(location.getLongitude());
            locaTion=lat+ "\n" + lonG;

            Location.setText(address);


        } catch (Exception e) {
            e.printStackTrace();
        }
        LocationHelper helper = new LocationHelper(
                location.getLongitude(),
                location.getLatitude()
        );

        FirebaseDatabase.getInstance().getReference("Current Location")
                .setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }


    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);


    }

   public static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);




   }
   public  void uploadData(View view){
       ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
       progressDialog.setMessage("Please wait");
       progressDialog.setCancelable(false);
       progressDialog.setTitle("Uploading...");
       progressDialog.show();

        DB_Handler db_handler=new DB_Handler(getApplicationContext(),"AssignmentData",null,1);
        db_handler.addData( battery_Percentage, locaTion, time_stamp, battery_charging, internet_connection);
       progressDialog.cancel();

   }

    public static boolean isCharging() {
        try
        {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = getApplicationUsingReflection().registerReceiver(null,ifilter);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            battery_Percentage =status + "%";
            boolean isCharging = status== BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                battery_charging="ON";
                return true;
            }
            else {
                battery_charging="OFF";
                return false;

            }
        } catch (Exception e) {
            return false;
        }


    }

}
