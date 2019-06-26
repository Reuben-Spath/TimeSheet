package com.example.timesheet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;
//information and total time need to be added together
public class EmployeeWeek extends FrontScreenEmployee {

    private Button back;

    private TextView Monday;
    private TextView Tuesday;
    private TextView Wednesday;
    private TextView Thursday;
    private TextView Friday;
    private TextView Saturday;
    private TextView Sunday;
    private TextView totalHours;
    private TextView weekEnding;

    private String name;


    private FirebaseAuth mAuth;

    private static final String TAG = "EmployeeWeek";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_week);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        back = findViewById(R.id.btBackEdit);
        weekEnding = findViewById(R.id.tvWeekEnding);

        totalHours = findViewById(R.id.tvTotalHours);

        Monday = findViewById(R.id.Monday);
        Tuesday = findViewById(R.id.Tuesday);
        Wednesday = findViewById(R.id.Wednesday);
        Thursday = findViewById(R.id.Thursday);
        Friday = findViewById(R.id.Friday);
        Saturday = findViewById(R.id.Saturday);
        Sunday = findViewById(R.id.Sunday);

        mAuth = FirebaseAuth.getInstance();

        Monday.setOnClickListener(editing);
        Tuesday.setOnClickListener(editing);
        Wednesday.setOnClickListener(editing);
        Thursday.setOnClickListener(editing);
        Friday.setOnClickListener(editing);
        Saturday.setOnClickListener(editing);
        Sunday.setOnClickListener(editing);
    }

    @Override
    public void onStart() {
        super.onStart();

        info();
        weekEnding.setText(weekEnding());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void info(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            String mUid = currentUser.getUid();
            setName(currentUser.getUid());


            db.collection("Users").document(mUid).collection(weekEnding())
                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            String data = "";
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);

                                noteEmployee.setDocumentId(documentSnapshot.getId());
                                String documentId = noteEmployee.getDocumentId();
                                String start = noteEmployee.getSignInN();
                                String finish = noteEmployee.getSignOutN();
                                String time = noteEmployee.getTimeStr();
                                boolean lunch = noteEmployee.getIfLunch();

                                String hadlunch = "No";

                                if (lunch) {
                                    hadlunch = "Yes";
                                }
                                if (start == null) {
                                    start = "Invalid";
                                }
                                if (finish == null) {
                                    finish = "Invalid";
                                }
                                if (time == null) {
                                    time = "Invalid";
                                }
                                if (!time.contains("hours") && !time.equalsIgnoreCase("Invalid")) {
                                    time = time + " hours";
                                }

                                Log.d(TAG, "onEvent: " + data);

                                if (documentId.equals("Monday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Monday.setText(data);
                                }
                                if (documentId.equals("Tuesday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Tuesday.setText(data);
                                }
                                if (documentId.equals("Wednesday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Wednesday.setText(data);
                                }
                                if (documentId.equals("Thursday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Thursday.setText(data);
                                }
                                if (documentId.equals("Friday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Friday.setText(data);
                                }
                                if (documentId.equals("Saturday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Saturday.setText(data);
                                }
                                if (documentId.equals("Sunday")) {
                                    data = "ID: " + documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Sunday.setText(data);
                                }

                            }
                        }
                    });
            db.collection("Users").document(mUid).collection(weekEnding())
                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }

                            float data = 0;
                            float time;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);

                                time = noteEmployee.getTimeInt();

                                data += time;
                                Log.d(TAG, "onEvent: " + data);

                            }

                            String finalTime = String.format(Locale.getDefault(), "%.2f hours", data);

                            totalHours.setText(finalTime);
                        }
                    });
        }
    }


    private View.OnClickListener editing = new View.OnClickListener() {
        public void onClick(View v) {

            Intent i = new Intent(EmployeeWeek.this, Edit.class);
            String id;

            switch (v.getId() /*to get clicked view id**/) {
                case R.id.Monday:

                    id = "Monday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Tuesday:

                    id = "Tuesday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Wednesday:

                    id = "Wednesday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Thursday:

                    id = "Thursday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Friday:

                    id = "Friday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Saturday:

                    id = "Saturday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Sunday:

                    id = "Sunday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}