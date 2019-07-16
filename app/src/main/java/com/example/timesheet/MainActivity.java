package com.example.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailField = findViewById(R.id.etEmpCreateName);
        mPasswordField = findViewById(R.id.etPassword);


        findViewById(R.id.btLogin).setOnClickListener(this);
        findViewById(R.id.tvCreate).setOnClickListener(this);
        findViewById(R.id.tvPasswordRetrieval).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

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
        Log.d(TAG, "signIn:" + email);
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
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            snapshot.getBoolean("company");
                            if( snapshot.getBoolean("company")!=null){
                                goToFrontScreenManager();
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
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
//            Intent create_pass = new Intent(MainActivity.this, CreateChoice.class);
//            startActivity(create_pass);
            Intent create_pass = new Intent(MainActivity.this, gridLayout.class);
            startActivity(create_pass);
        } else if (i == R.id.tvPasswordRetrieval) {
            Intent retrieval = new Intent(MainActivity.this, PasswordRetrieval.class);
            startActivity(retrieval);

        }
    }
}
//Delete users database when their account is deleted.
