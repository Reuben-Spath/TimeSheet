package com.example.timesheet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class gridLayout extends AppCompatActivity {
    private static final String FILE_NAME = "example.txt";

    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);

        mEditText = findViewById(R.id.edit_text);
    }

    public void save(View v) {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

            mEditText.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
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

    public void load(View v) {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
//public class gridLayout extends FragmentActivity {
//    private Planet[] planets;
//    private ArrayAdapter<Planet> listAdapter;
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_grid_layout);
//
//        // Find the ListView resource.
//        ListView mainListView = findViewById(R.id.mainListView);
//
//        // When item is tapped, toggle checked properties of CheckBox and Planet.
//        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View item,
//                                    int position, long id) {
//                Planet planet = listAdapter.getItem(position);
//                planet.toggleChecked();
//                Toast.makeText(gridLayout.this, "yes", Toast.LENGTH_SHORT).show();
//                PlanetViewHolder viewHolder = (PlanetViewHolder) item.getTag();
//                viewHolder.getCheckBox().setChecked(planet.isChecked());
//            }
//        });
//
//
//        // Create and populate planets.
//        planets = (Planet[]) getLastCustomNonConfigurationInstance();
//        if (planets == null) {
//            planets = new Planet[]{
//                    new Planet("Mercury"), new Planet("Venus"), new Planet("Earth"),
//                    new Planet("Mars"), new Planet("Jupiter"), new Planet("Saturn"),
//                    new Planet("Uranus"), new Planet("Neptune"), new Planet("Ceres"),
//                    new Planet("Pluto"), new Planet("Haumea"), new Planet("Makemake"),
//                    new Planet("Eris"), new Planet("Epsilon Eridani"), new Planet("Gliese 876 b"),
//                    new Planet("HD 209458 b")
//            };
//        }
//        ArrayList<Planet> planetList = new ArrayList<Planet>();
//        planetList.addAll(Arrays.asList(planets));
//
//        // Set our custom array adapter as the ListView's adapter.
//        listAdapter = new PlanetArrayAdapter(this, planetList);
//        mainListView.setAdapter(listAdapter);
//    }
//
//    public Object onRetainCustomNonConfigurationInstance() {
//        return planets;
//    }
//
//    /**
//     * Holds planet data.
//     */
//    private static class Planet {
//        private String name = "";
//        private boolean checked = false;
//
//        public Planet() {
//        }
//
//        public Planet(String name) {
//            this.name = name;
//        }
//
//        public Planet(String name, boolean checked) {
//            this.name = name;
//            this.checked = checked;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public boolean isChecked() {
//            return checked;
//        }
//
//        public void setChecked(boolean checked) {
//            this.checked = checked;
//        }
//
//        public String toString() {
//            return name;
//        }
//
//        public void toggleChecked() {
//            checked = !checked;
//        }
//    }
//
//    /**
//     * Holds child views for one row.
//     */
//    private static class PlanetViewHolder {
//        private CheckBox checkBox;
//        private TextView textView;
//
//        public PlanetViewHolder() {
//        }
//
//        public PlanetViewHolder(TextView textView, CheckBox checkBox) {
//            this.checkBox = checkBox;
//            this.textView = textView;
//        }
//
//        public CheckBox getCheckBox() {
//            return checkBox;
//        }
//
//        public void setCheckBox(CheckBox checkBox) {
//            this.checkBox = checkBox;
//        }
//
//        public TextView getTextView() {
//            return textView;
//        }
//
//        public void setTextView(TextView textView) {
//            this.textView = textView;
//        }
//    }
//
//    /**
//     * Custom adapter for displaying an array of Planet objects.
//     */
//    private static class PlanetArrayAdapter extends ArrayAdapter<Planet> {
//
//        private LayoutInflater inflater;
//
//        public PlanetArrayAdapter(Context context, List<Planet> planetList) {
//            super(context, R.layout.simplerow, R.id.rowTextView, planetList);
//            // Cache the LayoutInflate to avoid asking for a new one each time.
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // Planet to display
//            Planet planet = this.getItem(position);
//
//            // The child views in each row.
//            CheckBox checkBox;
//            TextView textView;
//
//            // Create a new row view
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.simplerow, null);
//
//                // Find the child views.
//                textView = convertView.findViewById(R.id.rowTextView);
//                checkBox = convertView.findViewById(R.id.CheckBox01);
//
//                // Optimization: Tag the row with it's child views, so we don't have to
//                // call findViewById() later when we reuse the row.
//                convertView.setTag(new PlanetViewHolder(textView, checkBox));
//
//                // If CheckBox is toggled, update the planet it is tagged with.
//                checkBox.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        CheckBox cb = (CheckBox) v;
//                        Planet planet = (Planet) cb.getTag();
//                        planet.setChecked(cb.isChecked());
//                    }
//                });
//            }
//            // Reuse existing row view
//            else {
//                // Because we use a ViewHolder, we avoid having to call findViewById().
//                PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
//                checkBox = viewHolder.getCheckBox();
//                textView = viewHolder.getTextView();
//            }
//
//            // Tag the CheckBox with the Planet it is displaying, so that we can
//            // access the planet in onClick() when the CheckBox is toggled.
//            checkBox.setTag(planet);
//
//            // Display planet data
//            checkBox.setChecked(planet.isChecked());
//            textView.setText(planet.getName());
//
//            return convertView;
//        }
//
//    }
//}