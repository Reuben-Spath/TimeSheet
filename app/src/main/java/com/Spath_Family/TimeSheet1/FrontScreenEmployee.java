package com.Spath_Family.TimeSheet1;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FrontScreenEmployee extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static float lunch = (float) 0.5;

    private boolean in = false;
    private boolean out = false;
    protected TextView hours;

    private ImageView profile;
    private ImageView logo;

    protected TextView timeSignedIn;
    protected TextView timeSignedOff;
    //    private static final String TAG = "FrontScreenEmployee";
    Calendar calendar = Calendar.getInstance();
    public static final int FLAG_START_TIME = 0;

    private Button sign_in;
    private Button sign_off;
    private Button confirm;
    private boolean lunch_eaten = false;
    private FirebaseAuth mAuth;

    public static final int FLAG_END_TIME = 1;
    private TextView date;
    private int flag = 0;

    private int priority = 0;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private float start_time;
    private float finish_time;
    private String start_s;
    private String finish_s;


    private CheckBox lunch_checkbox;

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_employee);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        sign_in = findViewById(R.id.btSignIn);
        sign_off = findViewById(R.id.btSignOff);
        confirm = findViewById(R.id.btConfirm);

        hours = findViewById(R.id.tvHoursFS);
        profile = findViewById(R.id.draw_profile);

        logo = findViewById(R.id.logo);
        date = findViewById(R.id.tvWEnd);
        timeSignedIn = findViewById(R.id.tvTimeSignedIn);
        timeSignedOff = findViewById(R.id.tvTimeSignedOff);

        create();

        lunch_checkbox = findViewById(R.id.cbLunchFS);

        lunch_checkbox.setEnabled(false);

        lunch_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lunch_checkbox.isChecked()) {
                    setLunch_eaten(true);
                    timeworked(isLunch_eaten(), lunch, getStart_time(), getFinish_time());
                    hours.setText(change_lunch());

                } else {
                    setLunch_eaten(false);
                    timeworked(isLunch_eaten(), lunch, getStart_time(), getFinish_time());
                    hours.setText(change_lunch());
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
//        final String mUid = currentUser.getUid();

        date.setText(getCurrentDate());

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent special_code_pass = new Intent(FrontScreenEmployee.this, Profile.class);
                startActivity(special_code_pass);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    db.collection("Users").document(currentUser.getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String name_pass = documentSnapshot.getString("name");
                                    Intent i = new Intent(FrontScreenEmployee.this, EmployeeWeek.class);

                                    i.putExtra("name", name_pass);
                                    startActivity(i);
                                }
                            });
                }

            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag(FLAG_START_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        sign_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag(FLAG_END_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }

        });
        timeSignedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag(FLAG_START_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        timeSignedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag(FLAG_END_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }

        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (in && out) {
                    if (currentUser != null) {

                        db.collection("Users").document(currentUser.getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String name_pass = documentSnapshot.getString("name");
                                        Intent i = new Intent(FrontScreenEmployee.this, EmployeeWeek.class);
                                        i.putExtra("name", name_pass);
                                        startActivity(i);
                                    }
                                });
                    }
                }
            }

        });
    }

    public void setFlag(int i) {
        flag = i;
    }

    public void priority_setter(String input) {
        if (input.equalsIgnoreCase("Monday")) {
            setPriority(1);
        } else if (input.equalsIgnoreCase("Tuesday")) {
            setPriority(2);
        } else if (input.equalsIgnoreCase("Wednesday")) {
            setPriority(3);
        } else if (input.equalsIgnoreCase("Thursday")) {
            setPriority(4);
        } else if (input.equalsIgnoreCase("Friday")) {
            setPriority(5);
        } else if (input.equalsIgnoreCase("Saturday")) {
            setPriority(6);
        } else if (input.equalsIgnoreCase("Sunday")) {
            setPriority(7);
        }
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
        DateFormat df = new SimpleDateFormat("EEEE\ndd-MMMM", Locale.getDefault());
        return df.format(myCalender1.getTime());
    }

    public String history_maker() {
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int days = Calendar.SUNDAY - weekday;
        if (days < 0) {
            // this will usually be the case since Calendar.SUNDAY is the smallest
            days += 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, days);

        DateFormat df = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());

        return df.format(calendar.getTime());
    }

    public String asWeek() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dateFormat.format(now);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int value = hourOfDay;
        String amPm;
        if (hourOfDay > 12) {
            amPm = "PM";
            value = hourOfDay - 12;
        } else if (hourOfDay == 12) {
            amPm = "PM";
        } else {
            amPm = "AM";
        }
        if (flag == FLAG_START_TIME) {
            setStart_s(String.format(Locale.getDefault(), "%d:%02d %s", value, minute, amPm));
            timeSignedIn.setText(getStart_s());

            float minutes = (float) minute / 60;
            setStart_time(hourOfDay + minutes);

            if (out) {
                hours.setText(change_lunch());
            }
            in = true;

        } else if (flag == FLAG_END_TIME) {
            setFinish_s(String.format(Locale.getDefault(), "%d:%02d %s", value, minute, amPm));
            timeSignedOff.setText(getFinish_s());

            float minutes = (float) minute / 60;
            setFinish_time(hourOfDay + minutes);

            if (in) {
                hours.setText((change_lunch()));
            }
            out = true;
        }
        if (in && out) {
            lunch_checkbox.setEnabled(true);
        }
    }

    public void create() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String[] week = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        if (currentUser != null) {

            String mUid = currentUser.getUid();

            Map<String, Object> past_week = new HashMap<>();
            past_week.put(history_maker(), history_maker());

            for (String s : week) {
                priority_setter(s);
                Map<String, Object> note = new HashMap<>();
                note.put("priority", getPriority());
                db.collection("Users").document(mUid).collection(history_maker()).document(s)
                        .set(note, SetOptions.merge());
            }
            db.collection("Users").document(mUid).collection("History").document("History")
                    .set(past_week, SetOptions.merge());
        }
    }

    public float timeworked(boolean lunch, float lunch_length, float start, float finish) {
        float total_time;

        total_time = finish - start;
        if (total_time < 0) {
            total_time = total_time + 24;
        }
        float outcome = round(total_time, 2);

        if (lunch) {
            total_time = total_time - lunch_length;
            if (total_time < 0) {
                total_time = total_time + 24;
            }
            outcome = round(total_time, 2);
            return outcome;
        } else {
            return outcome;
        }
    }

    public String change_lunch() {
        return timeworked(isLunch_eaten(), getLunch(), getStart_time(), getFinish_time()) + " hours";
    }

    public void fullTimeSave() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            String mUid = mAuth.getCurrentUser().getUid();

            NoteEmployee noteEmployee = new NoteEmployee(getStart_time(), getFinish_time(), isLunch_eaten(), getStart_s(), getFinish_s());

            db.collection("Users").document(mUid).collection(history_maker()).document(asWeek())
                    .set(noteEmployee, SetOptions.merge());
        }
    }

    public float getStart_time() {
        return start_time;
    }

    public void setStart_time(float start_time) {
        this.start_time = start_time;
    }

    public float getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(float finish_time) {
        this.finish_time = finish_time;
    }

    public float getLunch() {
        return lunch;
    }

    public boolean isLunch_eaten() {
        return lunch_eaten;
    }

    public void setLunch_eaten(boolean lunch_eaten) {
        this.lunch_eaten = lunch_eaten;
    }

    public String getStart_s() {
        return start_s;
    }

    public void setStart_s(String start_s) {
        this.start_s = start_s;
    }

    public String getFinish_s() {
        return finish_s;
    }

    public void setFinish_s(String finish_s) {
        this.finish_s = finish_s;
    }
}
