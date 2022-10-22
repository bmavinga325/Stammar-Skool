package com.example.stammaskool;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.example.stammaskool.scenariosActivities.ScenariosHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    CardView read_card,LayoutCalender;
      Button buttonLogout;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initAuthentication();
         findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               logout();
             }
         });
        ImageView img_profile = findViewById(R.id.img_profile);
        read_card = findViewById(R.id.read_card);
        LayoutCalender=findViewById(R.id.LayoutCalender);
        LayoutCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalender();
            }
        });

          findViewById(R.id.daf).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  startActivity(new Intent(HomeActivity.this,DAFActivity.class));
              }
          });

        findViewById(R.id.img_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
            }
        });

        findViewById(R.id.Scenarios).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ScenariosHome.class));
            }
        });


        read_card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, SpeechToText.class));

               /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                     if(checkPermission()){

                     }else {
                         //somethign
                     }
                }else {
                    startActivity(new Intent(HomeActivity.this, SpeechToText.class));
                }*/


               // startActivity(new Intent(HomeActivity.this,SpeechToText.class));
            }
        });
    }

    private void openCalender() {


        // get calender instance
        final Calendar newCalendar = Calendar.getInstance();

        // create date picker dialog
        final DatePickerDialog endDate = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int newMonth=monthOfYear+1;
                String date="";
                if(dayOfMonth<10){
                    date=date+"0"+dayOfMonth;
                }else {
                    date=date+dayOfMonth;
                }
                if(newMonth<10){
                    date=date+"-0"+newMonth;
                }else {
                    date=date+"-"+newMonth;
                }
                date=date+"-"+year;
                Intent intent=new Intent(HomeActivity.this,StatisticsActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDate.show();  // show date picker dialog


    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean checkPermission(){
        if(!Environment.isExternalStorageManager()){
            try {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                startActivity(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
            return false;
        }else {
            return true;
        }

    }
    private void logout() {
        new AlertDialog.Builder(this)  // crate dialog
                .setMessage("Are you sure to logout")// show message on dialog

                // create button no on dialog
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // simple close dialog
                    }
                })

                // create button yes on Dialog
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // check if the user login
                        if(firebaseUser!=null){
                            firebaseAuth.signOut(); // logout user
                            finish(); // close this activity


                            // start login activity
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        }

                    }
                })
                .show();

    }

    private void initAuthentication() {
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

    }
}