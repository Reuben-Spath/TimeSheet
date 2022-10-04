package com.Spath_Family.TimeSheet1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FrontScreenManager extends FragmentActivity {

    private static final String TAG = "FrontScreenManager";

    Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private TextView week_ending;
    private TextView mng_code;
    private Button send;
    private ImageView logoM;

    private LinearLayout myLinearLayout;

    private String letter;
    //    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> username = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_manager);

        mAuth = FirebaseAuth.getInstance();

        logoM = findViewById(R.id.logoM);
        week_ending = findViewById(R.id.tvWeekEnding);
        mng_code = findViewById(R.id.tvmgCode);

        myLinearLayout = findViewById(R.id.dynamic);

        String week_end = "Week Ending: " + weekEnding();
        week_ending.setText(week_end);
        empListner();
        employerCounter();

    }

    @Override
    protected void onStart() {
        super.onStart();
        logoM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent special_code_pass = new Intent(FrontScreenManager.this, Profile.class);
                special_code_pass.putExtra("comp", true);
                startActivity(special_code_pass);
            }
        });
//        list_employees();
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
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Toast.makeText(FrontScreenManager.this, "success", Toast.LENGTH_SHORT).show();
                        mng_code.setText(snapshot.getString("empCode"));
                    }
                }
            });
        }
    }

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
//        final ArrayList<String> names = new ArrayList<>();

//        final int N = 100; // total number of textviews to add

        if (currentUser != null) {
            String mUid = currentUser.getUid();

            db.collection("Company").document(mUid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            setLetter(documentSnapshot.getString("empCode"));


                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

                            db1.collection("Users")
                                    .whereEqualTo("empCode", getLetter())// <-- This line
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (final DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                                    final String name = document.getString("name");
                                                    final String id = document.getId();

//                                                    names.add(name);
                                                    username.add(id);

                                                    final TextView rowTextView = new TextView(getApplicationContext());
                                                    rowTextView.setTextSize(24);
                                                    rowTextView.setPadding(5, 10, 5, 10);
                                                    rowTextView.setTextColor(getResources().getColor(R.color.purple));
                                                    rowTextView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(FrontScreenManager.this, Employee.class);
//

                                                            i.putExtra("name", name);
                                                            i.putExtra("id", id);
                                                            startActivity(i);
                                                        }
                                                    });
                                                    String bullet = "\u2022" + name;
                                                    rowTextView.setText(bullet);

                                                    // add the textview to the linearlayout
                                                    myLinearLayout.addView(rowTextView);
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    //add takes in user input
    //searches for user in firestore
    //adds to week current data
//  User logs in
/*
Employer logs into site
Employer has limited employee options,
to add employee to system, employee clicks add button
pop up screen that takes input of the users unique code
search through fire base where unique code is equal to that is input
once accepted add to employers document and increase their counter number
name and counter increase
possible array of names
check if current status is breahed, if so return error
if() array size is <= status then add, else reply with error (Possible request update with link to wix)
once added recall creation and update UI with new employee list.

add people through their email address / name /


// Atomically remove a region from the "regions" array field.
 CompanyEmployeesRef.update("regions", FieldValue.arrayRemove(name));

 */

    public void add(String code) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth.getInstance().getCurrentUser();
        final String mUid = mAuth.getCurrentUser().getUid();

        final DocumentReference CompanyEmployeesRef = db.collection("cities").document("DC");

//        db.collection("Users").document(mUid)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        documentSnapshot.getString("Status");
//                        if ()
//                    }
//                });
//        db.collection("Users")
//                .whereEqualTo("empCode", code)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
//                                Map<String, Object> note = new HashMap<>();
//
//                                document.getString("empCode");
//                               String name =document.getString("name");
//// Atomically add a new region to the "regions" array field.
//                                CompanyEmployeesRef.update("Current Employees", FieldValue.arrayUnion(name));
//
//                                db1.collection("Users").document(mUid).collection(week_ending).document("Employees")
//                                        .set(note, SetOptions.merge());
//
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//    }

}

//    public void list_employees() {
//        final int N = 100; // total number of textviews to add
//
//        final TextView[] myTextViews = new TextView[N]; // create an empty array;
//
//        for (int i = 0; i < N; i++) {
//            // create a new textview
//            final TextView rowTextView = new TextView(this);
//            rowTextView.setTextSize(16);
//            rowTextView.setPadding(5, 10, 5, 10);
//            final String info_string = "This is " + i + "th TextView";
//            rowTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(FrontScreenManager.this, info_string, Toast.LENGTH_SHORT).show();
//                }
//            });
//            // set some properties of rowTextView or something
//            rowTextView.setText("This is row #" + i);
//
//            // add the textview to the linearlayout
//            myLinearLayout.addView(rowTextView);
//
//            // save a reference to the textview for later
//            myTextViews[i] = rowTextView;
//        }
//    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            mAuth.signOut();
            Toast.makeText(FrontScreenManager.this, "Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}