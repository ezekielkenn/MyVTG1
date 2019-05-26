package com.vtg.myvtgph.myvtg;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.location.Criteria;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitedFrag extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<Visits> listVisits= new ArrayList<Visits>();
    private VisitedListAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;

    //ProgressDIALOG
    private ProgressDialog pd;

    //FOR LISTVIEW
    private ListView myRecord;

    //INFINITE LOOP STOPPER
    private boolean once;

    //TEXTTOSPEECH
    TextToSpeech tts;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visited,container,false);
        //VERIFY IF USER IS LOGEDIN
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) userId=firebaseAuth.getCurrentUser().getUid();
        else getActivity().finish();
        myRecord = v.findViewById(R.id.listVisited);
        myRecord.setOnItemClickListener(this);
        myRecord.setEmptyView(v.findViewById(R.id.txtemptyVisits));
        adapter = new VisitedListAdapter(getContext(),listVisits);


        once=true;

        //SET THE LISTVIEW DATA ADAPTER TO THE CUSTOM ADAPTER
        myRecord.setAdapter(adapter);

        //INIT TEXT TO SPEECH
        speak();

        //CALL AND CHECK FOR COMPLETED/ONGOING RECORDS
        getMyRecord();

        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
//            tts.shutdown();
        }

    }


    public void getMyRecord(){
        pd = new ProgressDialog(getContext(),ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Retrieving history.");
        pd.setMessage("Retrieving Places details and Travel history...");
        pd.setCancelable(false);
        pd.show();
        firebaseFirestore.collection("Travels").whereEqualTo("travel_user_id",userId).whereEqualTo("travel_status","completed").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    tstr(e.getMessage(),1);
                    Log.d("visitedfragerror",e.getMessage());
                    pd.dismiss();
                    return;
                }
                if(once) {
                    if (queryDocumentSnapshots != null) {
                        if(queryDocumentSnapshots.size()>0){
                            listVisits.clear();
                            for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                                if(q!=null){
                                    if(q.exists()){
                                        if(q.getString("travel_status").toLowerCase().equals("completed")) {
                                            listVisits.add(new Visits(
                                                    q.getString("travel_place_name"),
                                                    q.getString("travel_place_history"),
                                                    q.getString("travel_place_img"),
                                                    q.getTimestamp("travel_completed"),
                                                    q.getLong("travel_user_rate").intValue(),
                                                    q.getString("travel_place_id")
                                                    ));
                                        }//else tstr("ongoing history coming soon...",0);
                                    }
                                }
                            }
                            pd.dismiss();
                            once=false;
                            adapter.notifyDataSetChanged();
                        }else pd.dismiss();
                    }
                }
            }
        });

    }
    public void tstr(String msg,int lng){
        Toast.makeText(getContext(), msg, lng).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //INIT TEXTTOSPEECH
        speak();
        CircleImageView civ = view.findViewById(R.id.placeImg);
        Visits visits = (Visits) civ.getTag() ;
        String placeId= visits.getPlaceId();
        showPlaceDetails(placeId);
    }

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

    public void tstr(String msg){Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();}

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

    public void showPlaceDetails(final String placeId){

        final Dialog placedetails = new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.place_details,null);
        final TextView txtDetails = view.findViewById(R.id.txt_hist);
        final RatingBar placeRate = view.findViewById(R.id.placeRate);
        final CircleImageView pic = view.findViewById(R.id.placeImg);
        final Button speak;
        final TextView openhrs = view.findViewById( R.id.ts_openhrs ),
                closed = view.findViewById( R.id.ts_closed );
        final ImageView cover = view.findViewById(R.id.placecover),close = view.findViewById(R.id.btnCloseDetails);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placedetails.dismiss();
                if(tts!=null){
                    tts.stop();
                    tts.shutdown();
                }
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
                    speak.setTextColor(getResources().getColor(R.color.m_red));

                }else{
                    speak.setText("Speak");
                    speak.setTextColor(getResources().getColor(R.color.m_blue));
                    tts.stop();
//                    tts.shutdown();
                }
            }
        });

        firebaseFirestore.collection("TouristSpots").document(placeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        if(snapshot.getString("place_history")!=null){
                            txtDetails.setText(snapshot.getString("place_history"));
//                            speak.performClick();
                        }
                        //COMPUTE ALL RATINGS FROM TRAVEL DATABASE
                        firebaseFirestore.collection("Travels").whereEqualTo("travel_place_id",placeId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if(e!=null){
                                    tstr(e.getMessage(),1);
                                    return;
                                }
                                if(queryDocumentSnapshots!=null){
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        //THIS TMP VALUES IS A STORAGE TO COMPUTE OVERALL RATE
                                        int tmp1=0,tmp2=0,tmp3=0,tmp4=0,tmp5=0;
                                        int totalRate;
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
                                        totalRate = (5*tmp5 + 4*tmp4 + 3*tmp3 + 2*tmp2 + 1*tmp1) / (tmp5+tmp4+tmp3+tmp2+tmp1);
                                        placeRate.setRating(totalRate);
                                    }
                                }

                            }
                        });
                    }
                }
            }
        });

        placedetails.setContentView(view);
        placedetails.show();
    }
}
