package com.example.timesheet;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FrontScreenEmployee extends AppCompatActivity{

    private static final String TAG = "FrontScreenEmployee";
    Calendar calendar = Calendar.getInstance();

    protected Chronometer chronometer;

    protected static boolean less_5 = false;
    private boolean in = false;
    private boolean out = false;

    private ImageView profile;

    protected TextView timeSignedIn;
    protected TextView timeSignedOff;
//    private TextView EmpCode;
    private TextView date;

    private Button sign_in;
    private Button sign_off;
    private Button employer_code;
    private Button week;
    private Button confirm;

    private static String signedIn;
    private static String signedOff;

    //    private long pauseOffset = 0L;
//    private long startT;
//    private long finishT;

    private static float timeWorkedWLunch;
    private static float timeWorked;

    private TimePickerDialog timePickerDialog;
    private String amPm;
    private FirebaseAuth mAuth;


//    private String time1;
//    private String time2;

    private static int startMin;
    private static int startHr;
    private static int finishMin;
    private static int finishHr;

    private static int hours4today;
    private static int minutes4today;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

//    public boolean isLess_5() {
//        return less_5;
//    }

    public void setLess_5(boolean less_5) {
        FrontScreenEmployee.less_5 = less_5;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        FrontScreenEmployee.startMin = startMin;
    }

    public int getStartHr() {
        return startHr;
    }

    public void setStartHr(int startHr) {
        FrontScreenEmployee.startHr = startHr;
    }

    public int getFinishMin() {
        return finishMin;
    }

    public void setFinishMin(int finishMin) {
        FrontScreenEmployee.finishMin = finishMin;
    }

    public int getFinishHr() {
        return finishHr;
    }

    public void setFinishHr(int finishHr) {
        FrontScreenEmployee.finishHr = finishHr;
    }

    public int getHours4today() {
        return hours4today;
    }

    public void setHours4today(int hours4today) {
        FrontScreenEmployee.hours4today = hours4today;
    }

    public int getMinutes4today() {
        return minutes4today;
    }

    public void setMinutes4today(int minutes4today) {
        FrontScreenEmployee.minutes4today = minutes4today;
    }

//    public long getStartT() {
//        return startT;
//    }

//    public void setStartT(long startT) {
//        this.startT = startT;
//    }

//    public long getFinishT() {
//        return finishT;
//    }

//    public void setFinishT(long finishT) {
//        this.finishT = finishT;
//    }

    public String getSignedIn() {
        return signedIn;
    }

    public void setSignedIn(String signedIn) {
        FrontScreenEmployee.signedIn = signedIn;
    }

    public String getSignedOff() {
        return signedOff;
    }

    public void setSignedOff(String signedOff) {
        FrontScreenEmployee.signedOff = signedOff;
    }


    public void setTimeWorked(float timeWorked) {
        FrontScreenEmployee.timeWorked = timeWorked;
    }

    public float getTimeWorked() {
        return timeWorked;
    }

//    public void setTimeWorkedWLunch(float timeWorkedWLunch) {
//        FrontScreenEmployee.timeWorkedWLunch = timeWorkedWLunch;
//    }

    public float getTimeWorkedWLunch() {
        return timeWorkedWLunch;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_employee);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mAuth = FirebaseAuth.getInstance();

        chronometer = findViewById(R.id.chronometer);

        sign_in = findViewById(R.id.btSignIn);
        sign_off = findViewById(R.id.btSignOff);
        confirm = findViewById(R.id.btConfirm);

        profile = findViewById(R.id.draw_profile);

        employer_code = findViewById(R.id.btEmpCode);
//        week = findViewById(R.id.btWeek);

//        EmpCode = findViewById(R.id.tvEmpCode);
        date = findViewById(R.id.tvWEnd);
        timeSignedIn = findViewById(R.id.tvTimeSignedIn);
        timeSignedOff = findViewById(R.id.tvTimeSignedOff);

        create();

    }

    @Override
    public void onStart() {
        super.onStart();

        date.setText(getCurrentDate());

        employer_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent special_code_pass = new Intent(FrontScreenEmployee.this, Profile.class);
                startActivity(special_code_pass);
//                Intent special_code_pass = new Intent(FrontScreenEmployee.this, Tester.class);
//                startActivity(special_code_pass);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile_pass = new Intent(FrontScreenEmployee.this, EmployeeWeek.class);
                startActivity(profile_pass);
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInCustomPicker();
            }
        });
        sign_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOffCustomPicker();
            }

        });

        timeSignedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInCustomPicker();
            }
        });
        timeSignedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOffCustomPicker();
            }

        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (in && out) {
                    Intent i = new Intent(FrontScreenEmployee.this, Confirmation.class);
                    startActivity(i);

                }
            }
        });
//        empListner();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            mAuth.signOut();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getCurrentDate() {
        final Calendar myCalender1 = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("EEEE", Locale.getDefault());
        return df.format(myCalender1.getTime());
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

    public String asWeek() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dateFormat.format(now);
    }

//    public void empListner() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            final DocumentReference docRef = db.collection("Users").document(user.getUid());
//            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                    @Nullable FirebaseFirestoreException e) {
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//
//                    if (snapshot != null && snapshot.exists()) {
//                        Log.d(TAG, "Current data: " + snapshot.getData());
//                        EmpCode.setText(snapshot.getString("Employer Code"));
//                    } else {
//                        Log.d(TAG, "Current data: null");
//                    }
//                }
//            });
//        }
//    }

    public void create() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            String mUid = currentUser.getUid();
            Map<String, Object> note = new HashMap<>();

            db.collection("Users").document(mUid).collection(weekEnding()).document(asWeek())
                    .set(note, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
    }

    public void signInCustomPicker() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

//        time1 = String.format(String.valueOf(Calendar.getInstance().getTime()));

        timePickerDialog = new TimePickerDialog(FrontScreenEmployee.this, R.style.HoloDialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, hourOfDay);
                calendar.set(Calendar.MINUTE, minutes);

                setStartHr(hourOfDay);
                setStartMin(minutes);

                if (hourOfDay > 12) {
                    amPm = "PM";
                    hourOfDay = hourOfDay - 12;
                } else {
                    amPm = "AM";
                }
                timeSignedIn.setText(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minutes, amPm));

                setSignedIn(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minutes, amPm));
                in = true;
            }
        }, hour, minute, false);

        timePickerDialog.setTitle("Sign In Time:");
        timePickerDialog.show();
    }

    public void signOffCustomPicker() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

//        time2 = String.format(String.valueOf(Calendar.getInstance().getTime()));

        timePickerDialog = new TimePickerDialog(FrontScreenEmployee.this, R.style.HoloDialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, hourOfDay);
                calendar.set(Calendar.MINUTE, minutes);

                setFinishHr(hourOfDay);
                setFinishMin(minutes);

                if (hourOfDay > 12) {
                    amPm = "PM";
                    hourOfDay = hourOfDay - 12;
                } else {
                    amPm = "AM";
                }
                timeSignedOff.setText(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minutes, amPm));

                setSignedOff(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minutes, amPm));
                out = true;

            }
        }, hour, minute, false);

        timePickerDialog.setTitle("Sign Off Time:");
        timePickerDialog.show();
    }

    //100 minutes just needs to have a total of 60

    public String hours_worked_without_lunch() {

        setHours4today(getFinishHr() - getStartHr());
        setMinutes4today(getFinishMin() - getStartMin());

        if(getHours4today()<5){
            setLess_5(true);
        }

        if(getHours4today()==0 && getMinutes4today()<0){
            setHours4today(getHours4today()+24);

        }
        if(getHours4today()<0){
            setHours4today(getHours4today()+24);
        }
        if(getMinutes4today()>60){
            setHours4today(getHours4today()+1);
            setMinutes4today(getMinutes4today()-60);
        }
        if (getMinutes4today() < 0) {
            setHours4today(getHours4today() - 1);
            setMinutes4today(getMinutes4today() + 60);
        }

        float minutes = (float) getMinutes4today() / 60;
        setTimeWorked(getHours4today() + minutes);


        if (getMinutes4today() < 10 && getMinutes4today() > 0) {
            return getHours4today() + ":0" + getMinutes4today() + " hours";

        } else if (getHours4today() < 1 && getMinutes4today() > 30) {
            return getHours4today() + ":" + getMinutes4today() + " minutes";

        } else {
            return getHours4today() + ":" + getMinutes4today() + " hours";
        }

    }

    public String hours_worked_with_lunch() {

        setHours4today(getFinishHr() - getStartHr());
        setMinutes4today(getFinishMin() - getStartMin() - 30);


        if(getHours4today()<5){
            setLess_5(true);
        }
        if(getHours4today()==0 && getMinutes4today()<0){
            setHours4today(getHours4today()+24);

        }
        if(getHours4today()<0){
            setHours4today(getHours4today()+24);
        }
        if(getMinutes4today()>60){
            setHours4today(getHours4today()+1);
            setMinutes4today(getMinutes4today()-60);
        }
        if (getMinutes4today() < 0) {
            setHours4today(getHours4today() - 1);
            setMinutes4today(getMinutes4today() + 60);
        }


        if (getHours4today() < 1) {
            return getHours4today() + ":" + getMinutes4today() + " minutes";
        } else {

            if (getMinutes4today() < 0) {

                setHours4today(getHours4today() - 1);
                setMinutes4today(getMinutes4today() + 60);
            }

            float minutes = (float) getMinutes4today() / 60;
            setTimeWorked(getHours4today() + minutes);

            if (getMinutes4today() < 10 && getMinutes4today() > 0) {
                return getHours4today() + ":0" + getMinutes4today() + " hours";

            } else {
                return getHours4today() + ":" + getMinutes4today() + " hours";

            }

        }
    }
}