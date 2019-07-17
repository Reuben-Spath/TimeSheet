package com.example.timesheet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

//information and total time need to be added together
public class EmployeeWeek extends FrontScreenEmployee {

    private Button back;

    private String dialogDate;

    private TextView EmpCode;

//    private TextView Monday;
//    private TextView Tuesday;
//    private TextView Wednesday;
//    private TextView Thursday;
//    private TextView Friday;
//    private TextView Saturday;
//    private TextView Sunday;

    private TextView Monday;
    private TextView Tuesday;
    private TextView Wednesday;
    private TextView Thursday;
    private TextView Friday;
    private TextView Saturday;
    private TextView Sunday;

    private TextView totalHours;
    private TextView weekEnding;

    private TextView monst;
    private TextView tuesst;
    private TextView wedst;
    private TextView thursst;
    private TextView frist;
    private TextView satst;
    private TextView sunst;

    private TextView monfn;
    private TextView tuesfn;
    private TextView wedfn;
    private TextView thursfn;
    private TextView frifn;
    private TextView satfn;
    private TextView sunfn;

    private TextView montot;
    private TextView tuestot;
    private TextView wedtot;
    private TextView thurstot;
    private TextView fritot;
    private TextView sattot;
    private TextView suntot;

    private String name;
    private String week_header;

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

        EmpCode = findViewById(R.id.tvEmpCode);

        week_header = "Week Ending:\n" + weekEnding();

        back = findViewById(R.id.btBackEdit);

        weekEnding = findViewById(R.id.tvWeekEnding);
        totalHours = findViewById(R.id.tvTotalHours);

        mAuth = FirebaseAuth.getInstance();


        Monday = findViewById(R.id.Monday);
        Tuesday = findViewById(R.id.Tuesday);
        Wednesday = findViewById(R.id.Wednesday);
        Thursday = findViewById(R.id.Thursday);
        Friday = findViewById(R.id.Friday);
        Saturday = findViewById(R.id.Saturday);
        Sunday = findViewById(R.id.Sunday);

        Monday.setOnClickListener(editing);
        Tuesday.setOnClickListener(editing);
        Wednesday.setOnClickListener(editing);
        Thursday.setOnClickListener(editing);
        Friday.setOnClickListener(editing);
        Saturday.setOnClickListener(editing);
        Sunday.setOnClickListener(editing);

        monst = findViewById(R.id.monst);
        tuesst = findViewById(R.id.tuesst);
        wedst = findViewById(R.id.wedst);
        thursst = findViewById(R.id.thurst);
        frist = findViewById(R.id.frist);
        satst = findViewById(R.id.satst);
        sunst = findViewById(R.id.sunst);

        monfn = findViewById(R.id.monfn);
        tuesfn = findViewById(R.id.tuesfn);
        wedfn = findViewById(R.id.wedfn);
        thursfn = findViewById(R.id.thurfn);
        frifn = findViewById(R.id.frifn);
        satfn = findViewById(R.id.satfn);
        sunfn = findViewById(R.id.sunfn);

        montot = findViewById(R.id.montot);
        tuestot = findViewById(R.id.tuestot);
        wedtot = findViewById(R.id.wedtot);
        thurstot = findViewById(R.id.thurtot);
        fritot = findViewById(R.id.fritot);
        sattot = findViewById(R.id.sattot);
        suntot = findViewById(R.id.suntot);
    }


    @Override
    public void onStart() {
        super.onStart();

        empListner();
        info();

        weekEnding.setText(week_header);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void empListner() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference docRef = db.collection("Users").document(user.getUid());
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
                        EmpCode.setText(snapshot.getString("Employer Code"));
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }

    public void info() {
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
                                boolean holiday = noteEmployee.isIfHoliday();
                                boolean sick = noteEmployee.isIfSick();
//                                boolean lunch = noteEmployee.getIfLunch();
//
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


                                Log.d(TAG, "onEvent: " + data);

                                if (documentId.equals("Monday")) {
                                    if (holiday) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Holiday";
                                        montot.setText(data);

                                    } else if (sick) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Sick";
                                        montot.setText(data);

                                    } else {
                                        monst.setText(start);
                                        monfn.setText(finish);
                                        montot.setText(time);
                                    }
                                }
                                if (documentId.equals("Tuesday")) {
                                    if (holiday) {
                                        tuesst.setText("");
                                        tuesfn.setText("");
                                        data = "Holiday";
                                        tuestot.setText(data);
                                    } else if (sick) {
                                        tuesst.setText("");
                                        tuesfn.setText("");
                                        data = "Sick";
                                        tuestot.setText(data);
                                    } else {
                                        tuesst.setText(start);
                                        tuesfn.setText(finish);
                                        tuestot.setText(time);
                                    }
                                }
                                if (documentId.equals("Wednesday")) {
                                    if (holiday) {
                                        wedst.setText("");
                                        wedfn.setText("");
                                        data = "Holiday";
                                        wedtot.setText(data);
                                    } else if (sick) {
                                        wedst.setText("");
                                        wedfn.setText("");
                                        data = "Sick";
                                        wedtot.setText(data);
                                    } else {
                                        wedst.setText(start);
                                        wedfn.setText(finish);
                                        wedtot.setText(time);
                                    }
                                }
                                if (documentId.equals("Thursday")) {
                                    if (holiday) {
                                        thursst.setText("");
                                        thursfn.setText("");
                                        data = "Holiday";
                                        thurstot.setText(data);
                                    } else if (sick) {
                                        thursst.setText("");
                                        thursfn.setText("");
                                        data = "Sick";
                                        thurstot.setText(data);
                                    } else {
                                        thursst.setText(start);
                                        thursfn.setText(finish);
                                        thurstot.setText(time);
                                    }
                                }
                                if (documentId.equals("Friday")) {
                                    if (holiday) {
                                        frist.setText("");
                                        frifn.setText("");
                                        data = "Holiday";
                                        fritot.setText(data);
                                    } else if (sick) {
                                        frist.setText("");
                                        frifn.setText("");
                                        data = "Sick";
                                        fritot.setText(data);
                                    } else {
                                        frist.setText(start);
                                        frifn.setText(finish);
                                        fritot.setText(time);
                                    }
                                }
                                if (documentId.equals("Saturday")) {
                                    if (holiday) {
                                        satst.setText("");
                                        satfn.setText("");
                                        data = "Holiday";
                                        sattot.setText(data);
                                    } else if (sick) {
                                        satst.setText("");
                                        satfn.setText("");
                                        data = "Sick";
                                        sattot.setText(data);
                                    } else {
                                        satst.setText(start);
                                        satfn.setText(finish);
                                        sattot.setText(time);
                                    }
                                }
                                if (documentId.equals("Sunday")) {
                                    if (holiday) {
                                        sunst.setText("");
                                        sunfn.setText("");
                                        data = "Holiday";
                                        suntot.setText(data);
                                    } else if (sick) {
                                        sunst.setText("");
                                        sunfn.setText("");
                                        data = "Sick";
                                        suntot.setText(data);
                                    } else {
                                        sunst.setText(start);
                                        sunfn.setText(finish);
                                        suntot.setText(time);
                                    }
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
                            boolean holiday;
                            boolean sick;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);

                                time = noteEmployee.getTimeInt();

                                holiday = noteEmployee.isIfHoliday();
                                sick = noteEmployee.isIfSick();

                                data += time;

                                if (holiday || sick) {
                                    data -= time;
                                }

                                Log.d(TAG, "onEvent: " + data);

                            }

                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", data);

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

// for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//         NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);
//
//        noteEmployee.setDocumentId(documentSnapshot.getId());
//        String documentId = noteEmployee.getDocumentId();
//        String start = noteEmployee.getSignInN();
//        String finish = noteEmployee.getSignOutN();
//        String time = noteEmployee.getTimeStr();
//        boolean holiday = noteEmployee.isIfHoliday();
//        boolean sick = noteEmployee.isIfSick();
//        boolean lunch = noteEmployee.getIfLunch();
//
//        String hadlunch = "No";
//
//        if (lunch) {
//        hadlunch = "Yes";
//        }
//        if (start == null) {
//        start = "Invalid";
//        }
//        if (finish == null) {
//        finish = "Invalid";
//        }
//        if (time == null) {
//        time = "Invalid";
//        }
//        if (!time.contains("hours") && !time.equalsIgnoreCase("Invalid")) {
//        time = time + " hours";
//        }
//
//
//        Log.d(TAG, "onEvent: " + data);
//
//        if (documentId.equals("Monday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Monday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Monday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Monday.setText(data);
//        }
//        }
//        if (documentId.equals("Tuesday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Tuesday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Tuesday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Tuesday.setText(data);
//        }
//        }
//        if (documentId.equals("Wednesday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Wednesday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Wednesday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Wednesday.setText(data);
//        }
//        }
//        if (documentId.equals("Thursday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Thursday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Thursday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Thursday.setText(data);
//        }
//        }
//        if (documentId.equals("Friday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Friday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Friday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Friday.setText(data);
//        }
//        }
//        if (documentId.equals("Saturday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Saturday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Saturday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Saturday.setText(data);
//        }
//        }
//        if (documentId.equals("Sunday")) {
//        if (holiday) {
//        data = documentId + "\nHoliday";
//        Sunday.setText(data);
//        } else if (sick) {
//
//        data = documentId + "\nSick";
//        Sunday.setText(data);
//        } else {
//        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//        Sunday.setText(data);
//        }
//        }
//
//        }
//