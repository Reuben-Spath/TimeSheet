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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

//information and total time need to be added together
public class EmployeeWeek extends AppCompatActivity implements exampleDialog.ExampleDialogistener {

    private Button back;
    private Button save;

    private ImageView history;

    private TextView EmpCode;

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

    private String total;
    private String name;
    private String week_header;
    private String week_pass;

    private String email_subject;

    private String userId;

    private FirebaseAuth mAuth;

    private String input_text = "";
    private String fileName = "config.csv";

    private float lunch = (float) 0.5;

//    private static final String TAG = "EmployeeWeek";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_week);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent i = getIntent();
        setUserId(i.getStringExtra("name"));

        EmpCode = findViewById(R.id.tvEmpCode);

        FrontScreenEmployee frontScreenEmployee = new FrontScreenEmployee();
        setWeek_pass(frontScreenEmployee.history_maker());
        week_header = "Week Ending:\n" + getWeek_pass();

        back = findViewById(R.id.btBackEdit);
        save = findViewById(R.id.btSaveEdit);

        history = findViewById(R.id.draw_history);

        weekEnding = findViewById(R.id.tvWeekEnding);
        totalHours = findViewById(R.id.tvTotalHours);

        mAuth = FirebaseAuth.getInstance();

        TextView monday = findViewById(R.id.Monday);
        TextView tuesday = findViewById(R.id.Tuesday);
        TextView wednesday = findViewById(R.id.Wednesday);
        TextView thursday = findViewById(R.id.Thursday);
        TextView friday = findViewById(R.id.Friday);
        TextView saturday = findViewById(R.id.Saturday);
        TextView sunday = findViewById(R.id.Sunday);

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
    public void onStart() {
        super.onStart();
        final FrontScreenEmployee frontScreenEmployee = new FrontScreenEmployee();
        info(frontScreenEmployee.history_maker());
        setEmail_subject(frontScreenEmployee.history_maker() + " " + getUserId());
        empListner();

        weekEnding.setText(week_header);

        setInput_text("Day:, Start Time:, Finish Time:, Total Time:, Lunch: \n");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
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
            String mUid = user.getUid();

            Toast.makeText(this, mUid, Toast.LENGTH_SHORT).show();
            db.collection("Users").document(mUid).collection("History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    if (document.getData().containsValue(input_text)) {
                                        setInput_text("Day:, Start Time:, Finish Time:, Total Time:, Lunch: \n");
                                        info(input_text);

                                        String pass = "Week Ending:\n" + input_text;
                                        weekEnding.setText(pass);
                                        setEmail_subject(input_text + " " + getUserId());
                                        Toast.makeText(EmployeeWeek.this, "Successfully Loaded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EmployeeWeek.this, "Please enter the correct format (e.g 28-07-19)", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(EmployeeWeek.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        EmpCode.setText(snapshot.getString("empCode"));
                    }
                }
            });
        }
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public void info(final String week_ending) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            String mUid = currentUser.getUid();
            setName(currentUser.getUid());

            db.collection("Users").document(mUid).collection(week_ending)
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

                                String start = "";
                                String finish = "";

                                boolean lunch_bool = false;
                                boolean holiday = false;
                                boolean sick = false;

                                if (documentSnapshot.get("String_s") != null) {
                                    start = documentSnapshot.getString("String_s");
                                }
                                if (documentSnapshot.get("String_f") != null) {
                                    finish = documentSnapshot.getString("String_f");
                                }
                                if (documentSnapshot.get("bool_s") != null) {
                                    sick = documentSnapshot.getBoolean("bool_s");
                                }
                                if (documentSnapshot.get("bool_h") != null) {
                                    holiday = documentSnapshot.getBoolean("bool_h");
                                }
                                if (documentSnapshot.get("bool_l") != null) {
                                    lunch_bool = documentSnapshot.getBoolean("bool_l");
                                }

                                String y_or_n;
                                if (lunch_bool) {
                                    tot_time = (noteEmployee.getFinish() - noteEmployee.getStart()) - getLunch();
                                    if (tot_time < 0) {
                                        tot_time += 24;
                                    }
                                    y_or_n = "Y";
                                } else {
                                    tot_time = noteEmployee.getFinish() - noteEmployee.getStart();
                                    if (tot_time < 0) {
                                        tot_time += 24;
                                    }
                                    y_or_n = "N";
                                }

                                tot_time = round(tot_time,2);
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
                                if (start.equalsIgnoreCase("") && finish.equalsIgnoreCase("")) {
                                    y_or_n = "";
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

    public String getWeek_pass() {
        return week_pass;
    }

    public void setWeek_pass(String week_pass) {
        this.week_pass = week_pass;
    }

    public String getEmail_subject() {
        return email_subject;
    }

    public void setEmail_subject(String email_subject) {
        this.email_subject = email_subject;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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
