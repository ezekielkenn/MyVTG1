package com.vtg.myvtgph.myvtg;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //PROGRESSDIALOG
    ProgressDialog pd;

    //GENDER VIEW
    private ImageView genM,genF;


    //FIREBASE
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

    //FIELDS
    private EditText email,pass,cpass,firstname,lastname,middleI,
                    address,phone;

    //Buttons
    private ImageView gotologin;
    private Button btndone;

    //STRINGS
//    private String gender = "";
    private String imgUrl="nopic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        gotologin = (ImageView) findViewById(R.id.btn_goback);
        gotologin.setOnClickListener(this);

        //GENDER VIEW
//        genM = (ImageView) findViewById(R.id.imgM);
//        genF = (ImageView) findViewById(R.id.imgF);
//        genF.setOnClickListener(this);
//        genM.setOnClickListener(this);

        //BTN DONE
        btndone = (Button) findViewById(R.id.btn_done);
        btndone.setOnClickListener(this);

        //FIELDS
        email = (EditText) findViewById(R.id.txt_regemail);
        pass = (EditText) findViewById(R.id.txt_regpass);
        cpass = (EditText) findViewById(R.id.txt_confirmpass);
        lastname = (EditText) findViewById(R.id.txt_lastname);
        firstname = (EditText) findViewById(R.id.txt_firstname);
        middleI = (EditText) findViewById(R.id.txt_mi);
        address = (EditText) findViewById(R.id.txt_address);
        //phone = (EditText) findViewById(R.id.txt_phone);

        //FIREBASE
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_goback:
                finish();
                break;
//            case R.id.imgF:
//                female();
//                break;
//            case R.id.imgM:
//                male();
//                break;
            case R.id.btn_done:
                checkEmpty();
                break;
        }
    }

//    public void male(){
//        gender = "m";
//        genM.setBackgroundColor(getResources().getColor(R.color.m_blue));
//        genF.setBackgroundColor(Color.TRANSPARENT);
//
//    }
//    public void female(){
//        gender = "f";
//        genF.setBackgroundColor(getResources().getColor(R.color.m_blue));
//        genM.setBackgroundColor(Color.TRANSPARENT);
//    }
    public void registerUser(){
        progress("Loading.","Saving & Creating your profile...");
        pd.show();
        String e = email.getText().toString(),
                p = pass.getText().toString(),
                fn = firstname.getText().toString(),
                ln = lastname.getText().toString(),
                mi = middleI.getText().toString(),
                ad = address.getText().toString();
//                ph = phone.getText().toString();
        Timestamp regdate = new Timestamp(new Date());
        GeoPoint mypoint = new GeoPoint(23,43);
        String usertype="tourist";
        final Map<String,Object> newdata = new HashMap<>();
        newdata.put("user_firstname",fn);
        newdata.put("user_lastname",ln);
        newdata.put("user_mi",mi);
        newdata.put("user_fullname",fn + " " + ln + " " + mi + ((mi!="")?".":""));
        newdata.put("user_email",e);
        newdata.put("user_password",p);
        newdata.put("user_address",ad);
        newdata.put("user_latlng",mypoint);
//        newdata.put("user_phone",ph);
        newdata.put("user_profilepic",imgUrl);
        newdata.put("user_regdatetime",regdate);
        newdata.put("user_usertype",usertype);
//        newdata.put("user_gender",gender);
        firebaseAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    progress("Loading.","Saving additional user's informations...");
                    String id=task.getResult().getUser().getUid();
                    newdata.put("user_id",id);
                    pd.show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                tstr( "Email verification sent!", 0);
                            }else{
                                tstr( "Registration failed!",1 );
                            }
                        }
                    } );
                    firebaseFirestore.collection("Users").document(id).set(newdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();
                                tstr("Registration was successful!",0);
                                firebaseAuth.signOut();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alert("Something went wrong!",e.getMessage(),"Close");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alert("Something went wrong!",e.getMessage(),"Close");
            }
        });

    }


    public void checkEmpty(){
        String e = email.getText().toString(),
                p = pass.getText().toString(),
                cp = cpass.getText().toString(),
                fn = firstname.getText().toString(),
                ln = lastname.getText().toString(),
                mi = middleI.getText().toString(),
                ad = address.getText().toString();
//                ph = phone.getText().toString();
        if(isEmailValid(e) && e.length()>5){
            if(p.length()>5 && cp.length()>5 && p.equals(cp)){
                if(fn.length()>=1 && ln.length()>=1 ){
                    if(ad.length()>5){
                        registerUser();
                        //if(ph.length()>=11 && ph.matches("^(09|\\+639)\\d{9}$")){
//                            if(gender.length()>=1) {
//                                registerUser();
//                            }else alert("Select a gender!","Please select a gender at the bottom of the form!","Okay");
                        //}else alert("Invalid phone format!","Please use a philippine phone number!","Okay");
                    }else alert("Empty Address!","Please select/enter a valid address!","Okay");
                }else alert("Invalid fullname!","You've entered a invalid length of fullname!","Okay");
            }else alert("Invalid length/mismatched !","Please make sure both password are the same!","Okay");
        }else alert("Registration Error","There's something wrong with your registration. Please check it again!","Okay");
    }

    @Override
    public void onBackPressed() {
        tstr("Back is disabled!",0);
    }


    public void tstr(String msg,int lng){
        Toast.makeText(this,msg,lng).show();
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
    public void progress(String title,String msg){
        pd = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
        pd.setTitle(title);
        pd.setMessage(msg);
        pd.setCancelable(false);
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
