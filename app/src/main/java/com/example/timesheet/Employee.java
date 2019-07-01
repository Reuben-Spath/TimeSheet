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

    private TextView Monday;
    private TextView Tuesday;
    private TextView Wednesday;
    private TextView Thursday;
    private TextView Friday;
    private TextView Saturday;
    private TextView Sunday;
    private TextView totalHours;
    private TextView Employee;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Back = findViewById(R.id.btBack1);
        send = findViewById(R.id.btSend);

        Employee = findViewById(R.id.tvWeekEnding1);
        totalHours = findViewById(R.id.tvTotalHours1);

        Intent i = getIntent();
        setName(i.getStringExtra("id"));
        setUserId(i.getStringExtra("name"));
        Employee.setText(i.getStringExtra("name"));

        Monday = findViewById(R.id.Monday1);
        Tuesday = findViewById(R.id.Tuesday1);
        Wednesday = findViewById(R.id.Wednesday1);
        Thursday = findViewById(R.id.Thursday1);
        Friday = findViewById(R.id.Friday1);
        Saturday = findViewById(R.id.Saturday1);
        Sunday = findViewById(R.id.Sunday1);

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
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);

                                noteEmployee.setDocumentId(documentSnapshot.getId());
                                String documentId = noteEmployee.getDocumentId();
                                String start = noteEmployee.getSignInN();
                                String finish = noteEmployee.getSignOutN();
                                String time = noteEmployee.getTimeStr();
                                tot_time = noteEmployee.getTimeInt();
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

                                tot_count += tot_time;

                                Log.d(TAG, "onEvent: " + data);

                                if (documentId.equals("Monday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Monday.setTextColor(getResources().getColor(R.color.Blue));
                                    Monday.setText(data);
                                    setMon(data);
                                }
                                if (documentId.equals("Tuesday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Tuesday.setTextColor(getResources().getColor(R.color.Blue));
                                    Tuesday.setText(data);
                                    setTues(data);
                                }
                                if (documentId.equals("Wednesday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Wednesday.setTextColor(getResources().getColor(R.color.Blue));
                                    Wednesday.setText(data);
                                    setWed(data);
                                }
                                if (documentId.equals("Thursday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Thursday.setTextColor(getResources().getColor(R.color.Blue));
                                    Thursday.setText(data);
                                    setThurs(data);
                                }
                                if (documentId.equals("Friday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Friday.setTextColor(getResources().getColor(R.color.Blue));
                                    Friday.setText(data);
                                    setFri(data);
                                }
                                if (documentId.equals("Saturday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Saturday.setTextColor(getResources().getColor(R.color.Blue));
                                    Saturday.setText(data);
                                    setSat(data);
                                }
                                if (documentId.equals("Sunday")) {
                                    data = documentId + "\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
                                    Sunday.setTextColor(getResources().getColor(R.color.Blue));
                                    Sunday.setText(data);
                                    setSun(data);
                                }
                            }
                            String finalTime = String.format(Locale.getDefault(), "%.2f hours", tot_count);

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


    public void send() {

        if (getMon() == null) {
            setMon("Did not work on Monday");
        }
        if (getTues() == null) {
            setTues("Did not work on Tuesday");
        }
        if (getWed() == null) {
            setWed("Did not work on Wednesday");
        }
        if (getThurs() == null) {
            setThurs("Did not work on Thursday");
        }
        if (getFri() == null) {
            setFri("Did not work on Friday");
        }
        if (getSat() == null) {
            setSat("Did not work on Saturday");
        }
        if (getSun() == null) {
            setSun("Did not work on Sunday");
        }

//        testString.add(getMon());
//        testString.add(getTues());
//        testString.add(getWed());
//        testString.add(getThurs());
//        testString.add(getFri());
//        testString.add(getSat());
//        testString.add(getSun());
//        testString.add(getTotal());

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "The total hours are: " + getTotal() + "\n" + getMon() + "\n" + getTues() + "\n" + getWed() + "\n" + getThurs() + "\n" + getFri() + "\n" + getSat() + "\n" + getSun());
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
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

    private View.OnClickListener editing = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(Employee.this, Edit.class);
            String id;
            switch (v.getId() /*to get clicked view id**/) {
                case R.id.Monday1:

                    id = "Monday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Tuesday1:

                    id = "Tuesday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Wednesday1:

                    id = "Wednesday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Thursday1:

                    id = "Thursday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Friday1:

                    id = "Friday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Saturday1:

                    id = "Saturday";
                    i.putExtra("name", getName());
                    i.putExtra("id", id);
                    startActivity(i);

                    break;
                case R.id.Sunday1:

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
