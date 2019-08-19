package com.Spath_Family.TimeSheet1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CreateChoice extends AppCompatActivity implements View.OnClickListener{

    private Button company;
    private Button employee;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_choice);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        company= findViewById(R.id.btCompany);
        employee=findViewById(R.id.btEmployee);
        back = findViewById(R.id.btBackEdit);

        company.setOnClickListener(this);
        employee.setOnClickListener(this);
        back.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view == company) {
            startActivity(new Intent(CreateChoice.this, CreateCompany.class));
//            Toast.makeText(this, "Coming Soon 23/8/19", Toast.LENGTH_SHORT).show();
        }
        if (view == employee){
            startActivity(new Intent(CreateChoice.this, CreateEmployee.class));
        }
        if (view == back){
            finish();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
