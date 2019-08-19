package com.Spath_Family.TimeSheet1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Employee extends AppCompatActivity implements exampleDialog.ExampleDialogistener {

    Calendar calendar = Calendar.getInstance();

    private static final String TAG = "Employee";

    private FirebaseAuth mAuth;

    //    private GridLayout layout;
    private TextView totalHours;
    private ImageView history;
    private TextView employee;

    private Button Back;
    private Button send;

    private String nameId;
    private String userId;

    private String total;

    private String email_subject;

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

    private String input_text = "";
    private String fileName = "config.csv";

    private float lunch = (float) 0.5;


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

        employee = findViewById(R.id.tvWeekEnding1);
        totalHours = findViewById(R.id.tvTotalHours1);

        history = findViewById(R.id.draw_history_mng);

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
        final FrontScreenEmployee frontScreenEmployee = new FrontScreenEmployee();
        info_pt_2(frontScreenEmployee.history_maker());
        setEmail_subject(weekEnding() + " " + getUserId());

        setInput_text("Day:, Start Time:, Finish Time:, Total Time:, Lunch: \n");

        String pass = "Week Ending:\n" + frontScreenEmployee.history_maker();

        employee.setText(pass);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = getEmail_subject() + ".csv";
                save(getInput_text());
                share_text();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });
    }

    @Override
    public void applyTexts(String editTextHistory) {
        history(editTextHistory);
    }

    public void openDialog() {
        exampleDialog exampleDialog = new exampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void history(final String input_text) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(getName()).collection("History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    if (document.getData().containsValue(input_text)) {
                                        setInput_text("Day:, Start Time:, Finish Time:, Total Time:, Lunch: \n");
                                        info_pt_2(input_text);

                                        String pass = "Week Ending:\n" + input_text;
                                        employee.setText(pass);
                                        setEmail_subject(input_text + " " + getUserId());
                                        Toast.makeText(Employee.this, "Successfully Loaded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Employee.this, "Please enter the correct format (e.g 28-07-19)", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(Employee.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void save(String text) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(text.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void share_text() {
        File file = new File(getFilesDir() + "/" + fileName);
        Uri path = FileProvider.getUriForFile(this, "com.Spath_Family.TimeSheet1", file);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, getEmail_subject());
        String text_message = getTotal();
        i.putExtra(Intent.EXTRA_TEXT, text_message);
        i.putExtra(Intent.EXTRA_STREAM, path);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setType("text/*");
        startActivity(i);
    }

    public void info_pt_2(final String week_ending_pass) {
        Intent i = getIntent();
        String userName = i.getStringExtra("id");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            db.collection("Users").document(userName).collection(week_ending_pass)
                    .whereGreaterThan("priority", 0)
                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }


                            float tot_count = 0;
                            float tot_time;
                            String data;
                            String hours;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);
                                noteEmployee.setDocumentId(documentSnapshot.getId());
                                String documentId = noteEmployee.getDocumentId();
                                boolean lunch = noteEmployee.getIfLunch();

                                String y_or_n;
                                if (lunch) {
                                    y_or_n = "Y";
                                    tot_time = (noteEmployee.getFinish() - noteEmployee.getStart()) - getLunch();
                                    if (tot_time < 0) {
                                        tot_time += 24;
                                    }
                                } else {
                                    y_or_n = "N";
                                    tot_time = noteEmployee.getFinish() - noteEmployee.getStart();
                                    if (tot_time < 0) {
                                        tot_time += 24;
                                    }
                                }

                                String start = noteEmployee.getStart_s();
                                String finish = noteEmployee.getFinish_s();

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
                                    y_or_n = "";
                                    start = "";
                                }
                                if (finish == null) {
                                    y_or_n = "";
                                    finish = "";
                                }


                                if (documentId.equals("Monday")) {
                                    if (holiday) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Holiday";
                                        montot.setText(data);
                                        setInput_text(getInput_text() + "Monday:,,,Holiday\n");

                                    } else if (sick) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Sick";
                                        montot.setText(data);
                                        setInput_text(getInput_text() + "Monday:,,,Sick\n");

                                    } else {
                                        monst.setText(start);
                                        monfn.setText(finish);
                                        montot.setText(hours);
                                        setInput_text(getInput_text() + "Monday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");
                                    }
                                }
                                if (documentId.equals("Tuesday")) {
                                    if (holiday) {
                                        tuesst.setText("");
                                        tuesfn.setText("");
                                        data = "Holiday";
                                        tuestot.setText(data);
                                        setInput_text(getInput_text() + "Tuesday:,,,Holiday\n");

                                    } else if (sick) {
                                        tuesst.setText("");
                                        tuesfn.setText("");
                                        data = "Sick";
                                        tuestot.setText(data);
                                        setInput_text(getInput_text() + "Tuesday:,,,Sick\n");

                                    } else {
                                        tuesst.setText(start);
                                        tuesfn.setText(finish);
                                        tuestot.setText(hours);
                                        setInput_text(getInput_text() + "Tuesday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");

                                    }
                                }
                                if (documentId.equals("Wednesday")) {
                                    if (holiday) {
                                        wedst.setText("");
                                        wedfn.setText("");
                                        data = "Holiday";
                                        wedtot.setText(data);
                                        setInput_text(getInput_text() + "Wednesday:,,,Holiday\n");

                                    } else if (sick) {
                                        wedst.setText("");
                                        wedfn.setText("");
                                        data = "Sick";
                                        wedtot.setText(data);
                                        setInput_text(getInput_text() + "Wednesday:,,,Sick\n");

                                    } else {
                                        wedst.setText(start);
                                        wedfn.setText(finish);
                                        wedtot.setText(hours);
                                        setInput_text(getInput_text() + "Wednesday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");

                                    }
                                }
                                if (documentId.equals("Thursday")) {
                                    if (holiday) {
                                        thursst.setText("");
                                        thursfn.setText("");
                                        data = "Holiday";
                                        thurstot.setText(data);
                                        setInput_text(getInput_text() + "Thursday:,,,Holiday\n");

                                    } else if (sick) {
                                        thursst.setText("");
                                        thursfn.setText("");
                                        data = "Sick";
                                        thurstot.setText(data);
                                        setInput_text(getInput_text() + "Thursday:,,,Sick\n");

                                    } else {
                                        thursst.setText(start);
                                        thursfn.setText(finish);
                                        thurstot.setText(hours);
                                        setInput_text(getInput_text() + "Thursday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");

                                    }
                                }
                                if (documentId.equals("Friday")) {
                                    if (holiday) {
                                        frist.setText("");
                                        frifn.setText("");
                                        data = "Holiday";
                                        fritot.setText(data);
                                        setInput_text(getInput_text() + "Friday:,,,Holiday\n");

                                    } else if (sick) {
                                        frist.setText("");
                                        frifn.setText("");
                                        data = "Sick";
                                        fritot.setText(data);
                                        setInput_text(getInput_text() + "Friday:,,,Sick\n");

                                    } else {
                                        frist.setText(start);
                                        frifn.setText(finish);
                                        fritot.setText(hours);
                                        setInput_text(getInput_text() + "Friday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");

                                    }
                                }
                                if (documentId.equals("Saturday")) {
                                    if (holiday) {
                                        satst.setText("");
                                        satfn.setText("");
                                        data = "Holiday";
                                        sattot.setText(data);
                                        setInput_text(getInput_text() + "Saturday:,,,Holiday\n");

                                    } else if (sick) {
                                        satst.setText("");
                                        satfn.setText("");
                                        data = "Sick";
                                        sattot.setText(data);
                                        setInput_text(getInput_text() + "Saturday:,,,Sick\n");

                                    } else {
                                        satst.setText(start);
                                        satfn.setText(finish);
                                        sattot.setText(hours);
                                        setInput_text(getInput_text() + "Saturday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");
                                    }
                                }
                                if (documentId.equals("Sunday")) {
                                    if (holiday) {
                                        sunst.setText("");
                                        sunfn.setText("");
                                        data = "Holiday";
                                        suntot.setText(data);
                                        setInput_text(getInput_text() + "Sunday:,,,Holiday\n");

                                    } else if (sick) {
                                        sunst.setText("");
                                        sunfn.setText("");
                                        data = "Sick";
                                        suntot.setText(data);
                                        setInput_text(getInput_text() + "Sunday:,,,Sick\n");

                                    } else {
                                        sunst.setText(start);
                                        sunfn.setText(finish);
                                        suntot.setText(hours);
                                        setInput_text(getInput_text() + "Sunday:," + start + "," + finish + "," + hours + "," + y_or_n + "\n");
                                    }
                                }
                            }
                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", tot_count);

                            setInput_text(getInput_text() + ",,Total Hours:," + tot_count + "\n");
                            setTotal(finalTime);
                            totalHours.setText(finalTime);
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEmail_subject() {
        return email_subject;
    }

    public void setEmail_subject(String email_subject) {
        this.email_subject = email_subject;
    }

    public String getInput_text() {
        return input_text;
    }

    public void setInput_text(String input_text) {
        this.input_text = input_text;
    }

    public float getLunch() {
        return lunch;
    }

    public void setLunch(float lunch) {
        this.lunch = lunch;
    }
}