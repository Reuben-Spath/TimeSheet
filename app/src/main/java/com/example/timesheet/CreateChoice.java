package com.example.timesheet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

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
