package com.example.soundtest.Question;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soundtest.MainActivity;
import com.example.soundtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button updateAccountButtn;
    private EditText userName,userStatuse,phoneNumber,gender,DoB;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagerReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserProfileImagerReference = FirebaseStorage.getInstance().getReference().child("profile Images");
        init();

        updateAccountButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateSettings();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode ==RESULT_OK && data!=null ){

            Uri ImageUri= data.getData();

            CropImage.activity()
                     .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImagerReference.child(currentUserID+ ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(SettingsActivity.this, "Profile Image Upload", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            String messege  = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error:" +messege, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }

        }


    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatuse.getText().toString();
        String setphoneNo = phoneNumber.getText().toString();
        String setGender = gender.getText().toString();
        String setdate = DoB.getText().toString();


        if (TextUtils.isEmpty(setUserName)) {

            Toast.makeText(this, "Please Write your User Name First...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(setStatus)) {

            Toast.makeText(this, "Please Write your Statuse First...", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String,String>profileMap=new HashMap<>();
            profileMap.put("uID",currentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);
            profileMap.put("Phone No",setphoneNo);
            profileMap.put("Gender",setGender);
            profileMap.put("Date of Birth",setdate);
            RootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this, "ProfileUpdated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String massage=task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error:"+massage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    private void SendUserToMainActivity() {

        Intent mainIntent=new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void init() {

        updateAccountButtn=findViewById(R.id.update_settings_buttn);
        userName = findViewById(R.id.set_user_name);
        userStatuse=findViewById(R.id.set_user_statuse);
        userProfileImage=findViewById(R.id.set_profile_image);
        phoneNumber =findViewById(R.id.set_user_phone_number);
        gender =findViewById(R.id.set_user_gender);
        DoB=findViewById(R.id.set_user_date_of_birth);
    }
}
