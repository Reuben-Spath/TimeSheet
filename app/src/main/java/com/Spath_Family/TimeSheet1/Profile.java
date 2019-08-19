package com.Spath_Family.TimeSheet1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends FrontScreenEmployee {

    private Button back;
    private Button change;
    private Button logOut;
    private EditText employer_code_change;

    public static final String EMP_CODE = "empCode";
    private Boolean company;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setCompany(Objects.requireNonNull(getIntent().getExtras()).getBoolean("comp"));

        back = findViewById(R.id.btBackEdit);
        change = findViewById(R.id.btChange);
        logOut = findViewById(R.id.logOut);
        employer_code_change = findViewById(R.id.etEmployerCode);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employer_code_change.onEditorAction(EditorInfo.IME_ACTION_DONE);
                saveNote();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(Profile.this, "Signed Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void saveNote() {
        String empCode = employer_code_change.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> note = new HashMap<>();
        note.put(EMP_CODE, empCode);

        if (user != null) {
            if (getCompany()) {
                db.collection("Company").document(user.getUid()).set(note, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                                Toast.makeText(Profile.this, "Employer code saved!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
//                            Log.d("Failure to add", e.toString());
                            }
                        });
            } else {
                db.collection("Users").document(user.getUid()).set(note, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                                Toast.makeText(Profile.this, "Employer code saved!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
//                            Log.d("Failure to add", e.toString());
                            }
                        });
            }
        } else {
            Toast.makeText(Profile.this, "Your user cannot be verified", Toast.LENGTH_SHORT).show();
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

    public Boolean getCompany() {
        return company;
    }

    public void setCompany(Boolean company) {
        this.company = company;
    }
}