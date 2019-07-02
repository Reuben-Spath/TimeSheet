package com.example.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Objects;

public class FrontScreenManager extends AppCompatActivity {

    private static final String TAG = "FrontScreenManager";

    Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private TextView week_ending;
    private TextView mng_code;
    private Button send;
    private ImageView logoM;

    private String letter;

//    private String trial;

//    ArrayList<String> mStringList= new ArrayList<String>();
//
//    public String getTrial() {
//        return trial;
//    }
//
//    public void setTrial(String trial) {
//        this.trial = trial;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_manager);


        mAuth = FirebaseAuth.getInstance();

        employerCounter();
//        send = findViewById(R.id.btMngSend);
        logoM = findViewById(R.id.logoM);
        week_ending = findViewById(R.id.tvWeekEnding);
        mng_code = findViewById(R.id.tvmgCode);


        String week_end = "Week Ending: " + weekEnding();
        week_ending.setText(week_end);
        empListner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logoM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FrontScreenManager.this, Profile.class);
                startActivity(i);
            }
        });
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
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            setLetter(documentSnapshot.getString("empCode"));


                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

                            db1.collection("Users")
                                    .whereEqualTo("Employer Code", getLetter()) // <-- This line
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int counter = 0;
                                                LinearLayout lLayout = findViewById(R.id.llLayout); // Root ViewGroup in which you want to add textviews

                                                for (final DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                    Log.d(TAG, "onComplete: " + document.getString("name"));

                                                    String name = document.getString("name");

                                                    counter++;

                                                    // Prepare textview object programmatically
                                                    TextView tv = new TextView(getApplicationContext());

                                                    tv.setText(name);
                                                    tv.setId(counter);
                                                    tv.setTextSize(24);
                                                    tv.setTextColor(getResources().getColor(R.color.Blue_dark));
                                                    tv.setPadding(10, 15, 10, 15);

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
                                    });
                        }
                    });
        }
    }
//    public void info(String empName){
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        if (currentUser != null) {
//
//            db.collection("Users").document(empName).collection(weekEnding())
//                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                            if (e != null) {
//                                return;
//                            }
//
//                            float tot_count = 0;
//                            float tot_time;
//                            String data = "";
//                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);
//
//                                noteEmployee.setDocumentId(documentSnapshot.getId());
//                                String documentId = noteEmployee.getDocumentId();
//                                String start = noteEmployee.getSignInN();
//                                String finish = noteEmployee.getSignOutN();
//                                String time = noteEmployee.getTimeStr();
//                                tot_time = noteEmployee.getTimeInt();
//                                boolean holiday = noteEmployee.isIfHoliday();
//                                boolean sick = noteEmployee.isIfSick();
//                                boolean lunch = noteEmployee.getIfLunch();
//                                String hadlunch = "No";
//
//                                if (lunch) {
//                                    hadlunch = "Yes";
//                                }
//                                if (start == null) {
//                                    start = "Invalid";
//                                }
//                                if (finish == null) {
//                                    finish = "Invalid";
//                                }
//                                if (time == null) {
//                                    time = "Invalid";
//                                }
//                                if (!time.contains("hours") && !time.equalsIgnoreCase("Invalid")) {
//                                    time = time + " hours";
//                                }
//
//                                tot_count += tot_time;
//
//                                Log.d(TAG, "onEvent: " + data);
//
//
//                                if (documentId.equals("Monday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Tuesday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Wednesday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Thursday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Friday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Saturday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Sunday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (!documentId.isEmpty()){
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    } else{
//                                        setTrial(getTrial()+" \nDid not work on Sunday");
//                                    }
//                                }
//                            }
//                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", tot_count);
//
//                            setTotal(finalTime);
//                            totalHours.setText(finalTime);
//                        }
//                    });
//        }
//    }
//    public void send_all() {
//
//        String info_subject = weekEnding()+" "+getUserId();
//        if (getMon() == null) {
//            setMon("Did not work on Monday");
//        }
//        if (getTues() == null) {
//            setTues("Did not work on Tuesday");
//        }
//        if (getWed() == null) {
//            setWed("Did not work on Wednesday");
//        }
//        if (getThurs() == null) {
//            setThurs("Did not work on Thursday");
//        }
//        if (getFri() == null) {
//            setFri("Did not work on Friday");
//        }
//        if (getSat() == null) {
//            setSat("Did not work on Saturday");
//        }
//        if (getSun() == null) {
//            setSun("Did not work on Sunday");
//        }
//
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT,info_subject);
//        shareIntent.putExtra(Intent.EXTRA_TEXT,  getTotal() + "\n\n" + getMon() + "\n" + getTues() + "\n" + getWed() + "\n" + getThurs() + "\n" + getFri() + "\n" + getSat() + "\n" + getSun());
//        shareIntent.setType("text/*");
//        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
//    }
//

//            db.collection("Company").document(mUid)
//                    .get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(final DocumentSnapshot documentSnapshot) {
//
//                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
//                            String code = documentSnapshot.getString("empCode");
//
//                            db1.collection("Users")
//                                    .whereEqualTo("Employer Code", code) // <-- This line
//                                    .get()
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                int counter = 0;
//                                                LinearLayout lLayout = findViewById(R.id.llLayout); // Root ViewGroup in which you want to add textviews
//
//                                                for (final DocumentSnapshot document : task.getResult()) {
//                                                    Log.d(TAG, document.getId() + " => " + document.getData());
//
//                                                    Log.d(TAG, "onComplete: " + document.getString("name"));
//
//                                                    String name = document.getString("name");
//
//                                                    counter++;
//
//                                                    // Prepare textview object programmatically
//                                                    TextView tv = new TextView(getApplicationContext());
//
//                                                    tv.setText(name);
//                                                    tv.setId(counter);
//                                                    tv.setTextSize(24);
//                                                    tv.setTextColor(getResources().getColor(R.color.Blue_dark));
//                                                    tv.setPadding(10, 15, 10, 15);
//
//                                                    lLayout.addView(tv);
//
//                                                    tv.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            String id = document.getId();
//                                                            String userName = document.getString("name");
//
//                                                            Intent i = new Intent(FrontScreenManager.this, Employee.class);
//
//                                                            i.putExtra("name", userName);
//                                                            i.putExtra("id", id);
//                                                            startActivity(i);
//                                                        }
//                                                    });
//                                                }
//                                            } else {
//                                                Log.d(TAG, "Error getting documents: ", task.getException());
//                                            }
//                                        }
//                                    })
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                            Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.getDocuments());
//                                        }
//                                    });
//                        }
//                    });
//}
//        }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}