package com.example.timesheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FrontScreenManager extends AppCompatActivity implements DialogOption.ExampleDialogListener {

    private static final String TAG = "FrontScreenManager";

    private FirebaseAuth mAuth;
    private Button add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_manager);

        mAuth = FirebaseAuth.getInstance();

        add = findViewById(R.id.btAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    public void openDialog() {
        DialogOption dialogOption = new DialogOption();
        dialogOption.show(getSupportFragmentManager(), "new Dialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void applyTexts(final String emp_code_add) {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {


            final FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("Users")
                    .whereEqualTo("Employer Code", emp_code_add) // <-- This line
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String mUid = currentUser.getUid();


                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String name = document.getString("name");


                                    Map<String, Object> note = new HashMap<>();
                                    note.put(name, emp_code_add);

                                    db.collection("Company").document(mUid).collection("Employees").document("Employee 1")
                                            .set(note, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(FrontScreenManager.this, "Successful", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(FrontScreenManager.this, "That Code does not exist", Toast.LENGTH_SHORT).show();
                                                    Log.d("Failure to add", e.toString());
                                                }
                                            });

                                }
                            } else {
                                Toast.makeText(FrontScreenManager.this, "That Code does not exist", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void employerCounter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            String mUid = currentUser.getUid();

            db.collection("Company").document(mUid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(final DocumentSnapshot documentSnapshot) {

                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                            String code = documentSnapshot.getString("empCode");

                            db1.collection("Users")
                                    .whereEqualTo("Employer Code", code) // <-- This line
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int counter = 0;
                                                LinearLayout lLayout = findViewById(R.id.llLayout); // Root ViewGroup in which you want to add textviews

                                                for (final DocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                                    Log.d(TAG, "onComplete: " + document.getString("name"));

                                                    String name = document.getString("name");

                                                    counter++;

                                                    // Prepare textview object programmatically
                                                    TextView tv = new TextView(getApplicationContext());
                                                    CheckBox cb = new CheckBox(getApplicationContext());
                                                    Button bt = new Button(getApplicationContext());

                                                    String names = counter + ". " + name;

                                                    tv.setText(names);
                                                    tv.setId(counter);
                                                    tv.setTextSize(30);
                                                    tv.setPadding(100, 20, 10, 20);
                                                    tv.setTextColor(getResources().getColor(R.color.Blue));

                                                    cb.setId(counter);
                                                    cb.setTextColor(getResources().getColor(R.color.Blue));

                                                    tv.setGravity(0);
                                                    cb.setGravity(300);
                                                    lLayout.addView(cb);
                                                    lLayout.addView(tv);
                                                    cb.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String id = document.getId();
                                                            String userName = document.getString("name");

                                                            Intent i = new Intent(FrontScreenManager.this, Employee.class);

                                                            i.putExtra("name", userName);
                                                            i.putExtra("id", id);
                                                            startActivity(i);
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.getDocuments());
                                        }
                                    });
                        }
                    });
        }
    }
}