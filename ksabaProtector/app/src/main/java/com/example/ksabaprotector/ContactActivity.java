package com.example.ksabaprotector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //    =======================sms=======================
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    //    =================================================

    //    ===========================navigation============================
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //    Toolbar toolbar;
    TextView menu_name, etDate;

    //    =================================================================


    //    rington
    Ringtone ringtone;
//    =========================================


    EditText reportET;
    Button send_btn, date_btn,watch_last;
    int mYear, mMonth, mDay; //date picker values
    TextView car_ET,name_ET;
    String name,car;
    boolean watch_flag = true ;
    //    ====================animation=================
    AnimationDrawable success_animation;
    ImageView imageView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        //    ===========================navigation============================
        //        איתחול המשתנים
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
//        toolbar = findViewById(R.id.toolbar);
        menu_name = findViewById(R.id.name_menu_xml);
//        toolbar setting
//        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
// mark item selected
        navigationView.bringToFront();


//set navigation listiner on items
        navigationView.setNavigationItemSelectedListener(this);
//        ==========================end navigation=================================


        //===========================animation===============
        imageView = findViewById(R.id.anim_success_xml);
        imageView.setScaleX(0);
        imageView.setBackgroundResource(R.drawable.animation);
        success_animation = (AnimationDrawable) imageView.getBackground();

//          set values
        watch_last = findViewById(R.id.watch_btn);
        date_btn = findViewById(R.id.date);
        send_btn = findViewById(R.id.send_btn);
        reportET = findViewById(R.id.carReport_Et_xml);
        etDate = findViewById(R.id.et_date_xml);
//        onClicks:
        date_btn.setOnClickListener(this);
        send_btn.setOnClickListener(this);
        watch_last.setOnClickListener(this);

//        intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        car = intent.getStringExtra("car");
        car_ET = findViewById(R.id.car_Tv_xml);
        name_ET = findViewById(R.id.name_Tv_xml);
        car_ET.setText( "רכב מספר: "+car);
        name_ET.setText("שם הסייר: "+name);


        if (checkPermission(Manifest.permission.SEND_SMS)) {
            send_btn.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public void onClick(View v) {
        if(v == watch_last)
        {
            if (watch_flag)
            {
                load();
                watch_flag=false;
                watch_last.setText("חזור");
                reportET.setEnabled(false);
                date_btn.setClickable(false);
                send_btn.setClickable(false);

            }else {
                watch_last.setText("צפה בדיווח האחרון");
                car_ET.setText( "רכב מספר: "+car);
                name_ET.setText("שם הסייר: "+name);
                reportET.setEnabled(true);
                date_btn.setClickable(true);
                send_btn.setClickable(true);
                reportET.setText("");
                etDate.setText("");
                watch_flag=true;

            }
        }

        if (v == date_btn) {

            Calendar c = Calendar.getInstance().getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, new ContactActivity.SetDate(), mYear, mMonth, mDay);
            dp.show();

        }
        if (v == send_btn) {
            if (!etDate.getText().equals("")&&!reportET.getText().toString().isEmpty())
            {

                if(checkPermission(Manifest.permission.SEND_SMS)){
                    imageView.setScaleX(1);
                    ring();
                    String number = "+972543045065";
                    String message =etDate.getText().toString()+"\n" +" הסייר/ת " +name+" "+car +" "+" שלח דיווח בנושא תקלת רכב \n "+ reportET.getText().toString();
                    success_animation.start();
                    SmsManager smsManager=SmsManager.getDefault();
                    ArrayList<String> messageList = smsManager.divideMessage(message.toString());
                    smsManager.sendMultipartTextMessage(number, null, messageList, null, null);
                    Toast.makeText(this,"Message Sent!",Toast.LENGTH_SHORT).show();
                    save();
                }else{
                    Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        imageView.setScaleX(0);
                    }
                }, 600);
            }else {
                if (etDate.getText().toString().isEmpty()) {
                    Toast.makeText(this, "please enter date", Toast.LENGTH_SHORT).show();
                }
                if (reportET.getText().toString().isEmpty())
                {
                    Toast.makeText(this, "please enter text", Toast.LENGTH_SHORT).show();

                }

            }
        }
    }

    private class SetDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);


        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));

                break;
            case R.id.nav_check_car:
                startActivity(new Intent(this, CarCheckActivity.class));

                break;
            case R.id.tasks:
                startActivity(new Intent(this, TasksActivity.class));
                break;
            case R.id.logOut_xml:
                //Disconect from Firebase
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ContactActivity.this, MainActivity.class));
                break;

        }

        return false;
    }


    //    storage======================================
    public void save() {
        final String FILE_NAME = name;
        String report = reportET.getText().toString() + "\n";
        String date = etDate.getText().toString();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(name.getBytes());
            fos.write("\n".getBytes());
            fos.write(car.getBytes());
            fos.write("\n".getBytes());
            fos.write(report.getBytes());
//            fos.write("\n".getBytes());
            fos.write(date.getBytes());
            reportET.getText().clear();

            Toast.makeText(this, "Saved",
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
//======================lifeCycle============================
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    //======================lifeCycle-end============================


    @RequiresApi
    public void ring() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                ringtone.stop();
            }
        }, 1000);
    }

    public void load() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            name_ET.setText("שם הסייר: "+br.readLine());
            car_ET.setText("רכב מספר: "+br.readLine());
            reportET.setText(br.readLine());
            etDate.setText(br.readLine());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}