package com.example.ksabaprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    Button report_btn, signIn_btn;
    boolean btn_index_flag = false;
    boolean flag_car = false;
    TextView title, title2_email;


    //firebase
    FirebaseAuth mFirebaseAuth;
    private static String TAG = "HomeActivity";
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //    workersFireStore wfs;
    workers info;
    private workers wk = new workers();
//    ========================================


    //      listview
    ListView mListView;
    String text;
//    ============================


    //      to retrive data frome fire store
    private CollectionReference cr;
    workers workerData;
    ArrayList<workers> onlineList;
    onlineListAdapter adapter;
//    ===========================


    //      SharedPreferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    //          Custom piccker Dialog
    Button btn_showDialog;
    String car;
    //    =====================================


    //realtime fire base
    private DatabaseReference databaseReference;

    //    ===========================navigation============================
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //    Toolbar toolbar;
    TextView menu_name;
//    =================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        title = findViewById(R.id.title);
        info = new workers();
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
//        =========================


        //      SharedPreferences
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);

        //================intent===================
        Intent intent = getIntent();
        text = intent.getStringExtra("mail");
        title2_email = findViewById(R.id.email);
        title2_email.setText(text);
//        ===============================

        mListView = (ListView) findViewById(R.id.lv_xml);
        onlineList = new ArrayList<>();
        //          Custom piccker Dialog
        car = "0";
        btn_showDialog = findViewById(R.id.getIn_xml);
        btn_showDialog.setOnClickListener(this);
//    =====================================

        mFirebaseAuth = FirebaseAuth.getInstance();

        report_btn = findViewById(R.id.report_xml);
        report_btn.setOnClickListener(this);
        signIn_btn = findViewById(R.id.getIn_xml);
        signIn_btn.setOnClickListener(this);


        myInfo();


    }


    @Override
    public void onClick(View v) {

        if (v == report_btn) {
            if (signIn_btn.getText().toString().equals("יציאה ממשמרת")) {
                Intent intent = new Intent(this, ContactActivity.class);
                intent.putExtra("name", info.getName());
                intent.putExtra("car", info.getCar());
                startActivityForResult(intent, 2);
                finish();
            } else {
                Toast.makeText(this, "you have to chose a car first", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == signIn_btn) {


            if (btn_index_flag == true) {
                //get info frome fire store
                onlineList.clear();
                mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                String uid = user.getUid();
                databaseReference = firebaseDatabase.getReference();
                //init values to text views
                databaseReference.child("users").child(uid).child("inwork").setValue("false");
                databaseReference.child("users").child(uid).child("car").setValue("0");
                signIn_btn.setText("כניסה למשמרת");
                flag_car = false;
                btn_index_flag = false;
                if (!btn_index_flag) {
                    takeInfoFromDB();
                }

            } else {

                onlineList.clear();
                flag_car = true;
                btn_index_flag = true;

                mFirebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mFirebaseAuth.getCurrentUser();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final String uid = user.getUid();
//                databaseReference.child("users").child(uid).child("inwork").setValue("true");
                databaseReference = firebaseDatabase.getReference();
                //init values to text views


                //          Custom piccker Dialog ============= Custom piccker Dialog

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_car, null);
                Button btn_54, btn_53, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6;
                btn_1 = mView.findViewById(R.id.car_1_xml);
                btn_2 = mView.findViewById(R.id.motorcycle_2_xml);
                btn_3 = mView.findViewById(R.id.car_3_xml);
                btn_4 = mView.findViewById(R.id.motorcycle_4_xml);
                btn_5 = mView.findViewById(R.id.car_5_xml);
                btn_6 = mView.findViewById(R.id.motorcycle_6_xml);
                btn_54 = mView.findViewById(R.id.jeep_54_xml);
                btn_53 = mView.findViewById(R.id.jeep_53_xml);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                btn_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "1";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();

                    }
                });

                btn_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "2";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });

                btn_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "3";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });

                btn_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "4";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });

                btn_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "5";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });

                btn_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "6";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });

                btn_54.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        car = "54";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
//                                signIn_btn.setText("יציאה ממשמרת");
                                info.setCar(car);
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });

                btn_53.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        car = "53";
                        dialog.dismiss();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("car", car);
                        editor.apply();
                        boolean f = check_car_choice();
                        Log.d("test : ", f + "");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                takeInfoFromDB();
                                if (!check_car_choice()) {
//                                    signIn_btn.setText("יציאה ממשמרת");
                                    info.setCar(car);
                                }
                            }
                        }, 500);

                        dialog.dismiss();


                    }
                });


                dialog.show();
                //          ===================== =====end===== =====================

            }
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
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
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                break;
        }

        return false;
    }

    private void takeInfoFromDB() {
        /**
         * takeInfoFromDB()
         *  on call refresh ListView
         *
         */
        //get info
        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
//        query : serch inwork = true
        Query query = databaseReference.child("users").orderByChild("inwork").equalTo("true");

        onlineList.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                int index = 0;
//                workers[] wkk = new workers[15];
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // take relevant paramters from db
                    String name = ds.child("email").getValue().toString();
                    String inwork = ds.child("inwork").getValue().toString();
                    String email = ds.child("name").getValue().toString();
                    String car = ds.child("car").getValue().toString();
                    //set the data in place
                    wk = new workers(name, car, email, inwork);
//                         add item to arraylist
                    onlineList.add(wk);
                }
//                 create new adapter

                adapter = new onlineListAdapter(HomeActivity.this, R.layout.adapter_lv, onlineList);
//                set adapter to the ListView
                mListView.setAdapter(adapter);
//                Log.d("Test", onlineList.toString());        test
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void myInfo() {
        /**myInfo:
         * on create send request for user info
         */
        //get info frome fire store
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = user.getUid();
        databaseReference = firebaseDatabase.getReference();
        //init values to text views
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", uid);
        editor.apply();
        Query query = databaseReference.child("users").orderByKey().equalTo(uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // take relevant paramters from db
                    String name = ds.child("name").getValue().toString();
                    String inwork = ds.child("inwork").getValue().toString();
                    String email = ds.child("email").getValue().toString();
                    String car = ds.child("car").getValue().toString();
                    //set the data in place
                    info = new workers(name, car, email, inwork);
                    title.setText("ברוך הבא " + info.getName());
//                    set name in sp
                    editor.putString("name", name);
                    editor.apply();
                    if (inwork.equals("true")) {
                        btn_index_flag = true;

                        signIn_btn.setText("יציאה ממשמרת");
                    }
                    takeInfoFromDB();

                    Log.d("test1133", info.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //=================================================================================================
    public boolean check_car_choice() {
        /**
         * This method prevents an employee from entering an area where another employee is located
         */

        //get info frome fire store

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Query query = myRef.orderByChild("car").equalTo(car);
        final boolean[] flag_car = new boolean[1];

// Read from the database
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                boolean value = dataSnapshot.exists();

                if (value) {

                    Toast.makeText(HomeActivity.this, "this car token please select again", Toast.LENGTH_SHORT).show();

                    flag_car[0] = false;
                    btn_index_flag = false;
                    signIn_btn.setText("כניסה למשמרת");
                } else {
                    databaseReference.child("users").child(sp.getString("userId", null)).child("car").setValue(car);
                    databaseReference.child("users").child(sp.getString("userId", null)).child("inwork").setValue("true");

                    flag_car[0] = true;
                    btn_index_flag = true;
                    signIn_btn.setText("יציאה ממשמרת");


                }

//                Log.d(TAG, "ds is: " + dataSnapshot.getValue().toString());
//                Log.d(TAG, "Value is: " + value + "flagcar:" + flag_car + " email:" + text);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        Log.d(TAG, "flag_car is: " + flag_car[0]);

        return flag_car[0];


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onlineList.clear();
        myInfo();

    }
}

