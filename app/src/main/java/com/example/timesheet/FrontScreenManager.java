package com.example.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FrontScreenManager extends AppCompatActivity {

    private static final String TAG = "FrontScreenManager";

    Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private TextView week_ending;
    private TextView mng_code;
    private Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_manager);

        mAuth = FirebaseAuth.getInstance();


        send = findViewById(R.id.btMngSend);

        employerCounter();

        week_ending = findViewById(R.id.tvWeekEnding);
        mng_code = findViewById(R.id.tvmgCode);

        week_ending.setText("Week Ending: " + weekEnding());
        empListner();
    }
//
//    public void openDialog() {
//        DialogOption dialogOption = new DialogOption();
//        dialogOption.show(getSupportFragmentManager(), "new Dialog");
//    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void empListner() {
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
                        mng_code.setText(snapshot.getString("empCode"));
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }

//    @Override
//    public void applyTexts(final String emp_code_add) {
//        final FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//
//
//            final FirebaseFirestore db1 = FirebaseFirestore.getInstance();
//            db1.collection("Users")
//                    .whereEqualTo("Employer Code", emp_code_add) // <-- This line
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                String mUid = currentUser.getUid();
//
//
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    String name = document.getString("name");
//
//
//                                    Map<String, Object> note = new HashMap<>();
//                                    note.put(name, emp_code_add);
//
//                                    db.collection("Company").document(mUid).collection("Employees").document("Employee 1")
//                                            .set(note, SetOptions.merge())
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Toast.makeText(FrontScreenManager.this, "Successful", Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(FrontScreenManager.this, "That Code does not exist", Toast.LENGTH_SHORT).show();
//                                                    Log.d("Failure to add", e.toString());
//                                                }
//                                            });
//
//                                }
//                            } else {
//                                Toast.makeText(FrontScreenManager.this, "That Code does not exist", Toast.LENGTH_SHORT).show();
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//        }
//    }

    public String weekEnding() {
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int days = Calendar.SUNDAY - weekday;
        if (days < 0) {
            // this will usually be the case since Calendar.SUNDAY is the smallest
            days += 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, days);

        DateFormat df = new SimpleDateFormat("dd-MMM", Locale.getDefault());

        return df.format(calendar.getTime());
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
                                                    LinearLayout ll = new LinearLayout(getApplicationContext());
                                                    TextView tv = new TextView(getApplicationContext());
//                                                    CheckBox cb = new CheckBox(getApplicationContext());
                                                    tv.setText(name);
                                                    tv.setId(counter);
                                                    tv.setTextSize(24);
                                                    tv.setTextColor(getResources().getColor(R.color.Blue_dark));
                                                    tv.setPadding(10, 15, 10, 15);
//                                                    cb.setId(counter);
//                                                    cb.setTextColor(getResources().getColor(R.color.Blue));
//                                                    lLayout.addView(ll);
//                                                    ll.setBackgroundColor(Color.parseColor("#135517"));
//                                                    ll.addView(cb);
//                                                    ll.addView(tv);
//                                                    lLayout.setOrientation(LinearLayout.VERTICAL);
//                                                    layout.addView(cb);
//                                                    layout.addView(tv);
//                                                    lLayout.addView(cb);
                                                    lLayout.addView(tv);

                                                    tv.setOnClickListener(new View.OnClickListener() {
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