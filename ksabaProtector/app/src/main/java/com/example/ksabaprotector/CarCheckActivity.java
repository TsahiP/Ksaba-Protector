package com.example.ksabaprotector;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.effect.Effect;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class CarCheckActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    CheckBox oilAndWater_check, fireExtinguisher_check, glamorVest_check, CheckStick_check;
    EditText Remarks_ET;
    TextView etDate;
    Button send_btn, btnDate;
    int mYear, mMonth, mDay;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    //    ===========================navigation============================
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //    Toolbar toolbar;
    TextView menu_name;

    //    =================================================================


    //    rington
    Ringtone ringtone;

    String car;
    //    SharedPreferences
    SharedPreferences sp;
    //    ===============
//    =======================sms=======================
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
//    =================================================

    //    ====================animation=================
    AnimationDrawable success_animation;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_check);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        oilAndWater_check = findViewById(R.id.water_oil_test_xml);
        fireExtinguisher_check = findViewById(R.id.Fire_extinguisher_xml);
        glamorVest_check = findViewById(R.id.Glamor_vest_xml);
        CheckStick_check = findViewById(R.id.CheckStick_xml);
        send_btn = findViewById(R.id.send_btn);
        btnDate = findViewById(R.id.date);
        etDate = findViewById(R.id.et_date_xml);
        send_btn.setOnClickListener(this);
        btnDate.setOnClickListener(this);
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
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            send_btn.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
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
                boolean checks_flag = false;
                //    SharedPreferences
                sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                onWindowFocusChanged(true);
                car = sp.getString("car", null).toString();
                String message = etDate.getText().toString() + "\n" + " הסייר/ת " + sp.getString("name", null) + " ביצע בדיתקת רכב לרכב סיור מספר " + " " + car + " " + "החוסרים שהיו קימים בבדיקה הם:";
                String oilWater = "\nחוסר במים או שמן\n", fire = "\nחוסר במטף\n", checker = "\nדוקרן גנים\n", glamorVest = "\nאפוד זוהר\n";
                if (!fireExtinguisher_check.isChecked()) {
                    message = message + fire;
                    checks_flag = true;
                }
                if (!oilAndWater_check.isChecked()) {
                    message = message + oilWater;
                    checks_flag = true;
                }
                if (!glamorVest_check.isChecked()) {
                    message = message + glamorVest;
                    checks_flag = true;
                }

                if (!CheckStick_check.isChecked()) {
                    message = message + checker;
                    checks_flag = true;
                }
//            message = "בוצעה בדיתקת רכב ל " +car;
                if (!checks_flag) {
                    message =etDate.getText().toString()+"\n"+ " הסייר/ת " + sp.getString("name", null) + " " + car + " " + " ביצע/ה את הבדיקות הנדרשות ונמצא תקין. ";
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
            }else{
                Toast.makeText(this,"please enter date",Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.tasks:
                startActivity(new Intent(this, TasksActivity.class));
                break;

            case R.id.logOut_xml:
                //Disconect from Firebase
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return false;
    }


    //    ==========================================rington=================================
    @RequiresApi
    public void ring() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(CarCheckActivity.this, alarmUri);
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
