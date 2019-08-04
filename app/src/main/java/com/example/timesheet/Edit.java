package com.example.timesheet;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Edit extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Calendar calendar = Calendar.getInstance();

    private static final String TAG = "Edit";
    private FirebaseAuth mAuth;

    private String day;
    private String user;
    private Button back;

    private CheckBox lunch1;
    private CheckBox cbHoliday;
    private CheckBox cbSick;

    private TextView signedIn;
    private TextView signedOut;
    private TextView totalHours;

//    private TimePickerDialog timePickerDialog;
//    private int i;

    private String pass;
    private float longpass;

    private boolean hadLunch;
    private boolean sick;
    private boolean holiday;

    private static String signedInS;
    private static String signedOff;
    private static float timeWorkedWLunch;
    private static float timeWorked;

    private static long startMin;
    private static long startHr;
    private static long finishMin;
    private static long finishHr;

    private static long hours4today;
    private static long minutes4today;

    public static final int FLAG_START_TIME = 0;
    public static final int FLAG_END_TIME = 1;
    private int flag = 0;

    private int priority = 0;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean getHadLunch() {
        return hadLunch;
    }

    public void setHadLunch(boolean hadLunch) {
        this.hadLunch = hadLunch;
    }

    public float getLongpass() {
        return longpass;
    }

    public void setLongpass(float longpass) {
        this.longpass = longpass;
    }

    public long getStartMin() {
        return startMin;
    }

    public void setStartMin(long startMin) {
        Edit.startMin = startMin;
    }

    public long getStartHr() {
        return startHr;
    }

    public void setStartHr(long startHr) {
        Edit.startHr = startHr;
    }

    public long getFinishMin() {
        return finishMin;
    }

    public void setFinishMin(long finishMin) {
        Edit.finishMin = finishMin;
    }

    public long getFinishHr() {
        return finishHr;
    }

    public void setFinishHr(long finishHr) {
        Edit.finishHr = finishHr;
    }

    public long getHours4today() {
        return hours4today;
    }

    public void setHours4today(long hours4today) {
        Edit.hours4today = hours4today;
    }

    public long getMinutes4today() {
        return minutes4today;
    }

    public void setMinutes4today(long minutes4today) {
        Edit.minutes4today = minutes4today;
    }

    public String getSignedIn() {
        return signedInS;
    }

    public void setSignedIn(String signedIn) {
        Edit.signedInS = signedIn;
    }

    public String getSignedOff() {
        return signedOff;
    }

    public void setSignedOff(String signedOff) {
        Edit.signedOff = signedOff;
    }


    public void setTimeWorked(float timeWorked) {
        Edit.timeWorked = timeWorked;
    }

    public float getTimeWorked() {
        return timeWorked;
    }

    public float getTimeWorkedWLunch() {
        return timeWorkedWLunch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent i = getIntent();

        setUser(i.getStringExtra("name"));
        setDay(i.getStringExtra("id"));

        Button confirm = findViewById(R.id.btConfirmEdit);
        back = findViewById(R.id.btBackEdit);


        TextView dayOfWeek = findViewById(R.id.tvDayOfWeek);
        signedIn = findViewById(R.id.tvTimeIn);
        signedOut = findViewById(R.id.tvTimeOut);
        totalHours = findViewById(R.id.tvTotalTime);

        dayOfWeek.setText(getDay());

        ImageView delete = findViewById(R.id.img_delete);

        mAuth = FirebaseAuth.getInstance();
        priority_setter();
        create();
        checkbox();
        checkbox_holiday();
        checkbox_sick();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getHadLunch()) {
                    setPass(hours_worked_with_lunch());
                } else {
                    setPass(hours_worked_without_lunch());
                }
                setLongpass(getTimeWorked());
                fullTimeSave();
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(getUser()).collection(weekEnding()).document(getDay())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                Toast.makeText(Edit.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                create();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

            }
        });

    }

    public void setFlag(int i) {
        flag = i;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String amPm;
        if (flag == FLAG_START_TIME) {
            setStartHr(hourOfDay);
            setStartMin(minute);

            if (hourOfDay > 12) {
                amPm = "PM";
                hourOfDay = hourOfDay - 12;
            } else {
                amPm = "AM";
            }
            signedIn.setText(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minute, amPm));
            setSignedIn(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minute, amPm));

            if (lunch1.isChecked()) {
                totalHours.setText(hours_worked_with_lunch());
            } else {
                totalHours.setText(hours_worked_without_lunch());
            }

        } else if (flag == FLAG_END_TIME) {

            setFinishHr(hourOfDay);
            setFinishMin(minute);

            if (hourOfDay > 12) {
                amPm = "PM";
                hourOfDay = hourOfDay - 12;
            } else {
                amPm = "AM";
            }
            signedOut.setText(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minute, amPm));
            setSignedOff(String.format(Locale.getDefault(), "%d:%02d %s", hourOfDay, minute, amPm));

            if (lunch1.isChecked()) {
                totalHours.setText(hours_worked_with_lunch());
            } else {
                totalHours.setText(hours_worked_without_lunch());
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        signedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signInCustomPicker();
                setFlag(FLAG_START_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        signedOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signOffCustomPicker();
                setFlag(FLAG_END_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            db.collection("Users").document(getUser()).collection(weekEnding()).document(getDay())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (Objects.requireNonNull(snapshot).toObject(NoteEmployee.class) != null) {
                                NoteEmployee noteEmployee = snapshot.toObject(NoteEmployee.class);

                                setPass(Objects.requireNonNull(noteEmployee).getTimeStr());
                                setStartHr(noteEmployee.getStartH());
                                setStartMin(noteEmployee.getStartM());
                                setFinishHr(noteEmployee.getFinishH());
                                setFinishMin(noteEmployee.getFinishM());
                                setSignedOff(noteEmployee.getSignOutN());
                                setSignedIn(noteEmployee.getSignInN());
                                setMinutes4today(noteEmployee.getM4t());
                                setHours4today(noteEmployee.getH4t());
                                setHadLunch(noteEmployee.getIfLunch());
                            }

                            if (snapshot.exists()) {
                                Log.d(TAG, "Current data: " + snapshot.getData());

                                if (snapshot.getString("signInN") != null) {
                                    signedIn.setText(snapshot.getString("signInN"));
                                    setSignedIn(snapshot.getString("signInN"));
                                }
                                if (snapshot.getString("signOutN") != null) {
                                    signedOut.setText(snapshot.getString("signOutN"));
                                    setSignedOff(snapshot.getString("signOutN"));
                                }
                                if (snapshot.getString("timeStr") != null) {
                                    totalHours.setText(snapshot.getString("timeStr"));
                                }
                                if (snapshot.getBoolean("ifLunch") != null) {
                                    //noinspection ConstantConditions
                                    if (snapshot.getBoolean("ifLunch")) {
                                        lunch1.setChecked(true);
                                    } else {
                                        lunch1.setChecked(false);
                                    }
                                }
                                if (snapshot.getBoolean("ifSick") != null) {
                                    //noinspection ConstantConditions
                                    if (snapshot.getBoolean("ifSick")) {
                                        cbSick.setChecked(true);
                                        cbHoliday.setEnabled(false);
                                    } else {
                                        cbSick.setChecked(false);
                                    }
                                }
                                if (snapshot.getBoolean("ifHoliday") != null) {
                                    //noinspection ConstantConditions
                                    if (snapshot.getBoolean("ifHoliday")) {
                                        cbHoliday.setChecked(true);
                                        cbSick.setEnabled(false);
                                    } else {
                                        cbHoliday.setChecked(false);
                                    }
                                }
                            } else {
                                Log.d(TAG, "Current data: null");
                            }
                        }
                    });
        }
    }

    public void priority_setter() {
        String day_pass = getDay();
        if (day_pass.equalsIgnoreCase("Monday")) {
            setPriority(1);
        } else if (day_pass.equalsIgnoreCase("Tuesday")) {
            setPriority(2);
        } else if (day_pass.equalsIgnoreCase("Wednesday")) {
            setPriority(3);
        } else if (day_pass.equalsIgnoreCase("Thursday")) {
            setPriority(4);
        } else if (day_pass.equalsIgnoreCase("Friday")) {
            setPriority(5);
        } else if (day_pass.equalsIgnoreCase("Saturday")) {
            setPriority(6);
        } else if (day_pass.equalsIgnoreCase("Sunday")) {
            setPriority(7);
        }
    }
    public void create() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Map<String, Object> note = new HashMap<>();
            note.put("priority", getPriority());

            db.collection("Users").document(getUser()).collection(weekEnding()).document(getDay())
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

    public void checkbox() {
        setHadLunch(false);
        totalHours.setText(hours_worked_without_lunch());
        setPass(hours_worked_without_lunch());
        setLongpass(getTimeWorked());

        lunch1 = findViewById(R.id.cbLunchEdit);

        lunch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lunch1.isChecked()) {
                    setHadLunch(true);
                    totalHours.setText(hours_worked_with_lunch());
                    setPass(hours_worked_with_lunch());
                    setLongpass(getTimeWorkedWLunch());

                } else {
                    setHadLunch(false);
                    totalHours.setText(hours_worked_without_lunch());
                    setPass(hours_worked_without_lunch());
                    setLongpass(getTimeWorked());
                }
            }
        });
    }

    public void checkbox_holiday() {
        setHoliday(false);
        cbHoliday = findViewById(R.id.cbHoliday);

        if (isSick()) {
            cbSick.setEnabled(true);
        }
        cbHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbHoliday.isChecked()) {
                    cbSick.setEnabled(false);
                    setHoliday(true);
                } else {
                    cbSick.setEnabled(true);
                    setHoliday(false);
                }
            }
        });
    }

    public void checkbox_sick() {
        setSick(false);
        cbSick = findViewById(R.id.cbSick);

        if (isHoliday()) {
            cbSick.setEnabled(true);
        }
        cbSick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSick.isChecked()) {
                    cbHoliday.setEnabled(false);
                    setSick(true);
                } else {
                    cbHoliday.setEnabled(true);
                    setSick(false);
                }
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

        DateFormat df = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());

        return df.format(calendar.getTime());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String hours_worked_without_lunch() {

        setHours4today(getFinishHr() - getStartHr());
        setMinutes4today(getFinishMin() - getStartMin());

        if (getHours4today() == 0 && getMinutes4today() < 0) {
            setHours4today(getHours4today() + 24);
        }
        if (getHours4today() < 0) {
            setHours4today(getHours4today() + 24);
        }
        if (getMinutes4today() > 60) {
            setHours4today(getHours4today() + 1);
            setMinutes4today(getMinutes4today() - 60);
        }
        if (getMinutes4today() < 0) {
            setHours4today(getHours4today() - 1);
            setMinutes4today(getMinutes4today() + 60);
        }

        float minutes = (float) getMinutes4today() / 60;
        setTimeWorked(getHours4today() + minutes);

        if (getMinutes4today() < 10 && getMinutes4today() > 0) {
            return getHours4today() + ".0" + getMinutes4today() + " hours";

        } else if (getHours4today() < 1 && getMinutes4today() > 30) {
            return getMinutes4today() + " minutes";

        } else {
            return getHours4today() + "." + getMinutes4today() + " hours";
        }
    }

    public String hours_worked_with_lunch() {

        setHours4today(getFinishHr() - getStartHr());
        setMinutes4today(getFinishMin() - getStartMin() - 30);

        if (getHours4today() == 0 && getMinutes4today() < 0) {
            setHours4today(getHours4today() + 24);
        }
        if (getHours4today() < 0) {
            setHours4today(getHours4today() + 24);
        }
        if (getMinutes4today() > 60) {
            setHours4today(getHours4today() + 1);
            setMinutes4today(getMinutes4today() - 60);
        }
        if (getMinutes4today() < 0) {
            setHours4today(getHours4today() - 1);
            setMinutes4today(getMinutes4today() + 60);
        }

        if (getHours4today() < 1) {
            return getMinutes4today() + " minutes";
        } else {

            if (getMinutes4today() < 0) {

                setHours4today(getHours4today() - 1);
                setMinutes4today(getMinutes4today() + 60);
            }

            float minutes = (float) getMinutes4today() / 60;
            setTimeWorked(getHours4today() + minutes);

            if (getMinutes4today() < 10 && getMinutes4today() > 0) {
                return getHours4today() + ".0" + getMinutes4today() + " hours";

            } else {
                return getHours4today() + "." + getMinutes4today() + " hours";
            }
        }
    }

    public void fullTimeSave() {

        if (user != null) {

            NoteEmployee noteEmployee = new NoteEmployee(getPass(), getLongpass(), getHadLunch(), getSignedIn(), getSignedOff());

            noteEmployee.setStartH(getStartHr());
            noteEmployee.setStartM(getStartMin());

            noteEmployee.setFinishH(getFinishHr());
            noteEmployee.setFinishM(getFinishMin());

            if (getStartHr() == 0 && getStartMin() == 0 && getFinishHr() == 0 && getFinishMin() == 0) {
                noteEmployee.setTimeStr("");
            }
            noteEmployee.setH4t(getHours4today());
            noteEmployee.setM4t(getMinutes4today());

            noteEmployee.setIfLunch(getHadLunch());

            noteEmployee.setIfHoliday(isHoliday());

            noteEmployee.setIfSick(isSick());

            db.collection("Users").document(getUser()).collection(weekEnding()).document(getDay())
                    .set(noteEmployee, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w(TAG, "onSuccess: ");
                            Toast.makeText(Edit.this, "Saved!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Failure to add", e.toString());
                        }
                    });
        }
    }

    public boolean isSick() {
        return sick;
    }

    public void setSick(boolean sick) {
        this.sick = sick;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
