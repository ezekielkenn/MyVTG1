package com.vtg.myvtgph.myvtg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback ,GoogleMap.OnMyLocationChangeListener{
    //FOR MAP
    private GoogleMap mMap;
    private Location myLoc;
    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    //HEADER
    private TextView myname,myemail;
    //end

    private String userid="";

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Back press is disabled",Toast.LENGTH_SHORT).show();
//        super.onBackPressed();
    }

    //FIREBASE
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                    new HomeFrag() ).commit();
            navigationView.setCheckedItem( R.id.nav_home );
        }
        View view = navigationView.getHeaderView(0);
        myname = view.findViewById( R.id.headName );
        myemail = view.findViewById( R.id.headEmail );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
            userid=firebaseAuth.getCurrentUser().getUid();
        }

        getHeaderData();

    }
    public void getHeaderData(){
        firebaseFirestore.collection( "Users" ).document( userid ).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }
                if(documentSnapshot!=null){
                    if(documentSnapshot.exists()){
                        String fullname;
                        String email;
                        fullname = documentSnapshot.getString( "user_firstname" ) + " " +
                                documentSnapshot.getString( "user_mi" ) + " " +
                                documentSnapshot.getString( "user_lastname" );
                        email = documentSnapshot.getString( "user_email" );
                        if(email!=null && fullname!=null){
                            myname.setText( fullname );
                            myemail.setText( email );
                        }

                    }
                }
            }
        } );
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                    new HomeFrag() ).commit();
                break;
            case R.id.nav_touristspot:
                getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new TouristSpotListFrag() ).commit();
                break;
            case R.id.nav_date:
                getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new VisitedFrag() ).commit();
                break;

        }
//
//        if (id == R.id.nav_home) {
//            getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
//                    new HomeFragment() ).commit();
//        }
//        else if (id == R.id.nav_touristspot) {
//            getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
//                    new ListFragment() ).commit();
//        }
//        else if (id == R.id.nav_date) {
//            getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
//                    new LogsFragment() ).commit();
//        }
//        else if (id == R.id.nav_logout) {
//            // Navigate back to Login ActivityLogout
//            Intent intentRegister = new Intent( NavActivity.this, LoginActivity.class );
//            startActivity( intentRegister );
//            finish();
//        }
//        {
//
////        } else if (id == R.id.nav_share) {
////
////        } else if (id == R.id.nav_send) {
////
////        }
//
            DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
            drawer.closeDrawer( GravityCompat.START );
        return false;
    }


    @Override
    public void onMyLocationChange(Location location) {
        this.myLoc=location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
