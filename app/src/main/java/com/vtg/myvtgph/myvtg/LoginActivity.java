package com.vtg.myvtgph.myvtg;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    //DEBUG LOGOUT
    private ImageView hiddenLogout;

    //LOADERS
    ProgressDialog pd;

    //STRINGS
    String email,pass;

    private FirebaseAuth firebaseAuth;

    //BOOLEAN
    private boolean signedin = false;
    private boolean doubleBackToExitPressedOnce = false;

    //FIELDS
    private EditText txtemail,txtpass;

    //Buttons
    private Button login;
    private TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        //FIELDS
        txtemail = (EditText) findViewById(R.id.txt_email);
        txtpass = (EditText) findViewById(R.id.txt_pass);
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        register = (TextView) findViewById(R.id.btn_register);
        register.setOnClickListener(this);


        //HIDDEN LOGOUT
        hiddenLogout = (ImageView) findViewById(R.id.logo1);
        hiddenLogout.setOnLongClickListener(this);

    }

    @Override
    protected void onStart() {
        if(firebaseAuth.getCurrentUser()!=null) {
            signedin = true;
            checkUser();
        }else
            signedin=false;
        super.onStart();

    }

    public void checkUser(){
        if(email!=null && pass!=null) {
            progress("Signing in.", "Checking your credentials...");
            pd.show();
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this,Dashboard.class));
                        }else{
                            alert( "Email verification!" ,"Please verify your email address!","Ok");
                            firebaseAuth.signOut();
                        }
                        pd.dismiss();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    tstr(e.getMessage(), 1);
                    pd.dismiss();
                }
            });
        }else{
            this.finish();
            startActivity(new Intent(this,Dashboard.class));
        }
    }
    public void checkEmpty(){
        if(txtemail.getText()!=null && txtpass.getText()!=null) {
            email = txtemail.getText().toString();
            pass = txtpass.getText().toString();
        }
        if(isEmailValid(email)){
            if(email.length()>5 && pass.length()>5) {
                checkUser();
            }else alert("Error","There's something wrong with your email and password!","Okay");
        }else alert("Empty Fields!","Please fill up all fields!","Okay");
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void alert(String title,String msg,String btnok){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(btnok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        if(firebaseAuth.getCurrentUser()!=null){
            signedin=true;
            tstr("Already signed in",0);
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                checkEmpty();
                break;
            case R.id.btn_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;

        }
    }
    public void progress(String title,String msg){
        pd = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
        pd.setTitle(title);
        pd.setMessage(msg);
        pd.setCancelable(false);
    }
    public void tstr(String msg,int lng){
        Toast.makeText(this,msg,lng).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        tstr("Press back again to exit!",0);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.logo1:
                firebaseAuth.signOut();
                tstr("Found hidden logout!",0);
                break;
        }
        return false;
    }
}
