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
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class TasksActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    CheckBox gordon_check, gellerLine_check, park_check, sapir_check,test_check;
    EditText Remarks_ET;
    TextView etDate;
    Button send_btn, btnDate;
    int mYear, mMonth, mDay;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;


    //    =======================sms=======================
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    //    =================================================

    //    SharedPreferences
    SharedPreferences sp;
//    ===============


    //    rington
    Ringtone ringtone ;


    //    ===========================navigation============================
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //    Toolbar toolbar;
    TextView menu_name;

    //    =================================================================
    //    ====================animation=================
    AnimationDrawable success_animation;
    ImageView imageView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//===================DatePicker================================
        btnDate = findViewById(R.id.date);
        btnDate.setOnClickListener(this);
        etDate = findViewById(R.id.et_date_xml);
        //===========================animation===============
        imageView = findViewById(R.id.anim_success_xml);
        imageView.setScaleX(0);
        imageView.setBackgroundResource(R.drawable.animation);
        success_animation = (AnimationDrawable) imageView.getBackground();
        //        menu ==========================================
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

//    =======================sms======================

        send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            send_btn.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
        gordon_check = findViewById(R.id.gordon_xml);
        sapir_check = findViewById(R.id.sapir_xml);
        gellerLine_check = findViewById(R.id.gelerLine_xml);
        park_check = findViewById(R.id.park_xml);
        test_check = findViewById(R.id.test_xml);
//    =================================================
    }

    @Override
    public void onClick(View v) {
        if (v == btnDate) {
            Calendar c = Calendar.getInstance().getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, new SetDate(), mYear, mMonth, mDay);
            dp.show();
        }

        if (v == send_btn) {
            if (!etDate.getText().equals("")) {
                //    SharedPreferences
                boolean checks_flag = false;
                sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                onWindowFocusChanged(true);
                String car = sp.getString("car", null).toString();
                String message = etDate.getText().toString() + "\n" + " הסייר/ת " + sp.getString("name", null) + " " + car + " " + "  לא ביצע את היזומות הבאות: ";
                String gordon_text = "\nיזומת גורדון\n", sapir_text = "\nיזומת ספיר\n", park_text = "\nיזומת פארק\n", gellerLine_text = "\nיזומת קו גלר\n";
                if (!gordon_check.isChecked()) {
                    message = message + gordon_text;
                    checks_flag = true;
                }
                if (!sapir_check.isChecked()) {
                    message = message + sapir_text;
                    checks_flag = true;
                }
                if (!park_check.isChecked()) {
                    message = message + park_text;
                    checks_flag = true;
                }

                if (!gellerLine_check.isChecked()) {
                    message = message + gellerLine_text;
                    checks_flag = true;
                }
                if (!test_check.isChecked()) {
                    message = message + "בדיקה בוצעה";
                    checks_flag = true;
                }
                if (!checks_flag) {
                    message =etDate.getText().toString()+ " הסייר/ת " + sp.getString("name", null) + " " + car + " " + " ביצע/ה את כל היזומות הנדרשות. ";
                }

                String number = "+972543045065";
                if (number.equals(null) || number.length() == 0 || message.equals(null) ||
                        message.length() == 0) {
                    return;
                }
                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    imageView.setScaleX(1);

                    success_animation.start();
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> messageList = smsManager.divideMessage(message.toString());
                    smsManager.sendMultipartTextMessage(number, null, messageList, null, null);
                    Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        imageView.setScaleX(0);
                    }
                }, 600);
                ring();
            } else {
                Toast.makeText(this, "please enter date", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                send_btn.setEnabled(true);
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
                break;
            case R.id.logOut_xml:
                //Disconect from Firebase
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return false;
    }

    @RequiresApi
    public  void ring()
    {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(TasksActivity.this, alarmUri);
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
}
