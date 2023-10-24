package com.example.wi_fi_police;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;


import com.example.wi_fi_police.R;
import com.example.wi_fi_police.databinding.ActivityMainBinding;

public class Spash_Activity extends AppCompatActivity {


    ActivityMainBinding binding;
    ImageButton OnboardingButton;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_spash);

       String[] optionName = {"Site Acquisition","Customer Onboarding","Troubleshoot","Map","Acquisition Report","Onboarding Report"};
       int[] images ={R.drawable.acquisition,R.drawable.onboarding,R.drawable.troubleshooting,R.drawable.map,R.drawable.report,R.drawable.onboarding_report};
        gridView = findViewById(R.id.gridView);
       gridAdapter GridAdapter = new gridAdapter(Spash_Activity.this,optionName,images);
        gridView.setAdapter(GridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 if(images[i] == R.drawable.acquisition){
                     Intent intent = new Intent(Spash_Activity.this,Customer_Acquisition.class);
                     startActivity(intent);
                 }
                 if(images[i] == R.drawable.onboarding){
                     Intent intent = new Intent(Spash_Activity.this,Customer_Onboarding.class);
                     startActivity(intent);
                 }if(images[i] == R.drawable.map){

                }
            }
        });
    }

    public void onBoardingClick( View v){
        Intent intent = new Intent(this, Customer_Onboarding.class);
        startActivity(intent);
    }
    public void onImageClick(View view) {
        // Start the new activity
        Intent intent = new Intent(this, Customer_Acquisition.class);
        startActivity(intent);
    }

}