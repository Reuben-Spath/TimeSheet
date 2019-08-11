package com.Spath_Family.TimeSheet1;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.Locale;
import java.util.Objects;

public class Edit extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
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

    private static float lunch = (float) 0.5;

    private boolean hadLunch;
    private boolean sick;
    private boolean holiday;

    private String start_s;
    private String finish_s;
    private float start;
    private float finish;

    public static final int FLAG_START_TIME = 0;
    public static final int FLAG_END_TIME = 1;
    private int flag = 0;

    private String end;


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

    public boolean getHadLunch() {
        return hadLunch;
    }

    public void setHadLunch(boolean hadLunch) {
        this.hadLunch = hadLunch;
    }

//    public void setLongpass(float longpass) {
//        this.longpass = longpass;
//    }

    private float start_time;
    private float finish_time;

    private FrontScreenEmployee fse = new FrontScreenEmployee();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent i = getIntent();

        setUser(i.getStringExtra("name"));
        setDay(i.getStringExtra("id"));

        Button confirm = findViewById(R.id.btConfirmEdit);
        back = findViewById(R.id.btBackEdit);


        setEnd(fse.history_maker());

        TextView dayOfWeek = findViewById(R.id.tvDayOfWeek);
        signedIn = findViewById(R.id.tvTimeIn);
        signedOut = findViewById(R.id.tvTimeOut);
        totalHours = findViewById(R.id.tvTotalTime);

        dayOfWeek.setText(getDay());

        ImageView delete = findViewById(R.id.img_delete);

        mAuth = FirebaseAuth.getInstance();
        checkbox();
        checkbox_holiday();
        checkbox_sick();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullTimeSave();
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(getUser()).collection(getEnd()).document(getDay())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Edit.this, "Deleted!", Toast.LENGTH_SHORT).show();
//                                create();
                                finish();
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

            float minutes = (float) minute / 60;
            setStart_time(hourOfDay + minutes);

            setStart_s(String.format(Locale.getDefault(), "%d:%02d %s", value, minute, amPm));
            signedIn.setText(getStart_s());

        } else if (flag == FLAG_END_TIME) {
            float minutes = (float) minute / 60;
            setFinish_time(hourOfDay + minutes);

            setFinish_s(String.format(Locale.getDefault(), "%d:%02d %s", value, minute, amPm));
            signedOut.setText(getFinish_s());
        }
        totalHours.setText(text_update());

    }

    public String text_update() {
        return fse.timeworked(getHadLunch(), lunch, getStart(), getFinish()) + " hours";
    }

    public void call() {
        fse.timeworked(getHadLunch(), lunch, getStart(), getFinish());
    }

    @Override
    protected void onStart() {
        super.onStart();

        signedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag(FLAG_START_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
//                totalHours.setText(text_update());
            }
        });
        signedOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag(FLAG_END_TIME);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
//                totalHours.setText(text_update());
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

            db.collection("Users").document(getUser()).collection(getEnd()).document(getDay())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            String total = "";
                            float tot_time = 0;
                            if (Objects.requireNonNull(snapshot).toObject(NoteEmployee.class) != null) {
                                NoteEmployee noteEmployee = snapshot.toObject(NoteEmployee.class);

                                if (noteEmployee != null) {
                                    setStart_s(noteEmployee.getStart_s());
                                    setFinish_s(noteEmployee.getFinish_s());
                                    setFinish(noteEmployee.getFinish());
                                    setStart(noteEmployee.getStart());
                                    tot_time = noteEmployee.getFinish() - noteEmployee.getStart();

                                }
                                total = tot_time + " hours";
                                totalHours.setText(total);
                                signedIn.setText(getStart_s());
                                signedOut.setText(getFinish_s());


                            }

                            if (snapshot.exists()) {

//                                if (snapshot.getString("start_s") != null) {
//                                    signedIn.setText(snapshot.getString("start_s"));
//                                    setStart_s(snapshot.getString("start_s"));
//                                }
//                                if (snapshot.getString("finish_s") != null) {
//                                    signedOut.setText(snapshot.getString("finish_s"));
//                                    setFinish_s(snapshot.getString("finish_s"));
//                                }
//
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
                            }
                        }
                    });
        }
    }

    public void checkbox() {
        setHadLunch(false);
        totalHours.setText(text_update());

        lunch1 = findViewById(R.id.cbLunchEdit);

        lunch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lunch1.isChecked()) {
                    setHadLunch(true);
                    totalHours.setText(text_update());

                } else {
                    setHadLunch(false);
                    totalHours.setText(text_update());
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
                    String holiday = "Holiday";
                    totalHours.setText(holiday);
                } else {
                    cbSick.setEnabled(true);
                    setHoliday(false);
                    totalHours.setText(text_update());
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
                    String sick = "Sick";
                    totalHours.setText(sick);

                } else {
                    cbHoliday.setEnabled(true);
                    setSick(false);
                    totalHours.setText(text_update());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void fullTimeSave() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            NoteEmployee noteEmployee = new NoteEmployee(getStart_time(), getFinish_time(), getHadLunch(), getStart_s(), getFinish_s());

            noteEmployee.setIfSick(isSick());
            noteEmployee.setIfHoliday(isHoliday());

            db.collection("Users").document(getUser()).collection(getEnd()).document(getDay())
                    .set(noteEmployee, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Edit.this, "Saved!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getFinish() {
        return finish;
    }

    public void setFinish(float finish) {
        this.finish = finish;
    }
}
