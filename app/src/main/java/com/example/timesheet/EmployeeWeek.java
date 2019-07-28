package com.example.timesheet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Objects;

//information and total time need to be added together
public class EmployeeWeek extends AppCompatActivity implements exampleDialog.ExampleDialogistener {
    // extends FrontScreenEmployee
//    Calendar calendar = Calendar.getInstance();
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

//    private exampleDialog.ExampleDialogistener listener;

    private static final String TAG = "EmployeeWeek";

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

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        builder.detectFileUriExposure();

        Intent i = getIntent();
        setUserId(i.getStringExtra("name"));

        EmpCode = findViewById(R.id.tvEmpCode);

        FrontScreenEmployee frontScreenEmployee = new FrontScreenEmployee();
        setWeek_pass(frontScreenEmployee.weekEnding());
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
        FrontScreenEmployee frontScreenEmployee = new FrontScreenEmployee();
        info(frontScreenEmployee.history_maker());
        setEmail_subject(frontScreenEmployee.weekEnding() + " " + getUserId());
        empListner();

        weekEnding.setText(week_header);

//        writeToFile("hello",getApplicationContext());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeScreenshot();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history();
                openDialog();
            }
        });
    }

    @Override
    public void applyTexts(String editTextHistory) {
        Toast.makeText(this, editTextHistory, Toast.LENGTH_SHORT).show();
        info(editTextHistory);

        String pass = "Week Ending:\n" + editTextHistory;
        weekEnding.setText(pass);
        setEmail_subject(editTextHistory + " " + getUserId());

    }

    public void openDialog() {
        exampleDialog exampleDialog = new exampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void history() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String mUid = user.getUid();

            db.collection("Users").document(mUid).collection("History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
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

    public void info(final String week_ending) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            String mUid = currentUser.getUid();
            setName(currentUser.getUid());


            db.collection("Users").document(mUid).collection(week_ending)
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

                                    } else if (sick) {
                                        monst.setText("");
                                        monfn.setText("");
                                        data = "Sick";
                                        montot.setText(data);

                                    } else {
                                        monst.setText(start);
                                        monfn.setText(finish);
                                        montot.setText(hours);
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
                                        tuestot.setText(hours);
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
                                        wedtot.setText(hours);
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
                                        thurstot.setText(hours);
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
                                        fritot.setText(hours);
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
                                        sattot.setText(hours);
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
                                        suntot.setText(hours);
                                    }
                                }

                            }
                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", tot_count);


                            setTotal(finalTime);
                            totalHours.setText(finalTime);
                        }
                    });
        }
    }
    private void takeScreenshot() {
        String now = getEmail_subject();

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";


            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

//            openScreenshot(imageFile);
            sendScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void sendScreenshot(File imageFile) {
        Uri uri = Uri.fromFile(imageFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, getEmail_subject());

        intent.setType("image/*");

        String text_message = "Total hours worked are: " + getTotal();
        intent.putExtra(Intent.EXTRA_TEXT, text_message);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + "abc.txt");
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        startActivity(Intent.createChooser(sharingIntent, "share file with"));


        startActivity(Intent.createChooser(intent, "Share Image"));

//        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
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
}


//    private static final String FILE_NAME = "example.txt";
//
//    public void save(View v) {
//        String text = "hello";
//        FileOutputStream fos = null;
//
//        try {
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            fos.write(text.getBytes());
//
//            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
//                    Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void writeToFile(String data, Context context) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }
//    private String readFromFile(Context context) {
//
//        String ret = "";
//
//        try {
//            InputStream inputStream = context.openFileInput("config.txt");
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
//        }
//        catch (FileNotFoundException e) {
//            Log.e("login activity", "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
//        }
//
//        return ret;
//    }
//    public void load(View v) {
//        FileInputStream fis = null;
//
//        try {
//            fis = openFileInput(FILE_NAME);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String text;
//
//            while ((text = br.readLine()) != null) {
//                sb.append(text).append("\n");
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
