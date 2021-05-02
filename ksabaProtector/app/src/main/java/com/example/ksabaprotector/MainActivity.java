package com.example.ksabaprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public EditText id_ET,password_ET;
    Button btn_log;
    //process dialog
    ProgressDialog pd;

    //database auth
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //pd set
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);
        //setting firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        //set position to read data
        id_ET = findViewById(R.id.id_xml);
        password_ET = findViewById(R.id.pw_xml);
        //login btn
        btn_log = findViewById(R.id.loginBTN_xml);
        btn_log.setOnClickListener(this);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();//get current user if already login
                if(mFirebaseUser != null){
                    Toast.makeText(MainActivity.this,"You are logged in ",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(MainActivity.this,"please logged in ",Toast.LENGTH_SHORT).show();

                }
            }
        };

    }

    @Override
    public void onClick(View v) {
        if (v == btn_log) {

            mFirebaseAuth = FirebaseAuth.getInstance();
//            take the values from ET
            String id = id_ET.getText().toString();
            String pw = password_ET.getText().toString();
            boolean flag=true;
            if (id.isEmpty()) {
                Toast.makeText(this, "please enter id", Toast.LENGTH_SHORT).show();
                id_ET.setText("");
                password_ET.setText("");
                //set focus
                id_ET.requestFocus();
                flag = false;
            }
            if (pw.isEmpty()) {
                Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show();
                id_ET.setText("");
                password_ET.setText("");
                password_ET.requestFocus();
                flag = false;
            }
            if (password_ET.length() < 4) {
                Toast.makeText(this, "please enter password contain at least 4 chars", Toast.LENGTH_SHORT).show();
                id_ET.setText("");
                password_ET.setText("");
                password_ET.requestFocus();
                flag = false;

            }
            if (pw.isEmpty() && id.isEmpty()) {
                Toast.makeText(this, "id and password are empty....", Toast.LENGTH_SHORT).show();
                id_ET.setText("");
                id_ET.setError("יש להכניס אימייל");
                password_ET.setError("יש להכניס סיסמא");
                password_ET.setText("");
                password_ET.requestFocus();
                id_ET.requestFocus();
                flag = false;
            }

            if(flag){
                pd.show();
                mFirebaseAuth.signInWithEmailAndPassword(id,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( !task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Error,please try again", Toast.LENGTH_SHORT).show();
                            id_ET.setText("");
                            password_ET.setText("");
                            pd.dismiss();

                        }
                        else
                        {
//                            intent
                            String mail_in = id_ET.getText().toString();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            intent.putExtra("mail",mail_in);
                            startActivityForResult(intent, 2);


                            pd.dismiss();
//                            Intent i = new Intent(MainActivity.this,HomeActivity.class);
//                            startActivity(i);
                        }
                    }
                });
            }
//            end progress dialog

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}