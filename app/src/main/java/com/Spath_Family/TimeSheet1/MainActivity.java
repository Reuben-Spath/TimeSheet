package com.Spath_Family.TimeSheet1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mEmailField = findViewById(R.id.CreateName);
        mPasswordField = findViewById(R.id.etPassword);

        findViewById(R.id.btLogin).setOnClickListener(this);
        findViewById(R.id.tvCreate).setOnClickListener(this);
        findViewById(R.id.tvPasswordRetrieval).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference docRef = db.collection("Company").document(user.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        snapshot.getBoolean("company");
                        if (snapshot.getBoolean("company") != null) {
                            goToFrontScreenManager();
                            Toast.makeText(MainActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        goToFrontScreen();
                        Toast.makeText(MainActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void goToFrontScreen() {
        Intent i = new Intent(this, FrontScreenEmployee.class);
        startActivity(i);
    }

    public void goToFrontScreenManager() {
        Intent i = new Intent(this, FrontScreenManager.class);
        startActivity(i);
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                tester();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "Your email and password don't match up.", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {

                                String errorCode =
                                        ((FirebaseAuthInvalidUserException) task.getException()).getErrorCode();

                                if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                    Toast.makeText(MainActivity.this, "No matching account found", Toast.LENGTH_SHORT).show();
                                } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                                    Toast.makeText(MainActivity.this, "User account has been disabled", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        hideProgressDialog();
                    }
                });
    }

    public void tester(){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                final DocumentReference docRef = db.collection("Company").document(user.getUid());
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            snapshot.getBoolean("company");
                            if( snapshot.getBoolean("company")!=null){
                                goToFrontScreenManager();
                            }
                        } else {
                            goToFrontScreen();
                        }
                    }
                });
            }
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btLogin) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            mEmailField.onEditorAction(EditorInfo.IME_ACTION_DONE);
            mPasswordField.onEditorAction(EditorInfo.IME_ACTION_DONE);
        } else if (i == R.id.tvCreate) {
            Intent create_pass = new Intent(MainActivity.this, Create.class);
            startActivity(create_pass);
        } else if (i == R.id.tvPasswordRetrieval) {
            Intent retrieval = new Intent(MainActivity.this, PasswordRetrieval.class);
            startActivity(retrieval);
        }
    }
}
//Delete users database when their account is deleted.
