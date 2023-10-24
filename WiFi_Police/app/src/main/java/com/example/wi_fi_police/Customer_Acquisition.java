package com.example.wi_fi_police;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;



import com.example.wi_fi_police.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Customer_Acquisition extends AppCompatActivity {

    private TextInputLayout locationTextInputLayout;
    private TextInputEditText locationTextField;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int  CAMERA_PERMISSION_CODE =1;
    private static final int LOCATION_PERMISSION_CODE = 101;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_IMAGE_PICK = 2;

    ///Layout widgets
    private TextView imageNameTextView;
    private Button submitButton;



    private Bitmap imageBitmap;
    private ActivityResultLauncher<Uri> takePictureLauncher;

    Uri imageUri;

    ImageView uploadedImage;



    ///Firebase
    FirebaseDatabase db;
    DatabaseReference reference;


    ///Form Fields



   TextInputLayout firstname,surname, email, cellNumber, Location;
   Spinner buildingType;
    //Firebase





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_acquisition);

        locationTextInputLayout = findViewById(R.id.locationTextInputLayout);
        locationTextField = findViewById(R.id.locationTextField);
        firstname = findViewById(R.id.firstnameInput);
        surname = findViewById(R.id.Surname);
        email = findViewById(R.id.Email);
        cellNumber = findViewById(R.id.number);
        Location = findViewById(R.id.locationTextInputLayout);
        buildingType = findViewById(R.id.spinner);


        submitButton = findViewById(R.id.submitBtn);

        uploadedImage = findViewById(R.id.uploadedImage);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationTextInputLayout.setEndIconOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                 if(checkLocationPermission()){
                     fusedLocationClient.getLastLocation()
                             .addOnSuccessListener(Customer_Acquisition.this,new OnSuccessListener<Location>(){

                                 @Override
                                 public void onSuccess(Location location) {
                                     if(location !=null){
                                         locationTextField.setText(location.getLatitude() + "," + location.getLongitude());
                                     }
                                 }
                             });

                 }

            }
        });

        ///Dealing with the dropdown list
          ///Building Material Type
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.BuildingType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Camera Methods

        Button uploadButton = findViewById(R.id.uploadButton);
        Button cameraButton = findViewById(R.id.cameraButton);

        ActivityResultLauncher<String> getImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null){
                            uploadedImage.setVisibility(View.VISIBLE);
                            uploadedImage.setImageURI(result);
                        }
                    }
                }
        );


       uploadButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               getImage.launch("image/*");
           }
       });



        ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean success) {
                        if (success) {
                            // Image captured successfully, display it
                            if (imageUri != null) {
                                uploadedImage.setVisibility(View.VISIBLE);
                                uploadedImage.setImageURI(imageUri);
                            }
                        } else {
                            // Handle error or cancellation
                            // You can add your own logic here
                        }
                    }
                }
        );

        ///Take Picture code


        imageUri = createUri();
        registerPictureLauncher();
        cameraButton.setOnClickListener(view->{
            checkCameraPermissionAndOpenCamera();
        });


        ///Saving data to firebase
        submitButton.setOnClickListener(view ->{
            db = FirebaseDatabase.getInstance();
            reference = db.getReference("customers");

            ///Get all the values

            String name = firstname.getEditText().getText().toString();
            String last = surname.getEditText().getText().toString();
            String emailAddr = email.getEditText().getText().toString();
            String cell = cellNumber.getEditText().getText().toString();
            String coordinates = Location.getEditText().getText().toString();
            String buildingMaterial = buildingType.getSelectedItem().toString();

            String imgUriString = imageUri.toString();
            Customers customers = new Customers(name,last,emailAddr,cell,coordinates,buildingMaterial,imgUriString);

            //Random ID numbers

            Random random = new Random();
            int userID = random.nextInt();
            String client = "Customer " + userID;
            reference.child(client).setValue(customers);
            Toast.makeText(this, "Customer details saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, Spash_Activity.class);
            startActivity(intent);
        });

    }

    private Uri createUri(){
       File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
       return FileProvider.getUriForFile(
               getApplicationContext(),"com.example.wi_fi_police.fileProvider",
               imageFile
       );
    }

    private void registerPictureLauncher(){
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                       try{
                           if(result){
                               uploadedImage.setVisibility(View.VISIBLE);
                               uploadedImage.setImageURI(null);
                               uploadedImage.setImageURI(imageUri);
                           }
                       }catch(Exception exception){
                          exception.getStackTrace();
                       }
                    }
                }
        );
    }







    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now proceed to get the location
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePictureLauncher.launch(imageUri);
            }else{
                Toast.makeText(this,"Camera permission denied, please allow permission to take picture", Toast.LENGTH_SHORT);
            }
        }

    }
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void checkCameraPermissionAndOpenCamera(){
        if(ActivityCompat.checkSelfPermission(Customer_Acquisition.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Customer_Acquisition.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }else{
            takePictureLauncher.launch(imageUri);
        }
    }


}