package com.example.timesheet;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

//information and total time need to be added together
public class EmployeeWeek extends FrontScreenEmployee {

    private Button back;
    private Button save;

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

    private String name;
    private String week_header;

    private String sendInfo;

    private FirebaseAuth mAuth;

    private static final String TAG = "EmployeeWeek";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_week);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        EmpCode = findViewById(R.id.tvEmpCode);

        week_header = "Week Ending:\n" + weekEnding();

        back = findViewById(R.id.btBackEdit);
        save = findViewById(R.id.btSaveEdit);


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
//    View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

    @Override
    public void onStart() {
        super.onStart();
        info();
        empListner();
//        save();
//        load();
        String text = monst + " " + monfn + " " + montot;


//        writeToFile(text);

        weekEnding.setText(week_header);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                send();
//                Bitmap bitmap = takeScreenshot();
//                saveBitmap(bitmap);
//                Bitmap bitmap = getScreenShot();
//                store(bitmap,"tester");
//                shareImage();
                takeScreenshot();


//                csvCode();
            }
        });
    }

    public Bitmap getScreenShot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private void shareImage(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    private void takeScreenshot() {
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "Tester" + ".jpg";

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
//            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

//            Toast.makeText(this, imageFile.getPath(), Toast.LENGTH_SHORT).show();
            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public void send() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tester");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getSendInfo());
        shareIntent.setType("text/csv");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
    }
//    public Bitmap takeScreenshot() {
//        View rootView = findViewById(android.R.id.content).getRootView();
//        rootView.setDrawingCacheEnabled(true);
//        return rootView.getDrawingCache();
//    }

//    public void saveBitmap(Bitmap bitmap) {
//        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(imagePath);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//            Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            Log.e("GREC", e.getMessage(), e);
//            Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            Log.e("GREC", e.getMessage(), e);
//            Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
//
//        }
//    }
//
//    //
//    private static final String FILE_NAME = "example.csv";
//
////    EditText mEditText;
//
//    public void save() {
//        String text = monst+" "+monfn+" "+montot;
////                mEditText.getText().toString();
//
//        FileOutputStream fos = null;
//
//        try {
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            fos.write(text.getBytes());
//
////            mEditText.getText().clear();
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
////
//    public void load() {
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
//            setSendInfo(sb.toString());
////            mEditText.setText(sb.toString());
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
//    final File path =
//            Environment.getExternalStoragePublicDirectory
//                    (
//                            //Environment.DIRECTORY_PICTURES
//                            Environment.DIRECTORY_DOCUMENTS + "/YourFolder/"
//                    );

//    public void writeToFile(String data) {
//        // Get the directory for the user's public pictures directory.
//
//
//        // Make sure the path directory exists.
//        if (!path.exists()) {
//            // Make it, if it doesn't exit
//            path.mkdirs();
//        }
//
//        final File file = new File(path, "example.txt");
//
//        // Save your stream, don't forget to flush() it before closing it.
//
//        try {
//            file.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(file);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            myOutWriter.append(data);
//
//            myOutWriter.close();
//
//            fOut.flush();
//            fOut.close();
//        } catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }

//    public void textReader(){
//        StringBuilder text = new StringBuilder();
//        try {
//            File sdcard = Environment.getExternalStorageDirectory();
//            File file = new File(sdcard,"testFile.txt");
//
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//                text.append('\n');
//            }
//            br.close() ;
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        TextView tv = (TextView)findViewById(R.id.amount);
////        tv.setText(text.toString()); ////Set the text to text view.
//    }

//    public void csvCode() {
//        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//        sharingIntent.setType("text/*");
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path.toString()));
//        startActivity(Intent.createChooser(sharingIntent, "share file with"));
//    }

//    public void send() {
//        Toast.makeText(this, getSendInfo(), Toast.LENGTH_SHORT).show();
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tester");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, getSendInfo());
//        shareIntent.setType("text/csv");
//        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
//    }



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

    public void info() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {

            String mUid = currentUser.getUid();
            setName(currentUser.getUid());


            db.collection("Users").document(mUid).collection(weekEnding())
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

    public String getSendInfo() {
        return sendInfo;
    }

    public void setSendInfo(String sendInfo) {
        this.sendInfo = sendInfo;
    }
}

// db.collection("Users").document(mUid).collection(weekEnding())
//                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                            if (e != null) {
//                                return;
//                            }
//
//                            float data = 0;
//                            float time;
//                            boolean holiday;
//                            boolean sick;
//                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);
//
//                                time = noteEmployee.getTimeInt();
//
//                                holiday = noteEmployee.isIfHoliday();
//                                sick = noteEmployee.isIfSick();
//
//                                data += time;
//
//                                if (holiday || sick) {
//                                    data -= time;
//                                }
//
//                                Log.d(TAG, "onEvent: " + data);
//
//                            }
//
//                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", data);
//
//                            totalHours.setText(finalTime);
//                        }
//                    });