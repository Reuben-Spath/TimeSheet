package com.example.timesheet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class CreateCompany extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateCompany";

    private Button btn_sign_up;

    private EditText txt_company_name;
    private EditText txt_emp_code;
    private EditText txt_company_email_sign_up;
    private EditText txt_password_sign_up;
    private EditText txt_passwordConfirm;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    boolean indi_code = true;

    public boolean isIndi_code() {
        return indi_code;
    }

    public void setIndi_code(boolean indi_code) {
        this.indi_code = indi_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        progressDialog = new ProgressDialog(this);
        btn_sign_up = findViewById(R.id.uyeSignUp);

        txt_company_name = findViewById(R.id.uyeCompanyName);
        txt_emp_code = findViewById(R.id.uyeEmpCode);
        txt_company_email_sign_up = findViewById(R.id.uyeEmail);
        txt_password_sign_up = findViewById(R.id.uyePassword);
        txt_passwordConfirm = findViewById(R.id.uyePasswordConfirm);

        //assign database instances
        mAuth = FirebaseAuth.getInstance();

        //set the listener for the click event
        btn_sign_up.setOnClickListener(this);
    }

    private boolean indi(final String empCode) {
        isIndi_code();
        final FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        db1.collection("Company")
                .whereEqualTo("empCode", empCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String temp_code = document.getString("empCode");
                                if(empCode.equalsIgnoreCase(temp_code)){
                                    setIndi_code(false);
                                    break;
                                }
                            }
                        } else {
                            setIndi_code(false);
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return isIndi_code();

    }

    private void registerUser() {
        //get user input
        String name = txt_company_name.getText().toString().trim();
        String empCode = txt_emp_code.getText().toString().trim();
        String email = txt_company_email_sign_up.getText().toString().trim();
        String password = txt_password_sign_up.getText().toString().trim();
        String confirm = txt_passwordConfirm.getText().toString().trim();

        if(!indi(empCode)){
            Toast.makeText(this, "This is already and Employer code", Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(name)) { //email is empty
            Toast.makeText(this, "Please enter your company name", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;

        }
        if (TextUtils.isEmpty(empCode)) { //email is empty
            Toast.makeText(this, "Please enter an employer code", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;

        }
        if (TextUtils.isEmpty(email)) { //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;

        }
        if (TextUtils.isEmpty(password)) { //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;
        }
        if (password.length() < 6) { //password is empty
            Toast.makeText(this, "Password must be 6 or more characters", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Your passwords do not match", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;
        }

        //if validations are okay
        //we will show a progressDialog as we create user account
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        //register user in firebase database
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateCompany.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {

                    Toast.makeText(CreateCompany.this, "Account Created", Toast.LENGTH_LONG).show();

                    newUser();
                    finish();

                    startActivity(new Intent(CreateCompany.this, MainActivity.class));
                } else {
                    Toast.makeText(CreateCompany.this, "Could not create account. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void newUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String name = txt_company_name.getText().toString().trim();
        String empCode = txt_emp_code.getText().toString().trim();
        String email = txt_company_email_sign_up.getText().toString().trim();

        NoteCompany note = new NoteCompany(name,empCode,email,true);

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!= null) {
            String mUid = user.getUid();
            db.collection("Company").document(mUid).set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateCompany.this, "Account Created", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateCompany.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btn_sign_up) {
            registerUser();
        }
    }
}