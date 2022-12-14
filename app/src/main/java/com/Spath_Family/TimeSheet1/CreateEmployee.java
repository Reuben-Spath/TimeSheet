package com.Spath_Family.TimeSheet1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CreateEmployee extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateEmployee";

    private ImageView imageView;
    private Button btn_signup;
    private EditText txt_email_signup;
    private EditText txt_password_signup;
    private EditText txt_passwordConfirm;
    private EditText txt_employer_name;
    private EditText txt_emp_code;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

//    boolean indi_code = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_employee);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressDialog = new ProgressDialog(this);
        btn_signup = findViewById(R.id.uyeSignUp);
        txt_email_signup = findViewById(R.id.CreateEmail);
        txt_password_signup = findViewById(R.id.CreatePassowrd);
        txt_passwordConfirm = findViewById(R.id.CreatePassword);
        txt_employer_name = findViewById(R.id.CreateName);
        txt_emp_code = findViewById(R.id.CreateEmpCode);

        imageView = findViewById(R.id.infoCreate);

        //assign database instances
        mAuth = FirebaseAuth.getInstance();

        //set the listener for the click event
        btn_signup.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    private void registerUser() {
        //get user input
        final String name = txt_employer_name.getText().toString().trim();
        final String empCode = txt_emp_code.getText().toString().trim();
        String email = txt_email_signup.getText().toString().trim();
        String password = txt_password_signup.getText().toString().trim();
        String confirm = txt_passwordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { //email is empty
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
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
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateEmployee.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser(); //You Firebase user
                    Toast.makeText(CreateEmployee.this, "Account Created", Toast.LENGTH_LONG).show();

                    if (user != null) {
                        EmpDoc empDoc = new EmpDoc(name, empCode);
                        newUser();
                        finish();
                        startActivity(new Intent(CreateEmployee.this, MainActivity.class));
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(CreateEmployee.this, "This account is already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateEmployee.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void newUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = txt_email_signup.getText().toString().trim();
        String name = txt_employer_name.getText().toString().trim();
        String empCode = txt_emp_code.getText().toString().trim();
        FirebaseUser newUser = mAuth.getCurrentUser();

        if (newUser != null) {
            Map<String, Object> user = new HashMap<>();
            user.put("email", email);
            user.put("name", name);
            user.put("empCode", empCode);

            String mUid = newUser.getUid();

            db.collection("Users").document(mUid).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateEmployee.this, "Account Created", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateEmployee.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btn_signup) {
            registerUser();
        }
        if (view == imageView) {
            Toast.makeText(this, "This code lets your employer see your TimeSheet", Toast.LENGTH_SHORT).show();
        }
    }
}