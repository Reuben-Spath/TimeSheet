package com.Spath_Family.TimeSheet1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FrontScreenManager extends FragmentActivity {

    private static final String TAG = "FrontScreenManager";

    Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private TextView week_ending;
    private TextView mng_code;
    private Button send;
    private ImageView logoM;

    private String letter;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> username = new ArrayList<>();
    //    private String onetwothree;
    private Planet[] planets;
    private ArrayAdapter<Planet> listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen_manager);


        mAuth = FirebaseAuth.getInstance();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();

        logoM = findViewById(R.id.logoM);
        week_ending = findViewById(R.id.tvWeekEnding);
        mng_code = findViewById(R.id.tvmgCode);


        String week_end = "Week Ending: " + weekEnding();
        week_ending.setText(week_end);
        employerCounter();
        empListner();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void empListner() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference docRef = db.collection("Company").document(user.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
//                        Log.d(TAG, "Current data: " + snapshot.getData());
                        mng_code.setText(snapshot.getString("empCode"));
                    } else {
//                        Log.d(TAG, "Current data: null");
                    }
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

        DateFormat df = new SimpleDateFormat("dd-MMM", Locale.getDefault());

        return df.format(calendar.getTime());
    }

    public void employerCounter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final ArrayList<String> names = new ArrayList<>();
        if (currentUser != null) {
            String mUid = currentUser.getUid();

            db.collection("Company").document(mUid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            setLetter(documentSnapshot.getString("empCode"));


                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

                            db1.collection("Users")
                                    .whereEqualTo("Employer Code", getLetter())// <-- This line
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (final DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                                    Log.d(TAG, "onComplete: " + document.getString("name"));

                                                    String name = document.getString("name");
                                                    String id = document.getId();

                                                    names.add(name);
                                                    username.add(id);
                                                }
                                                ListView mainListView = findViewById(R.id.mainListView);

                                                // When item is tapped, toggle checked properties of CheckBox and Planet.
                                                mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View item,
                                                                            int position, long id) {
                                                        Planet planet = listAdapter.getItem(position);
                                                        Objects.requireNonNull(planet).toggleChecked();
                                                        PlanetViewHolder viewHolder = (PlanetViewHolder) item.getTag();

                                                        Intent i = new Intent(FrontScreenManager.this, Employee.class);

                                                        String foundername = username.get(position);

                                                        i.putExtra("name", planet.getName());
                                                        i.putExtra("id", foundername);
                                                        startActivity(i);
                                                    }
                                                });

                                                planets = (Planet[]) getLastCustomNonConfigurationInstance();

                                                Planet[] planets = new Planet[names.size()];
                                                for (int i = 0; i < names.size(); i++) {
                                                    planets[i] = new Planet(names.get(i));
                                                }

                                                ArrayList<Planet> planetList = new ArrayList<>(Arrays.asList(planets));

                                                // Set our custom array adapter as the ListView's adapter.
                                                listAdapter = new PlanetArrayAdapter(getApplicationContext(), planetList);
                                                mainListView.setAdapter(listAdapter);

                                            } else {
//                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return planets;
    }

    /**
     * Holds planet data.
     */
    private static class Planet {
        private String name = "";
        private String id = "";
        private boolean checked = false;

        public Planet() {
        }

        Planet(String name) {
            this.name = name;
        }

        public Planet(String name, boolean checked) {
            this.name = name;
            this.checked = checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        boolean isChecked() {
            return checked;
        }

        void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String toString() {
            return name;
        }

        void toggleChecked() {
            checked = !checked;
        }
    }

    /**
     * Holds child views for one row.
     */
    private static class PlanetViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public PlanetViewHolder() {
        }

        PlanetViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /**
     * Custom adapter for displaying an array of Planet objects.
     */
    private static class PlanetArrayAdapter extends ArrayAdapter<Planet> {

        private LayoutInflater inflater;

        PlanetArrayAdapter(Context context, List<Planet> planetList) {
            super(context, R.layout.simplerow, R.id.rowTextView, planetList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Planet to display
            Planet planet = this.getItem(position);

            // The child views in each row.
            CheckBox checkBox;
            TextView textView;

            // Create a new row view
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = convertView.findViewById(R.id.rowTextView);
                checkBox = convertView.findViewById(R.id.CheckBox01);

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag(new PlanetViewHolder(textView, checkBox));

                // If CheckBox is toggled, update the planet it is tagged with.
                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Planet planet = (Planet) cb.getTag();
                        planet.setChecked(cb.isChecked());
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            // Tag the CheckBox with the Planet it is displaying, so that we can
            // access the planet in onClick() when the CheckBox is toggled.
            checkBox.setTag(planet);

            // Display planet data
            if (planet != null) {
                checkBox.setChecked(planet.isChecked());
            }
            if (planet != null) {
                textView.setText(planet.getName());
            }

            return convertView;
        }

    }
}


//    public void info(String empName){
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        if (currentUser != null) {
//
//            db.collection("Users").document(empName).collection(weekEnding())
//                    .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                            if (e != null) {
//                                return;
//                            }
//
//                            float tot_count = 0;
//                            float tot_time;
//                            String data = "";
//                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                NoteEmployee noteEmployee = documentSnapshot.toObject(NoteEmployee.class);
//
//                                noteEmployee.setDocumentId(documentSnapshot.getId());
//                                String documentId = noteEmployee.getDocumentId();
//                                String start = noteEmployee.getSignInN();
//                                String finish = noteEmployee.getSignOutN();
//                                String time = noteEmployee.getTimeStr();
//                                tot_time = noteEmployee.getTimeInt();
//                                boolean holiday = noteEmployee.isIfHoliday();
//                                boolean sick = noteEmployee.isIfSick();
//                                boolean lunch = noteEmployee.getIfLunch();
//                                String hadlunch = "No";
//
//                                if (lunch) {
//                                    hadlunch = "Yes";
//                                }
//                                if (start == null) {
//                                    start = "Invalid";
//                                }
//                                if (finish == null) {
//                                    finish = "Invalid";
//                                }
//                                if (time == null) {
//                                    time = "Invalid";
//                                }
//                                if (!time.contains("hours") && !time.equalsIgnoreCase("Invalid")) {
//                                    time = time + " hours";
//                                }
//
//                                tot_count += tot_time;
//
//                                Log.d(TAG, "onEvent: " + data);
//
//
//                                if (documentId.equals("Monday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Tuesday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Wednesday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Thursday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Friday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Saturday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else {
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    }
//                                }
//                                if (documentId.equals("Sunday")) {
//                                    if (holiday) {
//                                        data = documentId + ":\nHoliday\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (sick) {
//                                        data = documentId + ":\nSick\n";
//                                        setTrial(getTrial()+data);
//                                    } else if (!documentId.isEmpty()){
//                                        data = documentId + ":\nStart time: " + start + "\nFinish time: " + finish + "\nTime worked: " + time + "\nLunch: " + hadlunch + "\n";
//                                        setTrial(getTrial()+data);
//                                    } else{
//                                        setTrial(getTrial()+" \nDid not work on Sunday");
//                                    }
//                                }
//                            }
//                            String finalTime = String.format(Locale.getDefault(), "Total Hours:\n%.2f hours", tot_count);
//
//                            setTotal(finalTime);
//                            totalHours.setText(finalTime);
//                        }
//                    });
//        }
//    }
//    public void send_all() {
//
//        String info_subject = weekEnding()+" "+getUserId();
//        if (getMon() == null) {
//            setMon("Did not work on Monday");
//        }
//        if (getTues() == null) {
//            setTues("Did not work on Tuesday");
//        }
//        if (getWed() == null) {
//            setWed("Did not work on Wednesday");
//        }
//        if (getThurs() == null) {
//            setThurs("Did not work on Thursday");
//        }
//        if (getFri() == null) {
//            setFri("Did not work on Friday");
//        }
//        if (getSat() == null) {
//            setSat("Did not work on Saturday");
//        }
//        if (getSun() == null) {
//            setSun("Did not work on Sunday");
//        }
//
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT,info_subject);
//        shareIntent.putExtra(Intent.EXTRA_TEXT,  getTotal() + "\n\n" + getMon() + "\n" + getTues() + "\n" + getWed() + "\n" + getThurs() + "\n" + getFri() + "\n" + getSat() + "\n" + getSun());
//        shareIntent.setType("text/*");
//        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.chooser_text)));
//    }
//

//            db.collection("Company").document(mUid)
//                    .get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(final DocumentSnapshot documentSnapshot) {
//
//                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
//                            String code = documentSnapshot.getString("empCode");
//
//                            db1.collection("Users")
//                                    .whereEqualTo("Employer Code", code) // <-- This line
//                                    .get()
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                int counter = 0;
//                                                LinearLayout lLayout = findViewById(R.id.llLayout); // Root ViewGroup in which you want to add textviews
//
//                                                for (final DocumentSnapshot document : task.getResult()) {
//                                                    Log.d(TAG, document.getId() + " => " + document.getData());
//
//                                                    Log.d(TAG, "onComplete: " + document.getString("name"));
//
//                                                    String name = document.getString("name");
//
//                                                    counter++;
//
//                                                    // Prepare textview object programmatically
//                                                    TextView tv = new TextView(getApplicationContext());
//
//                                                    tv.setText(name);
//                                                    tv.setId(counter);
//                                                    tv.setTextSize(24);
//                                                    tv.setTextColor(getResources().getColor(R.color.Blue_dark));
//                                                    tv.setPadding(10, 15, 10, 15);
//
//                                                    lLayout.addView(tv);
//
//                                                    tv.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            String id = document.getId();
//                                                            String userName = document.getString("name");
//
//                                                            Intent i = new Intent(FrontScreenManager.this, Employee.class);
//
//                                                            i.putExtra("name", userName);
//                                                            i.putExtra("id", id);
//                                                            startActivity(i);
//                                                        }
//                                                    });
//                                                }
//                                            } else {
//                                                Log.d(TAG, "Error getting documents: ", task.getException());
//                                            }
//                                        }
//                                    })
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                            Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.getDocuments());
//                                        }
//                                    });
//                        }
//                    });
//}
//        }