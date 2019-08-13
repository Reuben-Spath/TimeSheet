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

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class Edit extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private FirebaseAuth mAuth;

    private String day;
    private String user;
    private Button back;
    private Button confirm;

    private CheckBox lunch1;
    private CheckBox cbHoliday;
    private CheckBox cbSick;

    private ImageView delete;
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

    private float start_time;
    private float finish_time;

    private FrontScreenEmployee fse = new FrontScreenEmployee();

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent i = getIntent();

        setUser(i.getStringExtra("name"));
        setDay(i.getStringExtra("id"));

        confirm = findViewById(R.id.btConfirmEdit);
        back = findViewById(R.id.btBackEdit);

        setEnd(fse.history_maker());

        TextView dayOfWeek = findViewById(R.id.tvDayOfWeek);
        signedIn = findViewById(R.id.tvTimeIn);
        signedOut = findViewById(R.id.tvTimeOut);
        totalHours = findViewById(R.id.tvTotalTime);

        dayOfWeek.setText(getDay());

        delete = findViewById(R.id.img_delete);

        mAuth = FirebaseAuth.getInstance();

        lunch1 = findViewById(R.id.cbLunchEdit);

        cbHoliday = findViewById(R.id.cbHoliday);

        cbSick = findViewById(R.id.cbSick);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            db.collection("Users").document(getUser()).collection(getEnd()).document(getDay())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            String total;
                            float tot_time = 0;
                            if (Objects.requireNonNull(snapshot).toObject(NoteEmployee.class) != null) {
                                NoteEmployee noteEmployee = snapshot.toObject(NoteEmployee.class);

                                if (noteEmployee != null) {
                                    setStart_s(noteEmployee.getStart_s());
                                    setFinish_s(noteEmployee.getFinish_s());
                                    setFinish_time(noteEmployee.getFinish());
                                    setStart_time(noteEmployee.getStart());
                                    Toast.makeText(Edit.this, String.valueOf(getStart_time()), Toast.LENGTH_SHORT).show();
                                    setHadLunch(noteEmployee.getIfLunch());
                                    setHoliday(noteEmployee.isIfHoliday());
                                    setSick(noteEmployee.isIfSick());

                                    if (isHoliday()) {
                                        cbHoliday.setChecked(true);
//                                        cbHoliday.setEnabled(true);
                                        cbSick.setEnabled(false);
                                    }
                                    if (isSick()) {
                                        cbSick.setChecked(true);
//                                        cbSick.setEnabled(true);
                                        cbHoliday.setEnabled(false);
                                    }
                                    if (getHadLunch()) {
                                        lunch1.setChecked(true);
                                    }
                                    if (isHoliday() || isSick()) {
                                        lunch1.setEnabled(false);
                                    }

                                    tot_time = noteEmployee.getFinish() - noteEmployee.getStart();

                                    total = tot_time + " hours";
                                    totalHours.setText(total);
                                    signedIn.setText(getStart_s());
                                    signedOut.setText(getFinish_s());
                                }
                            }
                        }
                    });
        }

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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeworked(getHadLunch(), lunch, getStart_time(), getFinish_time());
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
                                finish();
                            }
                        });

            }
        });
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
        cbHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbHoliday.isChecked()) {
                    cbSick.setEnabled(false);
                    lunch1.setEnabled(false);
                    setHoliday(true);
                    String holiday = "Holiday";
                    totalHours.setText(holiday);
                } else {
                    cbSick.setEnabled(true);
                    lunch1.setEnabled(true);
                    setHoliday(false);
                    totalHours.setText(text_update());
                }
            }
        });
        cbSick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSick.isChecked()) {
                    cbHoliday.setEnabled(false);
                    lunch1.setEnabled(false);
                    setSick(true);
                    String sick = "Sick";
                    totalHours.setText(sick);
                } else {
                    cbHoliday.setEnabled(true);
                    lunch1.setEnabled(true);
                    setSick(false);
                    totalHours.setText(text_update());
                }
            }
        });
    }

    public String text_update() {
        return timeworked(getHadLunch(), lunch, getStart_time(), getFinish_time()) + " hours";
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
            if (total_time < 0) {
                total_time = total_time + 24;
            }
            outcome = round(total_time, 2);
            return outcome;
        }
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

            Toast.makeText(this, String.valueOf(getStart_time()), Toast.LENGTH_SHORT).show();
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

}
