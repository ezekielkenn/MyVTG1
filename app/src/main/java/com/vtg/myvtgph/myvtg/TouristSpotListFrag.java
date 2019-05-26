package com.vtg.myvtgph.myvtg;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class TouristSpotListFrag extends Fragment {
    private ArrayList<TouristSpotList> listTs = new ArrayList<TouristSpotList>();
    private TouristSpotListAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;


    //ProgressDIALOG
    private ProgressDialog pd;

    //FOR LISTVIEW
    private ListView myRecord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.touristspot_list, container, false );
        //VERIFY IF USER IS LOGEDIN
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser() != null) userId = firebaseAuth.getCurrentUser().getUid();
        else getActivity().finish();

        myRecord = v.findViewById( R.id.listTouristSpot );
        adapter = new TouristSpotListAdapter( getContext(), listTs );


        //SET THE LISTVIEW DATA ADAPTER TO THE CUSTOM ADAPTER
        myRecord.setAdapter( adapter );
        myRecord.setEmptyView( v.findViewById( R.id.txtEmptyTouristSpots ) );


        //CALL AND CHECK FOR COMPLETED/ONGOING RECORDS
        getMyRecord();

        return v;

    }

    public void getMyRecord(){
        pd = new ProgressDialog(getContext(),ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Loading tourist spots.");
        pd.setMessage("Retrieving Places details...");
        pd.setCancelable(false);
        pd.show();
        firebaseFirestore.collection( "TouristSpots" ).addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }
                if(queryDocumentSnapshots!=null){
                    if(queryDocumentSnapshots.size()>0){
                        listTs.clear();
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots){
                            if(q!=null){
                                if(q.exists()){
                                    //String place_id, String placeName, String placeHistory, String placeImgUrl
                                    listTs.add( new TouristSpotList( q.getId() ,q.getString( "place_name" ),q.getString( "place_history" ),q.getString( "place_image" )));
                                }
                            }
                        }
                        pd.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } );


    }

//    public void showPlaceDetails(final String placeId){
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.touristspot,null);
//        final CircleImageView pic = view.findViewById(R.id.placeImg);
//        final TextView txtDetails1 = view.findViewById(R.id.placeName);
//        final Dialog placedetails = new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
//
//
//        firebaseFirestore.collection("TouristSpots").document(placeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot snapshot) {
//                if (snapshot != null) {
//                    if (snapshot.exists()) {
//                        //SET IMG
//                        if(snapshot.getString("place_image")!=null) {
//                            Picasso.get()
//                                    .load(snapshot.getString("place_image"))
//                                    .placeholder(R.drawable.loads)
//                                    .error(R.drawable.picplaceholder);
//                        }
//
//                        if(snapshot.getString("place_name")!=null){
//                            txtDetails1.setText(snapshot.getString("place_name"));
//
//                        }
//
//                    }
//                }
//            }
//        });
//
//        placedetails.setContentView(view);
//        placedetails.show();
//    }
}