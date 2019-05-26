package com.vtg.myvtgph.myvtg;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.Image;
import android.media.Rating;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFrag extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener,GoogleMap.OnMarkerClickListener{
    private GoogleMap mMap;
    private Location myLoc;
    //MARKERS AND LATLNG's
    private ArrayList<LatLng> touristLatLng = new ArrayList<LatLng>();
    private ArrayList<Marker> touristMarkers = new ArrayList<Marker>();
    private ArrayList<String> touristPlaces = new ArrayList<String>();
    private ArrayAdapter<String> myadapter;
    //FOR PROGRESSDIALOG
    private ProgressDialog pd;

    //SETTINGS DIALOG

    //FOR MARKER CLICK DETAILS RATE TOTAL;
    private int totalrate=0;


    //SELECTED MAP TYPE
    private String selectedMapType="";
    int [] mapTypes = {mMap.MAP_TYPE_NORMAL,mMap.MAP_TYPE_HYBRID,mMap.MAP_TYPE_SATELLITE,mMap.MAP_TYPE_TERRAIN};

    //FOR TRAVEL FIRESTORE
    private Map<String,Object> travelData=new HashMap<>();

    //CURRENT TRAVEL ID FIRESTORE
    private String travelId="";

    //FOR SEARCH
    private ArrayList<TSpot> Tspots = new ArrayList<TSpot>();
    private SearchAdapter adapter ;
    //FOR DISTANCE TO LAST POINT
    private double distancetodest;
    //LAST LATLNG FROM ROUTE
    private Location routeLastPoint=new Location("");

    //FOR GOOGLE MAP DIRECTIONS
    Polyline currentPoly;

    //FOR DIRECTION API
    private URL target;
    private HttpURLConnection hcon;
    private InputStream is;
    private BufferedReader br;

    //CANCEL TRAVEL BTN
    private CardView btnCancelTravel;

//    distance
    private Button distance;


    //FOR DETAILS FORM
    private EditText txtDetails;
    private Button speak;

    //FOR AUTOCOMPLETE TO FIND THE SELECTED PLACE
    int tmppos = -1;

    //TEXTTOSPEECH
    TextToSpeech tts;
    //FIREBASE
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    int PERMISSION_ALL = 1;
    //FOR SEARCH TOURIST
    AutoCompleteTextView touristSpots;

    //FOR TRAVEL POINTS STORAGE
    ArrayList<LatLng> travelArray = new ArrayList<LatLng>();
    ArrayList<LatLng> travelEndPoints = new ArrayList<LatLng>();

    //GOTO MY LOC
    private CardView gotomyloc;
    private CardView mapSetting;


    //FOR FOLLOW LOCATION SAVE IN SHAREDPREF
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    //VIEW INFLATE
    View view;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean autonav = false, firstlaunch = true;
    private String typeoftravel="";
    private boolean startDistanceCheck=false;
    private boolean navtomyloc=false;

    //FOR USER DETAILS VARIABLES
    private String fn,ln,mi,fullname,userPic,userId,email;

    //THIS IS TO SIMULATE THE TRAVEL
    private boolean autoTravel = false;

    //NEAREST RESTAURANT BUTTON
    private CardView nearestRestaurant;
    private String defaultradius = "350";

    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private RestaurantAdapter restaurantAdapter;

    private boolean drawCircleOnce=true;
    private Circle mapCircle=null;
    private CircleOptions co = new CircleOptions();



    public void getNearestRestaurant(LatLng myloc){

        try {
            target = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+parcelatlng(myloc)+"&radius="+defaultradius+"&type=restaurant&key="+getString(R.string.placeapi));
            Log.d("placeapi",target.toString());
            hcon = (HttpURLConnection) target.openConnection();
            is = hcon.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            String tmp="",res="";
            while((tmp=br.readLine())!=null){
                res = res + tmp;
            }
            hcon.disconnect();
            is.close();
            br.close();
            JSONObject jsn = new JSONObject(res);
            String status =  jsn.getString("status");
            if(status.toLowerCase().equals("ok")){
                tstr("List of all nearest restaurants");
                showRestaurants(jsn);
                pd.dismiss();
            }else if(status.toLowerCase().equals("zero_results")){
                tstr("No nearby restaurant : " + defaultradius);
                pd.dismiss();
            }else{
                tstr("Nearest restaurant empty data(s)!");
                pd.dismiss();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void showRestaurants(JSONObject res){

        restaurantAdapter = new RestaurantAdapter(getContext(),restaurants);
        try {

            Log.d("placeapi", String.valueOf(res.getJSONArray("results").getJSONObject(0)));
            JSONArray json = res.getJSONArray("results");
            Log.d("ganani",json.length()+"");

            String restaurantName = ""
                    ,icon=""
                    ,restaurantAddress="";
            boolean open=false;
            double rating=0.0;
            GeoPoint restaurantLocation=null;
            restaurants.clear();
            for (int i = 0;i<json.length();i++){
                JSONObject jsonObject = json.getJSONObject( i );
                Log.d( "testapi",jsonObject+"" );
//                restaurantName  = json.getJSONObject( i ).getString("name");
//                open=json.getJSONObject( i ).getJSONObject("opening_hours").getBoolean("open_now");
//                icon = json.getJSONObject( i ).getString("icon");
//                rating = json.getJSONObject( i ).getDouble("rating");
//                restaurantAddress = json.getJSONObject( i ).getString("vicinity");
//                double lat=json.getJSONObject( i ).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
//                        ,lng=json.getJSONObject( i ).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
//                restaurantLocation = new GeoPoint(lat,lng);
//                Log.d("currentrestaurant",json.getJSONObject( i )+"");
//                Log.d("placeapicurrentitem",restaurantName + " " + open + " " +icon +" " +rating + " " + restaurantAddress + " ");
//                //String restaurantName, String restaurantAddress, String restaurantIcon, boolean open, float rating
//                restaurants.add(new Restaurant(restaurantName,restaurantAddress,icon,open,(float)rating,restaurantLocation));
                String openString;
                if (jsonObject.has("opening_hours")) {
                    openString= jsonObject.getJSONObject("opening_hours").getBoolean("open_now")+"";
                }else{
                    openString="null";
                }
                if(jsonObject.has( "rating" )){

                }else{

                }

                if(jsonObject!=null) {
                    double lat=json.getJSONObject( i ).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                        ,lng=json.getJSONObject( i ).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                restaurantLocation = new GeoPoint(lat,lng);
                    restaurants.add( new Restaurant( jsonObject.getString( "name" )
                            , jsonObject.getString("vicinity") ,
                            jsonObject.getString("icon"),
                            openString
                            , (jsonObject.has( "rating" ))?(float)jsonObject.getDouble("rating"):-1.0f,
                            restaurantLocation) );
                }
            }
            restaurantAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.restaurant_list,null);
        ListView lv = view.findViewById(R.id.restaurant_list);
        lv.setAdapter(restaurantAdapter);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(view)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        final AlertDialog dialog = alert.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GeoPoint point = restaurants.get(position).getLocation();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.getLatitude(), point.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerWithPic(getContext(), restaurants.get(position).getRestaurantIcon(), "Ezekiel")))
                        .title(restaurants.get(position).getRestaurantName()));
                dialog.dismiss();
            }
        });
        pd.dismiss();


    }
    @Nullable
    @Override

    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate(R.layout.home, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        distance = view.findViewById( R.id.distance);


        //CHECK SHAREDPREF
         sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
         editor=sharedPref.edit();

         //RESTAURANT BUTTON
        nearestRestaurant = view.findViewById(R.id.nearestRestaurant);
        nearestRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLoc!=null) {
                    pd = new ProgressDialog(getContext(),ProgressDialog.STYLE_SPINNER);
                    pd.setTitle("Locating nearest restaurants");
                    pd.setMessage("Please wait while we are retrieving nearest restaurants details.");
                    pd.setCancelable(false);
                    pd.show();
                    getNearestRestaurant(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()));
                }else tstr("Please make sure you turned on your location!");

            }
        });

        //GET USER DETAILS TO BE INSERTED LATER;
        pd=new ProgressDialog(getContext(),ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Retrieving user information.");
        pd.setMessage("Retrieving your personal details...");
        pd.show();
        if(firebaseAuth.getCurrentUser()!=null) {
            userId = firebaseAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if(snapshot!=null){
                        if(snapshot.exists()){
                            fn=snapshot.getString("user_firstname");
                            ln=snapshot.getString("user_lastname");
                            mi=snapshot.getString("user_mi");
                            fullname=snapshot.getString("user_fullname");
                            userPic=snapshot.getString("user_profilepic");
                            email=snapshot.getString("user_email");
                            pd.dismiss();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    tstr(e.getMessage());
                    pd.dismiss();
                }
            });
        }
        else getActivity().finish();

        //CANCEL TRAVEL

        btnCancelTravel = view.findViewById(R.id.travelCancel);
        btnCancelTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure you'd like to remove the route?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                firebaseFirestore.collection("Travels").document(travelId).update("travel_status","cancelled").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            clearSomeFields();
                                            if(sharedPref.getString(getString(R.string.followloc),"yes").equals("yes"))
                                                navtomyloc=true;
                                            else
                                                navtomyloc=false;

                                        }else {
                                            tstr("Cancel Failed!");
                                            clearSomeFields();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        tstr(e.getMessage());
                                        clearSomeFields();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gotomyloc = view.findViewById(R.id.gotomyloc);
        mapSetting = view.findViewById(R.id.mapSettings);


        //Map settings/Map type
        //Follow location
        mapSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog settings = new Dialog(getActivity());
                View vv = inflater.inflate(R.layout.map_settings,null);
//                settings.setContentView( vv );
//                settings.show();
                String [] mapTypesNames = {"NORMAL","HYBRID","SATELLITE","TERRAIN"};
                ArrayAdapter spinadapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,mapTypesNames);
                final Spinner spin = vv.findViewById(R.id.mapType);
                final Button settingSave = vv.findViewById(R.id.settingSave);
                spin.setAdapter(spinadapter);
                final Switch followMyLoc = vv.findViewById(R.id.followMyLoc);
                followMyLoc.setChecked(autonav);

                final int[] defselected = {Integer.parseInt(sharedPref.getString(getString(R.string.maptype), "0"))};
                spin.setSelection(defselected[0]);
                spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedMapType = spin.getSelectedItem().toString();
                        defselected[0] =position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                settingSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(followMyLoc.isChecked()){
                            editor.putString(getString(R.string.followloc),"yes");
                            autonav=true;
                        }else{
                            editor.putString(getString(R.string.followloc),"no");
                            autonav=false;
                        }
                        editor.commit();

                        mMap.setMapType(mapTypes[defselected[0]]);
                        editor.putString(getString(R.string.maptype),String.valueOf(defselected[0]));
                        editor.commit();
                        settings.dismiss();
                    }
                });
                settings.setContentView(vv);
                settings.show();

            }



        });
        gotomyloc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //THIS IS FOR DELAY TO SIMULATE A CAR TRAVEL
                if(autoTravel) {
                    autoTravel = false;
                    tstr("Hidden auto travel disabled");
                }
                else {
                    autoTravel = true;
                    tstr("Hidden auto travel enabled");
                    //travelroute(new LatLng(myLoc.getLatitude(),myLoc.getLongitude()),touristLatLng.get(tmppos));

                }

                return false;
            }
        });
        gotomyloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navtomyloc=true;
                navtomyLocation();
            }
        });

        //FOR SEARCH TOURIST SPOT
        touristSpots = view.findViewById(R.id.txtSearchTS);
        adapter = new SearchAdapter(getContext(),Tspots);
        getallTS();
        myadapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,touristPlaces);
        touristSpots.setAdapter(myadapter);
        touristSpots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                preparefortravel();
            }
        });

        return view;
    }

    //FOR TRAVEL TYPE
    public void preparefortravel(){
        final AlertDialog.Builder d  =new AlertDialog.Builder(getContext());
//                tmppos = position;
        int counter = -1;
        for(String place : touristPlaces){
            counter++;
            if(place.equals(touristSpots.getText().toString())){
                tmppos=counter;
                break;
            }
        }
        d.setPositiveButton("Travel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tmppos!=-1 && typeoftravel!="") {
                    drawDirection();
                    touristSpots.setEnabled(false);
                    btnCancelTravel.setVisibility(View.VISIBLE);


                    autonav = true;
                    mMap.setTrafficEnabled(false);
                    startDistanceCheck = true;
                    pd = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
                    pd.setCancelable(false);
                    pd.setTitle("Creating Informations!");
                    pd.setMessage("Creating your travel information please wait...");
                    pd.show();
                    addToTravelDB();
                }else{
                    tstr("Please try again or Select a type of travel");
                    touristSpots.setText("");
                }
            }
        });
        View v = LayoutInflater.from(getContext()).inflate(R.layout.typeof_travel,null);
        final ImageView hike = v.findViewById(R.id.hike),
                drive = v.findViewById(R.id.car),
                bike = v.findViewById(R.id.bike);
        if(typeoftravel=="driving"){
            hike.setBackgroundColor(Color.TRANSPARENT);
            bike.setBackgroundColor(Color.TRANSPARENT);
            drive.setBackgroundColor(getResources().getColor(R.color.m_blue));
        }else if(typeoftravel=="walking"){
            hike.setBackgroundColor(getResources().getColor(R.color.m_blue));
            bike.setBackgroundColor(Color.TRANSPARENT);
            drive.setBackgroundColor(Color.TRANSPARENT);
        }else if(typeoftravel=="cycling"){
            hike.setBackgroundColor(Color.TRANSPARENT);
            bike.setBackgroundColor(getResources().getColor(R.color.m_blue));
            drive.setBackgroundColor(Color.TRANSPARENT);
        }
        hike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeoftravel = "walking";
                hike.setBackgroundColor(getResources().getColor(R.color.m_blue));
                bike.setBackgroundColor(Color.TRANSPARENT);
                drive.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeoftravel = "cycling";
                hike.setBackgroundColor(Color.TRANSPARENT);
                bike.setBackgroundColor(getResources().getColor(R.color.m_blue));
                drive.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeoftravel="driving";
                hike.setBackgroundColor(Color.TRANSPARENT);
                bike.setBackgroundColor(Color.TRANSPARENT);
                drive.setBackgroundColor(getResources().getColor(R.color.m_blue));
            }
        });
        d.setView(v);
        d.show();
    }

    //AFTER CANCEL AND COMPLETED TRAVEL - this is to clear all fields and the map;
    public void clearSomeFields(){
        btnCancelTravel.setVisibility(View.GONE);
        touristSpots.setEnabled(true);
        touristSpots.setText("");
        cleanMap();
        travelId="";
        tmppos=-1;
    }
    //END


    @Override
    public void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
//            tts.shutdown();
        }

    }


    //ADDING DATA ON TRAVEL COLLECTION IN FIRESTORE
    public void addToTravelDB(){
        if(myLoc!=null) {
            ArrayList<GeoPoint> convertedPoints = new ArrayList<GeoPoint>();
            if(travelArray.size()>0) {
                for (int i = 0; i < travelArray.size();i++){
                    convertedPoints.add(new GeoPoint(travelArray.get(i).latitude,travelArray.get(i).longitude));
                }
            }else{
                //tstr("Routes points not found!");
            }
            travelData.put("travel_map_type",selectedMapType);
            travelData.put("travel_place_history",Tspots.get(tmppos).getPlace_desc());
            travelData.put("travel_place_distance",distancetodest);
            travelData.put("travel_place_id",Tspots.get( tmppos ).getId());
            travelData.put("travel_place_img",Tspots.get(tmppos).getPlace_img());
            travelData.put("travel_place_location",Tspots.get(tmppos).getPlaceLoc());
            travelData.put("travel_place_name",Tspots.get(tmppos).getPlace_name());
            travelData.put("travel_route_points",convertedPoints);
            travelData.put("travel_started",new Timestamp(new Date()));
            travelData.put("travel_status","ongoing");
            travelData.put("travel_type",typeoftravel);
            travelData.put("travel_user_email","test@gmail.com");
            travelData.put("travel_user_fn",fn);
            travelData.put("travel_user_id",userId);
            travelData.put("travel_user_ln",ln);
            travelData.put("travel_user_location",new GeoPoint(myLoc.getLatitude(),myLoc.getLongitude()));
            travelData.put("travel_user_mi",mi);
            travelData.put("travel_user_name",fullname);
            travelData.put("travel_user_pic",userPic);
            travelData.put("travel_user_rate",0);
//            travelData.put("travel_completed",new Timestamp(new Date()));
            travelData.put("travel_id","travelid");
            if(travelData.size()>0) {

                firebaseFirestore.collection("Travels").add(travelData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {

                            pd.dismiss();
                            tstr("Ready to travel.");
                            final String addid = task.getResult().getId();
                            firebaseFirestore.collection("Travels").document(addid).update("travel_id", addid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        travelId=addid;
                                    }
                                }
                            });
//                            createTravelHistory(addid);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tstr(e.getMessage());
                        pd.dismiss();
                    }
                });
            }else {
                tstr("Missing some destination data!");
                pd.dismiss();
            }
        }
    }


    public void getallTS(){
        firebaseFirestore.collection("TouristSpots").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots!=null){
                    Tspots.clear();
                    touristPlaces.clear();
                    for (QueryDocumentSnapshot q :queryDocumentSnapshots){
                        if(q!=null){
                            if(q.exists()){
                                Tspots.add(new TSpot(
                                        q.getId(),
                                        q.getString("place_name"),
                                        q.getString("place_history"),
                                        q.getString("place_image"),
                                        5,
                                        q.getGeoPoint("place_latlng")
                                ));
                                touristPlaces.add(q.getString("place_name"));
                            }
                        }
                        if(q!=null){
                        }
                    }
                    adapter.notifyDataSetChanged();
                    myadapter.notifyDataSetChanged();
                }
            }
        });
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

    // GOOGLE MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setTrafficEnabled(true);
//        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(mapTypes[Integer.parseInt(sharedPref.getString(getString(R.string.maptype),"0"))]);
        loadTouristSpots();


    }

    //FOLLOW CURRENT LOCATION
    public void navtomyLocation(){
        if(myLoc!=null) {
            if (autonav || firstlaunch || navtomyloc) {
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()))
                        .zoom(17.8f)
                        .tilt(70)
                        .bearing(0)
                        .build();
                CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(camUpdate);
                firstlaunch = false;
                navtomyloc=false;

            }
        }else tstr("Current location not found!");
    }

    //CURRENT LOCATION WITH RADIUS
    @Override
    public void onMyLocationChange(Location location) {
        if(!autoTravel) {
            this.myLoc = location;
            mylocationChange(location);
        }
        if(drawCircleOnce){
                co.center(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()))
                        .radius(Integer.parseInt(defaultradius))
                        .strokeWidth(3f)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.argb(50,30,144,255));
                 mapCircle = mMap.addCircle(co);
                drawCircleOnce=false;
        }
        else {
            drawCircleOnce=true;
            mapCircle.remove();
        }

    }

    //WHEN USER ARRIVED
    public void mylocationChange(Location location){
        if (location != null) {
            //CHECK IF AUTONAV IS ENABLED FROM SHAREDPREF
            if(tmppos >0 ){
                distance.setVisibility( View.VISIBLE );
                if (Tspots.size() > 0) {
                    GeoPoint g = Tspots.get(tmppos).getPlaceLoc();
                    Location temp = new Location( "" );
                    temp.setLatitude( g.getLatitude() );
                    temp.setLongitude( g.getLongitude() );
                    distance.setText( location.distanceTo( temp )+"M" );
                }
            }else {
                distance.setVisibility( View.GONE );
            }

            String yesnofollow = sharedPref.getString(getString(R.string.followloc),"yes");
            if(yesnofollow.equals("yes")) autonav=true;
            else autonav=false;
            //END
            navtomyLocation();
        }
        if(startDistanceCheck) {
            distancetodest = location.distanceTo(routeLastPoint);
            Log.d("distancetotal", distancetodest + " DISTANCE TO MY LOC AND LAST POINT");
            if (distancetodest < 45) {
                tstr("Travel completed!");
                if(tts!=null){
                    tts.stop();
                    tts=null;
                }
                speak();
                final Dialog placedetails = new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                placedetails.setCancelable(false);
                this.view = LayoutInflater.from(getContext()).inflate(R.layout.place_details,null);
                txtDetails = view.findViewById(R.id.txt_hist);
                final CircleImageView pic = view.findViewById(R.id.placeImg);
                final RatingBar rate = view.findViewById(R.id.placeRate);
                rate.setIsIndicator(false);
                final ImageView closeVisible = view.findViewById(R.id.btnCloseDetails);
                final ImageView cover = view.findViewById(R.id.placecover);
                final TextView openhrs = view.findViewById( R.id.ts_openhrs ),
                        closed = view.findViewById( R.id.ts_closed );
                final LinearLayout details = view.findViewById( R.id.details );
                closeVisible.setVisibility(View.GONE);
                rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Save rating?")
                                .setMessage("Would you like to save rating ["+(int)rate.getRating()+"]. ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveRating((int)rate.getRating(),dialog);
                                        if (tts != null) {
                                            tts.stop();
                                        }

                                        placedetails.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }
                });
                speak = view.findViewById(R.id.btnSpeak);
                speak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(speak.getText().toString().toLowerCase().equals("speak")) {
                            tstr("Speech Started!");
                            speak(txtDetails.getText().toString());
                            speak.setText("Stop");
                            speak.setTextColor(getResources().getColor(R.color.md_red));

                        }else{
                            speak.setText("Speak");
                            speak.setTextColor(getResources().getColor(R.color.m_blue));
                            tts.stop();
                        }
                    }
                });
                QueryDocumentSnapshot place= (QueryDocumentSnapshot) touristMarkers.get(tmppos).getTag();
                firebaseFirestore.collection("TouristSpots").document(place.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot != null) {
                            if (snapshot.exists()) {

                                //SET IMG
                                if(snapshot.getString("place_image")!=null) {
                                    Picasso.get()
                                            .load(snapshot.getString("place_image"))
                                            .placeholder(R.drawable.loads)
                                            .error(R.drawable.picplaceholder)
                                            .into(pic, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    cover.setImageDrawable(pic.getDrawable());
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                }

                                            });
                                }
                                if(snapshot.getString( "place_openhrs" )!=null && snapshot.getString( "place_closehrs" )!=null){
                                    SimpleDateFormat sdf = new SimpleDateFormat( "hh:mm a" );
                                    try {
                                        Date open =  sdf.parse(snapshot.getString( "place_openhrs" ));
                                        Date close =  sdf.parse(snapshot.getString( "place_closehrs" ));

                                        Calendar openCalendar = Calendar.getInstance();
                                        openCalendar.setTime(open);
                                        Calendar closeCalendar = Calendar.getInstance();
                                        closeCalendar.setTime(close);

                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        Date date = new Date();
                                        String timenow = dateFormat.format( date ).toUpperCase();
                                        Date now = sdf.parse(timenow);
                                        Calendar current = Calendar.getInstance();
                                        current.setTime( now );
                                        int id = 0;
                                        if(isBetweenValidTime( openCalendar.getTime(),closeCalendar.getTime(),current.getTime() )) {
                                            id = R.color.black;
                                            Log.d("closeopen","open");
                                        }else {
                                            id = R.color.maroon;
                                            Log.d("closeopen","close");
                                        }

                                        closed.setText( (id==R.color.black)?"Opened":"Closed!" );
                                        closed.setTextColor( getResources().getColor(id) );

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    openhrs.setText( snapshot.getString( "place_openhrs" ).toUpperCase()
                                            + " - " +
                                            snapshot.getString( "place_closehrs" ).toUpperCase()
                                    );

                                }
//                                else{
//                                    details.setVisibility( View.GONE );
//                                }

                                if(snapshot.getString("place_history")!=null){
                                    txtDetails.setText(snapshot.getString("place_history"));
                                    speak.performClick();

                                }
                            }
                        }
                    }
                });

                placedetails.setContentView(view);
                placedetails.show();
                startDistanceCheck=false;
            }

        }
    }

    public void saveRating(int rate, final DialogInterface aDialog){
        if(travelId!="") {
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("travel_status", "completed");
        tmp.put("travel_completed", new Timestamp(new Date()));
        tmp.put("travel_user_rate",rate);
        pd = new ProgressDialog(getContext(),ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Saving travel status.");
        pd.setMessage("Saving additional data [Travel Completed Data]...");
        pd.setCancelable(false);
        pd.show();
        firebaseFirestore.collection("Travels").document(travelId).update(tmp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    travelId="";
                    clearSomeFields();
                    pd.dismiss();
                    tstr("Saved!");
                    aDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tstr(e.getMessage());
                clearSomeFields();
                pd.dismiss();

            }
        });
        nearestSpots();

    }else{
        tstr("Empty travel id, Completion data not saved!");
        aDialog.dismiss();
        clearSomeFields();
    }
    }
    public void cleanMap(){
        if(myLoc!=null) {
            autonav = false;
            mMap.setTrafficEnabled(true);
            mMap.clear();

            loadTouristSpots();
            drawCircleOnce=true;

        }else tstr("Make sure your location is turned on.");
    }

    public void drawDirection(){
        pd = new ProgressDialog(getContext(),ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Loading Directions.");
        pd.setMessage("Please wait for your directions...");
        pd.setCancelable(false);
        pd.show();
        sendDirectionRequest(new LatLng(myLoc.getLatitude(),myLoc.getLongitude()),touristLatLng.get(tmppos));
    }

    public void alert(String title,String msg,String btnok){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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
    public void nearestSpots(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(touristMarkers.size()>0 && myLoc!=null) {
            Location target = new Location("target");
            ArrayList<QueryDocumentSnapshot> allnearest = new ArrayList<QueryDocumentSnapshot>();
            QueryDocumentSnapshot queryDocumentSnapshot=null;
            for (Marker mark : touristMarkers) {
                target.setLatitude(mark.getPosition().latitude);
                target.setLongitude(mark.getPosition().longitude);
                if (myLoc.distanceTo(target)<= Integer.parseInt(defaultradius)) {
                    queryDocumentSnapshot = (QueryDocumentSnapshot) mark.getTag();
                    Log.d("nearme", target.getLatitude() + " " + target.getLongitude() + " name : "+ queryDocumentSnapshot.getId() + " " + Tspots.get(tmppos).getId());
                    //CHECK IF NEAREST IS NOT REPEATED THEN DRAW ROUTES
                    if(!queryDocumentSnapshot.getId().equals(Tspots.get(tmppos).getId())){
                        builder.include(new LatLng(mark.getPosition().latitude,mark.getPosition().longitude));
                        allnearest.add(queryDocumentSnapshot);
                    }
                    //END
                }
            }
            listAllNearest(allnearest);
            //CAMERA SHOW ALL NEAREST
//            try {
//                LatLngBounds bounds = builder.build();
//                int padding = 200; // offset from edges of the map in pixels
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                mMap.animateCamera(cu);
//            }catch (Exception e){}
        }
    }

    public void listAllNearest(ArrayList<QueryDocumentSnapshot> queryDocumentSnapshots){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        View v = LayoutInflater.from(getContext()).inflate(R.layout.nearest_tspot,null);
        ListView listNearest = v.findViewById(R.id.nearestTspots);
        final ArrayList<NearestTSpot> nearestTSpots = new ArrayList<NearestTSpot>();
        NearestTSpotAdapter adapter = new NearestTSpotAdapter(getContext(),nearestTSpots);
        listNearest.setAdapter(adapter);
        for(int i=0;i<queryDocumentSnapshots.size();i++){
            String placeid=queryDocumentSnapshots.get(i).getId();
            String place_name=queryDocumentSnapshots.get(i).getString("place_name");
            String place_hist=queryDocumentSnapshots.get(i).getString("place_history");
            String place_img = queryDocumentSnapshots.get(i).getString("place_image");
            Location loc = new Location("");
            GeoPoint placeloc = queryDocumentSnapshots.get(i).getGeoPoint("place_latlng");
            loc.setLatitude(placeloc.getLatitude());
            loc.setLongitude(placeloc.getLongitude());
            double distance = myLoc.distanceTo(loc);
            nearestTSpots.add(new NearestTSpot(placeid,place_name,place_hist,place_img,0,distance,placeloc));
        }
        adapter.notifyDataSetChanged();
        alert.setTitle("Nearby Tourist Spots")
                .setView(v)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        if(nearestTSpots.size()<=0){
            tstr("No nearest Tourist spots!");
        }else{
            final AlertDialog alertDialog = alert.show();
            listNearest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    touristSpots.setText(nearestTSpots.get(position).getPlace_name());
                    preparefortravel();
                    alertDialog.dismiss();
                }
            });
        }

    }

    public void sendDirectionRequest(LatLng origin,LatLng destination){

        String directionAPIURL="https://maps.googleapis.com/maps/api/directions/json?origin="+parcelatlng(origin)+"&destination="+parcelatlng(destination)+"&mode="+typeoftravel+"&alternatives=true&key="+getResources().getString(R.string.directionkey);
        Log.d("theurl",directionAPIURL);
        try {
            target = new URL(directionAPIURL);
            hcon = (HttpURLConnection) target.openConnection();
            is = hcon.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String tmp="",res="";

            while((tmp=br.readLine())!=null){
                res=res + tmp;
            }

            hcon.disconnect();
            is.close();
            br.close();
            JSONObject jsonObject = new JSONObject(res);
//            Log.d("gotjson", String.valueOf(jsonObject.getJSONArray("routes")));

            JSONArray jsonArray = jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");


            int length = jsonArray.length();
            String []polyarray = new String[length];
            JSONObject jsonObject1;

            //TO ADD A POINTS OF LATLNG ROUTE TO FIND THE LAST POINT AND TO CHECK IF THE USER ARRIVED.
            JSONObject findLastPoint = jsonObject.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("end_location");

            double lat = findLastPoint.getDouble("lat");
            double lng = findLastPoint.getDouble("lng");
            travelEndPoints.add(new LatLng(lat, lng));
            //END

            for (int i = 0;i<length;i++){
                jsonObject1 = jsonArray.getJSONObject(i);
                Log.d("gotjson", String.valueOf(jsonObject1));
                String polygon =  jsonObject1.getJSONObject("polyline").getString("points");
                polyarray[i] = polygon;
                Log.d("endroutes", String.valueOf(jsonObject1));

            }

            int length2 = polyarray.length;
            PolylineOptions polylineOptions=null;
            for (int i = 0;i<length2;i++){
               polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE)
                        .width(20)
                        .addAll(PolyUtil.decode(polyarray[i]));

                mMap.addPolyline(polylineOptions);
            }

            //END

            if(travelEndPoints.size()>0) {
                LatLng lastdest = travelEndPoints.get(travelEndPoints.size() - 1);
                routeLastPoint.setLatitude(lastdest.latitude);
                routeLastPoint.setLongitude(lastdest.longitude);
                distancetodest = myLoc.distanceTo(routeLastPoint);
                Log.d("distancetotal=", distancetodest + "");
            }else tstr("End point not found!");

            //FOR ALTERNATIVE ROUTE
            //IF STATEMENT TO CHECK IF THERES A ALTERNATIVE ROUTE
            if(jsonObject.getJSONArray("routes").length()>1) {
                JSONArray jsonArray0 = jsonObject.getJSONArray("routes")
                        .getJSONObject(1)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONArray("steps");


                int length0 = jsonArray0.length();
                String[] polyarray2 = new String[length0];
                JSONObject jsonObject0;
                for (int i = 0; i < length0; i++) {
                    jsonObject0 = jsonArray0.getJSONObject(i);
                    String polygon = jsonObject0.getJSONObject("polyline").getString("points");
                    polyarray2[i] = polygon;
                }
                int length02 = polyarray2.length;
                for (int i = 0; i < length02; i++) {
                    PolylineOptions polylineOptions2 = new PolylineOptions();
                    polylineOptions2.color(Color.GREEN)
                            .width(17)
                            .addAll(PolyUtil.decode(polyarray2[i]));
                    mMap.addPolyline(polylineOptions2);
                }
            }else tstr("No alternative routes.");

            pd.dismiss();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String parcelatlng(LatLng parcethis){
        return parcethis.latitude + "," + parcethis.longitude;

    }
    public String parcetomapboxlnglat(LatLng parcethis){
        return parcethis.longitude + "," + parcethis.latitude;
    }


    public void loadTouristSpots() {
        try {
            firebaseFirestore.getInstance().collection("TouristSpots").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if(touristMarkers.size()>0) touristMarkers.clear();

                    mMap.clear();
                    final Marker[] n = new Marker[1];
                    touristLatLng.clear();
                    for (final QueryDocumentSnapshot q : queryDocumentSnapshots) {
                        if (q != null) {
                            if (q.getGeoPoint("place_latlng") != null) {
                                touristLatLng.add(new LatLng(q.getGeoPoint("place_latlng").getLatitude(), q.getGeoPoint("place_latlng").getLongitude()));    // ADD NEAREST BARBER
                                n[0] = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(q.getGeoPoint("place_latlng").getLatitude(), q.getGeoPoint("place_latlng").getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(getContext(), "", "Ezek")))
                                        .title(q.getString("place_name"))
                                );
                                //placeidholder sih = new placeidholder(q.getId(),q.getString("place_name"),q.getString("place_history"),q.getString("place_image"));
                                n[0].setTag(q);
                                touristMarkers.add(n[0]);
                            }
                        }
                    }
                }
            });


        } catch (Exception ss) {
        }



    }

    public int computeplacerate(String placeid){

        //COMPUTE ALL RATINGS FROM TRAVEL DATABASE
        firebaseFirestore.collection("Travels").whereEqualTo("travel_place_id",placeid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e!=null){
                    return;
                }
                tstr( "test" );
                if(queryDocumentSnapshots!=null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        //THIS TMP VALUES IS A STORAGE TO COMPUTE OVERALL RATE
                        int tmp1=0,tmp2=0,tmp3=0,tmp4=0,tmp5=0;
                        for (QueryDocumentSnapshot q:queryDocumentSnapshots){
                            if(q!=null && q.exists()){
                                if(q.getLong("travel_user_rate")!=null){
                                    int rowRate = q.getLong("travel_user_rate").intValue();
                                    switch (rowRate){
                                        case 1:
                                            tmp1 = tmp1 + 1;
                                            break;
                                        case 2:
                                            tmp2 = tmp2 + 1;
                                            break;
                                        case 3:
                                            tmp3 = tmp3 + 1;
                                            break;
                                        case 4:
                                            tmp4 = tmp4 + 1;
                                            break;
                                        case 5:
                                            tmp5 = tmp5 + 1;
                                            break;
                                    }
                                }

                            }
                        }
                        //COMPUTE ALL RATE
                        //SAMPLE FORMULA : (5*252 + 4*124 + 3*40 + 2*29 + 1*33) / (252+124+40+29+33)
                        int total1 = (5*tmp5 + 4*tmp4 + 3*tmp3 + 2*tmp2 + 1*tmp1);
                        int total2 = (tmp5+tmp4+tmp3+tmp2+tmp1);
                        try {
                            totalrate = total1 / total2;
                        }catch (Exception ex){}
                        pd.dismiss();

                    }
                }
            }
        });
        return totalrate;
    }

    // CLICK TOURISTSPOT MARKER
    @Override
    public boolean onMarkerClick(Marker marker) {
        QueryDocumentSnapshot queryDocumentSnapshot =null;
        if(marker!=null && marker.getTag()!=null) {
            if (tts != null) {
                tts.stop();
                tts = null;
            }
            speak();
            queryDocumentSnapshot = (QueryDocumentSnapshot) marker.getTag();
            final String id = queryDocumentSnapshot.getId();
            final Dialog placedetails = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            placedetails.setCancelable(false);
            this.view = LayoutInflater.from(getContext()).inflate(R.layout.place_details, null);
            txtDetails = view.findViewById(R.id.txt_hist);
            final CircleImageView pic = view.findViewById(R.id.placeImg);
            final ImageView cover = view.findViewById(R.id.placecover), close = view.findViewById(R.id.btnCloseDetails);
            final RatingBar rate = view.findViewById(R.id.placeRate);
            final TextView openhrs = view.findViewById( R.id.ts_openhrs ),
                    closed = view.findViewById( R.id.ts_closed );
            final LinearLayout details = view.findViewById( R.id.details );
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placedetails.dismiss();
                    if (tts != null) {
                        tts.stop();
                        tts.shutdown();
                    }
                }
            });
            speak = view.findViewById(R.id.btnSpeak);
            speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (speak.getText().toString().toLowerCase().equals("speak")) {
                        tstr("Speech Started!");
                        speak(txtDetails.getText().toString());
                        speak.setText("Stop");
                        speak.setTextColor(getResources().getColor(R.color.m_red));

                    } else {
                        speak.setText("Speak");
                        speak.setTextColor(getResources().getColor(R.color.m_blue));
                        tts.stop();
//                    tts.shutdown();
                    }
                }
            });

            firebaseFirestore.collection("TouristSpots").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot != null) {
                        if (snapshot.exists()) {

                            //SET IMG
                            if (snapshot.getString("place_image") != null) {
                                Picasso.get()
                                        .load(snapshot.getString("place_image"))
                                        .placeholder(R.drawable.loads)
                                        .error(R.drawable.picplaceholder)
                                        .into(pic, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                cover.setImageDrawable(pic.getDrawable());
                                            }

                                            @Override
                                            public void onError(Exception e) {

                                            }

                                        });
                            }

                            if(snapshot.getString( "place_openhrs" )!=null && snapshot.getString( "place_closehrs" )!=null){
//                                CLICK MARKER
//                                details.setVisibility( View.VISIBLE );
//
//                                    String open  = snapshot.getString( "place_openhrs" );
//                                    String close  = snapshot.getString( "place_closehrs" );
//                                    Calendar now = Calendar.getInstance();
//
//                                    int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
//                                    int minute = now.get(Calendar.MINUTE);
//
//                                    Date currentTime = parseDate(hour+":"+minute);
//                                    Date timeopen = parseDate(open);
//                                    Date timeclose = parseDate(close);
//                                    Log.d("timen",currentTime + " " + timeopen + " " + timeclose);
//                                    Log.d("timenbef",currentTime.after( timeopen )+"");
//                                    Log.d("timenaft",currentTime.before( timeclose )+"");
//                                    if (currentTime.after( timeopen ) && currentTime.before(timeclose)) {
//                                        //your logic
//                                        Log.d("openclose","open");
//                                    }else{
//                                        Log.d("openclose","close");
//                                    }
////
////                                    if(current.getTime().after( openCalendar.getTime()) && current.getTime().before(closeCalendar.getTime()))
////                                     {
////                                        id = R.color.black;
////                                    }else id = R.color.maroon;
////
////                                    closed.setText( (id==R.color.black)?"Opened":"Closed!" );
////
//                                openhrs.setText( snapshot.getString( "place_openhrs" ).toUpperCase()
//                                                + " - " +
//                                                snapshot.getString( "place_closehrs" ).toUpperCase()
//                                                );

                                SimpleDateFormat sdf = new SimpleDateFormat( "hh:mm a" );
                                try {
                                    Date open =  sdf.parse(snapshot.getString( "place_openhrs" ));
                                    Date close =  sdf.parse(snapshot.getString( "place_closehrs" ));

                                    Calendar openCalendar = Calendar.getInstance();
                                    openCalendar.setTime(open);
                                    Calendar closeCalendar = Calendar.getInstance();
                                    closeCalendar.setTime(close);

                                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    Date date = new Date();
                                    String timenow = dateFormat.format( date ).toUpperCase();
                                    Date now = sdf.parse(timenow);
                                    Calendar current = Calendar.getInstance();
                                    current.setTime( now );
                                    int id = 0;
                                    if(isBetweenValidTime( openCalendar.getTime(),closeCalendar.getTime(),current.getTime() )) {
                                        id = R.color.black;
                                        Log.d("closeopen","open");
                                    }else {
                                        id = R.color.maroon;
                                        Log.d("closeopen","close");
                                    }

                                    closed.setText( (id==R.color.black)?"Opened":"Closed!" );
                                    closed.setTextColor( getResources().getColor(id) );

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                openhrs.setText( snapshot.getString( "place_openhrs" ).toUpperCase()
                                        + " - " +
                                        snapshot.getString( "place_closehrs" ).toUpperCase()
                                );
                            }else{
                                details.setVisibility( View.GONE );
                            }
                            if (snapshot.getString("place_history") != null) {
                                txtDetails.setText(snapshot.getString("place_history"));
                                speak.performClick();
                            }
                        }
                    }
                }
            });

            placedetails.setContentView(view);
            placedetails.show();
        }
        return false;
    }
    public static final boolean isBetweenValidTime(Date startTime, Date endTime, Date validateTime)
    {
        boolean validTimeFlag = false;

        if(endTime.compareTo(startTime) <= 0)
        {
            if(validateTime.compareTo(endTime) < 0 || validateTime.compareTo(startTime) >= 0)
            {
                validTimeFlag = true;
            }
        }
        else if(validateTime.compareTo(endTime) < 0 && validateTime.compareTo(startTime) >= 0)
        {
            validTimeFlag = true;
        }

        return validTimeFlag;
    }

    // VOICE TEXT TO SPEECH
    public void speak(){
        tts=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                }else{
                    tstr("TextToSpeech Failed!");
                }
            }
        });
    }
    private void speak(String msg) {
        if(tts!=null) {
            float pitch = (float) 50 / 50;
            if (pitch < 0.1) pitch = 0.1f;
            float speed = (float) 50 / 50;
            if (speed < 0.1) speed = 0.1f;

            tts.setPitch(pitch);
            tts.setSpeechRate(speed);

            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void tstr(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();}

    static class placeidholder{
        private String id,placename,placehistory,placeimg;


        public String getPlaceimg() {

            return placeimg;
        }

        public void setPlaceimg(String placeimg) {
            this.placeimg = placeimg;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlacename() {
            return placename;
        }

        public void setPlacename(String placename) {
            this.placename = placename;
        }

        public String getPlacehistory() {
            return placehistory;
        }

        public void setPlacehistory(String placehistory) {
            this.placehistory = placehistory;
        }

        public placeidholder(String id, String placename, String placehistory, String placeimg) {
            this.id = id;
            this.placename = placename;
            this.placehistory = placehistory;
            this.placeimg = placeimg;

        }
    }

    //TOURIST SPOT MARKER THAT DISPLAYED IN MAP
    public Bitmap createCustomMarker(final Context context, String imgurl, final String _name) {
        final View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        AppCompatImageView  markerImage = (AppCompatImageView) marker.findViewById(R.id.user_dp);
        if(imgurl!=""){
            markerImage.setImageResource(R.drawable.marker);
            //FOR CUSTOMIZE PICTURE
//            Picasso.get()
//                    .load(imgurl)
//                    .placeholder(R.drawable.image)
//                    .error(R.drawable.logo)
//                    .into(markerImage);
        }
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
    public Bitmap createCustomMarkerWithPic(final Context context, String imgurl, final String _name) {
        final View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.restaurant_marker, null);
        AppCompatImageView  markerImage = (AppCompatImageView ) marker.findViewById(R.id.user_dp);
        if(imgurl!=""){
            markerImage.setImageResource(R.drawable.rtpin);
//            FOR CUSTOMIZE PICTURE
//            Picasso.get()
//                    .load(imgurl)
//                    .placeholder(R.drawable.destinationpin)
//                    .error(R.drawable.logo)
//                    .into(markerImage);
        }
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

}
