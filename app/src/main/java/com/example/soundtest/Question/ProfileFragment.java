package com.example.soundtest.Question;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.soundtest.R;
import com.example.soundtest.Read.Read;
import com.example.soundtest.Salah.BoysFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView userName,userPhone,userEmail;
    private ImageView alemList;
    private ImageView userImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    Activity context;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        context = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth=FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        userName = view.findViewById(R.id.get_user_name);
        userPhone = view.findViewById(R.id.get_user_phone_number);
        userEmail = view.findViewById(R.id.get_user_email);
        userImage = view.findViewById(R.id.get_profile_image);
        alemList = view.findViewById(R.id.alemList);

        alemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             Intent intent = new Intent(context, AlemList.class);
             startActivity(intent);
            }
        });

        RetreveData();


      return view;
    }

    private void RetreveData() {

        RootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String retreveUsername = dataSnapshot.child("name").getValue().toString();
                String retreveUserPhone = dataSnapshot.child("phoneNo").getValue().toString();
                String retreveUserEmail = dataSnapshot.child("email").getValue().toString();

                userName.setText(retreveUsername);
                userPhone.setText(retreveUserPhone);
                userEmail.setText(retreveUserEmail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
