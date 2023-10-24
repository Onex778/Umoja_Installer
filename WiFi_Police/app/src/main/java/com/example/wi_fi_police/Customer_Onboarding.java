
package com.example.wi_fi_police;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class Customer_Onboarding extends AppCompatActivity {

    private static final int  CAMERA_PERMISSION_CODE =1;

    Calendar calender;
    int year,month,day;
    String CurrentDate;
    TextInputLayout dateInput, serialNumberInput, searchedit, macInput,customerNumberInput,imsiInput;

    DatabaseReference reference;
    Spinner chooseCustomer, installerSpinner;
    ArrayList<String> customerList, InstallerList;
    Button qrButton, uploadInstallationImage,submitOnboarding;
    ArrayAdapter<String> adapter,installerAdapter;

    TextView qrResults;

    Uri imageUri;
    ImageView uploadedImage;

    RadioGroup radioGroup;
    RadioButton repeaterButton,HubButton,w3500Button;



    FirebaseDatabase db;

    private ActivityResultLauncher<Uri> takePictureLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_onboarding);

        //fields
        customerNumberInput = findViewById(R.id.customerNumberInput);
        imsiInput = findViewById(R.id.imsiInput);
        HubButton= findViewById(R.id.HubButton);

        //Getting the current date
         calender =Calendar.getInstance();
         year = calender.get(Calendar.YEAR);
         month = calender.get(Calendar.MONTH) + 1;
         day = calender.get(Calendar.DAY_OF_MONTH);
         CurrentDate = year + "/" + month + "/" + day;
         dateInput = findViewById(R.id.dateInput);
         dateInput.getEditText().setText(CurrentDate);

        serialNumberInput = findViewById(R.id.serialNumberInput);
         //Populating the spinner with customer names

        chooseCustomer = findViewById(R.id.chooseCustomer);
        installerSpinner = findViewById(R.id.installerSpinner);
        reference = FirebaseDatabase.getInstance().getReference("customers");


        macInput = findViewById(R.id.macInput);
        customerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(Customer_Onboarding.this, android.R.layout.simple_spinner_dropdown_item,customerList);
        adapter.add("Choose customer");
        chooseCustomer.setAdapter(adapter);

        InstallerList = new ArrayList<>();
        installerAdapter = new ArrayAdapter<>(Customer_Onboarding.this, android.R.layout.simple_spinner_dropdown_item,InstallerList);
        installerAdapter.add("Choose Installer");
        installerAdapter.add("Zaid");
        installerSpinner.setAdapter(installerAdapter);

        searchedit = findViewById(R.id.searchedit);
        searchedit.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        showData();

        qrButton = findViewById(R.id.qrButton);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              ScanCode();
            }


        });

        radioGroup = findViewById(R.id.radioGroup);


        uploadedImage = findViewById(R.id.uploadedImage);
        uploadInstallationImage = findViewById(R.id.uploadInstallationImage);

        //Setting the captured Image
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

        imageUri = createUri();
        registerPictureLauncher();


        uploadInstallationImage.setOnClickListener(view->{
            checkCameraPermissionAndOpenCamera();
        });


        repeaterButton = findViewById(R.id.repeaterButton);
        submitOnboarding = findViewById(R.id.submitOnboarding);

        submitOnboarding.setOnClickListener(view -> {
            //Saving Onboarded customers to firebase
            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Onboarded_Customers");

            String date = dateInput.getEditText().getText().toString();
            String customerNumber = customerNumberInput.getEditText().getText().toString();
            String customer = chooseCustomer.getSelectedItem().toString();
            String sitetype ;
            if(radioGroup.getCheckedRadioButtonId() == HubButton.getId()){
                sitetype = "Baicell Hub";
            }else if(radioGroup.getCheckedRadioButtonId() == repeaterButton.getId()){
                sitetype="Repeater";
            }else{
                sitetype = "W3500";
            }
            String Mac = macInput.getEditText().getText().toString() ;
            String SN = serialNumberInput.getEditText().getText().toString();
            String imsi = imsiInput.getEditText().getText().toString();
            String installer = installerSpinner.getSelectedItem().toString();
            String imageUriString = imageUri.toString();
            Onboarded_Customers onboarded_customers = new Onboarded_Customers(date,customerNumber,customer,Mac,SN,imsi,installer,imageUriString,sitetype);

            Random random = new Random();
            int userID = random.nextInt();
            String client = "Onboarded customer " + userID;
            reference.child(client).setValue(onboarded_customers);
            Toast.makeText(this, "Customer Onboarded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Spash_Activity.class);
            startActivity(intent);
        });

    }

    private void checkCameraPermissionAndOpenCamera(){
        if(ActivityCompat.checkSelfPermission(Customer_Onboarding.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Customer_Onboarding.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }else{
            takePictureLauncher.launch(imageUri);
        }
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


    private void ScanCode(){
    ScanOptions scanOptions = new ScanOptions();
    scanOptions.setPrompt("Volume up to turn on the flash");
    scanOptions.setBeepEnabled(true);
    scanOptions.setOrientationLocked(true);
    scanOptions.setCaptureActivity(CaptureAct.class);
   barLauncher.launch(scanOptions);
}
ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult( new ScanContract(),result -> {
    if(result.getContents() !=null){
        AlertDialog.Builder builder = new AlertDialog.Builder(Customer_Onboarding.this);
        builder.setTitle("Result");

        ///Use result.getContents and and Populate MAC Address and Serial Number
        StringBuilder formatted = new StringBuilder();


        String inputResult = result.getContents().toString();
        //checking the radioButton that is checked
        if(radioGroup.getCheckedRadioButtonId() == repeaterButton.getId()){
            int startIndex = inputResult.indexOf("MAC:");
            if(startIndex !=-1){
                String macAddress = inputResult.substring(startIndex + 4, startIndex + 16);

                serialNumberInput.getEditText().setText(formatString(macAddress));
            }
            int snIndex = inputResult.indexOf("SN:");
            if(snIndex !=1){
                String extractedString = inputResult.substring(snIndex + 3);
                macInput.getEditText().setText(extractedString);
            }
        }

        builder.setMessage(result.getContents());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
});


    private void showData(){
        final String[] customerName = new String[1];
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    String firstname = item.child("firstname").getValue(String.class);
                    String surname = item.child("surname").getValue(String.class);

                      customerList.add(firstname + " " + surname);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        if(view.getId() == R.id.HubButton){
            if(checked){

            }
        }
        if(view.getId() == R.id.repeaterButton){
            if(checked){

            }
        }
        if(view.getId() == R.id.w3500Button){
            if(checked){
                
            }
        }
    }

    public static String formatString(String input) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < input.length(); i += 2) {
            if (i > 0) {
                formatted.append(":");
            }
            formatted.append(input.substring(i, i + 2));
        }
        return formatted.toString();
    }

}