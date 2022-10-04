package com.Spath_Family.TimeSheet1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Create extends AppCompatActivity {

    private static final String TAG = "CreateEmployee";

    private ImageView imageView;

    private Button btn_signup;

    private EditText txt_email_signup;
    private EditText txt_password_signup;
    private EditText txt_passwordConfirm;
    private EditText txt_employer_name;
    private EditText txt_emp_code;

    private CheckBox employer;

    private boolean company = false;

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }

    public boolean isIndi_code() {
        return indi_code;
    }

    public void setIndi_code(boolean indi_code) {
        this.indi_code = indi_code;
    }

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;


    boolean indi_code = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressDialog = new ProgressDialog(this);
        btn_signup = findViewById(R.id.CreateSignUp);
        txt_email_signup = findViewById(R.id.CreateEmail);
        txt_password_signup = findViewById(R.id.CreatePassowrd);
        txt_passwordConfirm = findViewById(R.id.CreatePassword);
        txt_employer_name = findViewById(R.id.CreateName);
        txt_emp_code = findViewById(R.id.CreateEmpCode);
        employer = findViewById(R.id.employer);

        imageView = findViewById(R.id.infoCreate);

        mAuth = FirebaseAuth.getInstance();


//      Sets the company status to true or false depending on the checkbox status along with the input information.
        employer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (employer.isChecked()) {
                    setCompany(true);
                    infoSetting(isCompany());
                } else {
                    setCompany(false);
                    infoSetting(isCompany());
                }
            }
        });
//      Calls either registerCompany() or registerEmployee() depending on the state of the employer checkbox
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    /**
     * Created to change the hint text provided by the textviews in the activty_create which alertnates depending on the boolean passed in
     *
     * @param tick
     */
    public void infoSetting(boolean tick) {
        String company_name = "Company Name";
        String employer_code = "The Companies Unique Code";
        String company_email = "The Company Email Address";

        String employee_name = "Your First and Last Name";
        String employee_code = "Your Unique Code";
        String email_address = "Your email address";

        if (tick) {
            txt_employer_name.setHint(company_name);
            txt_emp_code.setHint(employer_code);
            txt_email_signup.setHint(company_email);
        } else {
            txt_employer_name.setHint(employee_name);
            txt_emp_code.setHint(employee_code);
            txt_email_signup.setHint(email_address);
        }
    }

    /*
    Scan through a list of employer codes and return true if aren't any codes that are idnetical to the unique code provided in emp code
    start, take input from user
    search through firestore user list and r
    Scan through users, if a name is similar return false
    if false exit call
    toast to
     */
//    public boolean empCode(final String code) {
//        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
//        db1.collection("Users")
//                .whereEqualTo("empCode", code)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                String temp = document.getString("empCode");
//                                if (code.equalsIgnoreCase(temp)) {
//                                    setIndi_code(false);
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//    }


    public void register() {
        //get user input from text inputs
        final String name = txt_employer_name.getText().toString().trim();
        final String empCode = txt_emp_code.getText().toString().trim();
        String email = txt_email_signup.getText().toString().trim();
        String password = txt_password_signup.getText().toString().trim();
        String confirm = txt_passwordConfirm.getText().toString().trim();


//        if(isIndi_code()){
//            Toast.makeText(this, "Your unique code is already in use, please choose another ", Toast.LENGTH_SHORT).show();
//            return;
//        }
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
        if (TextUtils.isEmpty(empCode)) { //email is empty
            Toast.makeText(this, "Please enter an employer code", Toast.LENGTH_SHORT).show();
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
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Create.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(Create.this, "Account Created", Toast.LENGTH_LONG).show();
                    newUser();
//                    finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Create.this, "This account is already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Create.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
        Map<String, Object> user = new HashMap<>();

        if (newUser != null) {
            if (isCompany()) {
                user.put("Status", 3);
                user.put("Company", true);
                user.put("email", email);
                user.put("name", name);
                user.put("empCode", empCode);
                user.put("Current Employees", Arrays.asList("1", "2", "3"));

            } else {
                user.put("email", email);
                user.put("name", name);
                user.put("empCode", empCode);
            }
            String mUid = newUser.getUid();

            db.collection("Users").document(mUid).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Create.this, "Account Created", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Create.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
