package com.example.timesheet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Employee extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();

    private static final String TAG = "Employee";

    private FirebaseAuth mAuth;

    //    private GridLayout layout;
    private TextView totalHours;

    private Button Back;
    private Button send;

    private String nameId;
    private String userId;

    private String mon;
    private String tues;
    private String wed;
    private String thurs;
    private String fri;
    private String sat;
    private String sun;
    private String total;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return nameId;
    }

    public void setName(String name) {
        this.nameId = name;
    }

    private View.OnClickListener editing = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(Employee.this, Edit.class);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Back = findViewById(R.id.btBack1);
        send = findViewById(R.id.btSend);

        TextView employee = findViewById(R.id.tvWeekEnding1);
        totalHours = findViewById(R.id.tvTotalHours1);

        Intent i = getIntent();
        setName(i.getStringExtra("id"));
        setUserId(i.getStringExtra("name"));
        employee.setText(i.getStringExtra("name"));

        TextView monday = findViewById(R.id.Monday);
        TextView tuesday = findViewById(R.id.Tuesday);
        TextView wednesday = findViewById(R.id.Wednesday);
        TextView thursday = findViewById(R.id.Thursday);
        TextView friday = findViewById(R.id.Friday);
        TextView saturday = findViewById(R.id.Saturday);
        TextView sunday = findViewById(R.id.Sunday);

        mAuth = FirebaseAuth.getInstance();

        monday.setOnClickListener(editing);
        tuesday.setOnClickListener(editing);
        wednesday.setOnClickListener(editing);
        thursday.setOnClickListener(editing);
        friday.setOnClickListener(editing);
        saturday.setOnClickListener(editing);
        sunday.setOnClickListener(editing);

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
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        String userName = i.getStringExtra("id");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUser != null) {

            db.collection("Users").document(userName).collection(weekEnding())
                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }

                            float tot_count = 0;
                            float tot_time;
                            String data = "";
                            String hours;

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);

                                noteEmployee.setDocumentId(documentSnapshot.getId());
                                String documentId = noteEmployee.getDocumentId();
                                String start = noteEmployee.getSignInN();
                                String finish = noteEmployee.getSignOutN();
                                String time = noteEmployee.getTimeStr();
                                tot_time = noteEmployee.getTimeInt();
                                boolean holiday = noteEmployee.isIfHoliday();
                                boolean sick = noteEmployee.isIfSick();

                                tot_count += tot_time;

                                if (holiday || sick) {
                                    tot_count -= tot_time;
                                }
                                if (tot_time != 0.0) {
                                    hours = tot_time + " hrs";
                                } else hours = "";

                                if (start == null) {
                                    start = "";
                                }
                                if (finish == null) {
                                    finish = "";
                                }

                                Log.d(TAG, "onEvent: " + data);


                                if (documentId.equals("Monday")) {
                                    if (holiday) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Holiday";
                                        montot.setText(data);
                                        setMon(data);
                                    } else if (sick) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Sick";
                                        montot.setText(data);
                                        setMon(data);
                                    } else {
                                        monst.setText(start);
                                        monfn.setText(finish);
                                        montot.setText(hours);
                                        setMon(String.format("%-12s" + start + " " + finish + " " + time, "Monday:"));
                                    }
                                }
                                if (documentId.equals("Tuesday")) {
                                    if (holiday) {
                                        tuesst.setText("");
                                        tuesfn.setText("");
                                        data = "Holiday";
                                        tuestot.setText(data);
                                        setTues(data);
                                    } else if (sick) {
                                        tuesst.setText("");
                                        tuesfn.setText("");
                                        data = "Sick";
                                        tuestot.setText(data);
                                        setTues(data);
                                    } else {
                                        tuesst.setText(start);
                                        tuesfn.setText(finish);
                                        tuestot.setText(hours);
                                        setTues(String.format("%-12s" + start + " " + finish + " " + time, "Tuesday:"));
                                    }
                                }
                                if (documentId.equals("Wednesday")) {
                                    if (holiday) {
                                        wedst.setText("");
                                        wedfn.setText("");
                                        data = "Holiday";
                                        wedtot.setText(data);
                                        setWed(data);
                                    } else if (sick) {
                                        wedst.setText("");
                                        wedfn.setText("");
                                        data = "Sick";
                                        wedtot.setText(data);
                                        setWed(data);
                                    } else {
                                        wedst.setText(start);
                                        wedfn.setText(finish);
                                        wedtot.setText(hours);
                                        setWed(String.format("%-12s" + start + " " + finish + " " + time, "Wednesday:"));
                                    }
                                }
                                if (documentId.equals("Thursday")) {
                                    if (holiday) {
                                        thursst.setText("");
                                        thursfn.setText("");
                                        data = "Holiday";
                                        thurstot.setText(data);
                                        setThurs(data);
                                    } else if (sick) {
                                        thursst.setText("");
                                        thursfn.setText("");
                                        data = "Sick";
                                        thurstot.setText(data);
                                        setThurs(data);
                                    } else {
                                        thursst.setText(start);
                                        thursfn.setText(finish);
                                        thurstot.setText(hours);
                                        setThurs(String.format("%-12s" + start + " " + finish + " " + time, "Thursday:"));
                                    }
                                }
                                if (documentId.equals("Friday")) {
                                    if (holiday) {
                                        frist.setText("");
                                        frifn.setText("");
                                        data = "Holiday";
                                        fritot.setText(data);
                                        setFri(data);
                                    } else if (sick) {
                                        frist.setText("");
                                        frifn.setText("");
                                        data = "Sick";
                                        fritot.setText(data);
                                        setFri(data);
                                    } else {
                                        frist.setText(start);
                                        frifn.setText(finish);
                                        fritot.setText(hours);
                                        setFri(String.format("%-12s" + start + " " + finish + " " + time, "Friday:"));
                                    }
                                }
                                if (documentId.equals("Saturday")) {
                                    if (holiday) {
                                        satst.setText("");
                                        satfn.setText("");
                                        data = "Holiday";
                                        sattot.setText(data);
                                        setSat(data);
                                    } else if (sick) {
                                        satst.setText("");
                                        satfn.setText("");
                                        data = "Sick";
                                        sattot.setText(data);
                                        setSat(data);
                                    } else {
                                        satst.setText(start);
                                        satfn.setText(finish);
                                        sattot.setText(hours);
                                        setSat(String.format("%-12s" + start + " " + finish + " " + time, "Saturday:"));
                                    }
                                }
                                if (documentId.equals("Sunday")) {
                                    if (holiday) {
                                        sunst.setText("");
                                        sunfn.setText("");
                                        data = "Holiday";
                                        suntot.setText(data);
                                        setSun(data);
                                    } else if (sick) {
                                        sunst.setText("");
                                        sunfn.setText("");
                                        data = "Sick";
                                        suntot.setText(data);
                                        setSun(data);
                                    } else {
                                        sunst.setText(start);
                                        sunfn.setText(finish);
                                        suntot.setText(hours);
                                        setSun(String.format("%-12s" + start + " " + finish + " " + time, "Sunday:"));
                                    }
                                }
                            }
                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", tot_count);

                            setTotal(finalTime);
                            totalHours.setText(finalTime);
                        }
                    });
        }
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }


    public String weekEnding() {
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int days = Calendar.SUNDAY - weekday;
        if (days < 0) {
            // this will usually be the case since Calendar.SUNDAY is the smallest
            days += 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, days);

        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return df.format(calendar.getTime());
    }

    public void send() {

//        layout = findViewById(R.id.gridLayout);

        String info_subject = weekEnding() + " " + getUserId();
        if (getMon() == null) {
            setMon("\nDid not work on Monday");
        }
        if (getTues() == null) {
            setTues("\nDid not work on Tuesday");
        }
        if (getWed() == null) {
            setWed("\nDid not work on Wednesday");
        }
        if (getThurs() == null) {
            setThurs("\nDid not work on Thursday");
        }
        if (getFri() == null) {
            setFri("\nDid not work on Friday");
        }
        if (getSat() == null) {
            setSat("\nDid not work on Saturday");
        }
        if (getSun() == null) {
            setSun("\nDid not work on Sunday");
        }


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, info_subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT,

                formatted_day("Monday") + "\n" + formatted_day("Tuesday") + "\n" +
                        formatted_day("Wednesday") + "\n" + formatted_day("Thursday") + "\n" +
                        formatted_day("Friday") + "\n" + formatted_day("Saturday") + "\n" + formatted_day("Sunday")
        );
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
//        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
//        getTotal() + "\n\n           Start:     Finish:     Total:\n\n"
//                + getMon() + "\n"
//                + getTues() + "\n"
//                + getWed() + "\n"
//                + getThurs() + "\n"
//                + getFri() + "\n"
//                + getSat() + "\n"
//                + getSun())
    }

    public String formatted_day(String day) {
        return String.format("%.12s |", day + ":");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTues() {
        return tues;
    }

    public void setTues(String tues) {
        this.tues = tues;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThurs() {
        return thurs;
    }

    public void setThurs(String thurs) {
        this.thurs = thurs;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }


}
