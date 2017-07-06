package com.wieland.www.scheduletest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.width;
import static com.wieland.www.scheduletest.R.attr.height;

public class SettingsActivity extends AppCompatActivity {
    EditText customQuery;
    Button confirm;
    Context context;
    ArrayList<String> classesList, coursesList;
    Switch aSwitch;
    ListView listClasses, listCourses;

    public static final String CLASSES_NAME = "Classes";
    public static final String COURSES_NAME = "Courses";
    public static final String CUSTOMSQL_NAME = "CustomSQL";
    public static final String COLORS_ENABLED = "Colors_Enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        customQuery = (EditText) findViewById(R.id.editTextCustomSQL);
        confirm = (Button) findViewById(R.id.buttonSettingsConfirm);
        context = this;

        aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putBoolean(COLORS_ENABLED, aSwitch.isChecked());
                editor.commit();
            }
        });

        listClasses = (ListView) findViewById(R.id.listClasses);
        listCourses = (ListView) findViewById(R.id.listCourses);

        final SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        classesList = unscrambleRaw(pref.getString(CLASSES_NAME, ""));
        coursesList = unscrambleRaw(pref.getString(COURSES_NAME, ""));
        aSwitch.setChecked(pref.getBoolean(COLORS_ENABLED, true));

        listClasses.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, classesList));
        listCourses.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, coursesList));
        final Context context = this;

        listClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = getDialog("Klasse", classesList, position, CLASSES_NAME, listClasses, true);
                builder.show();
            }
        });
        listCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = getDialog("Kurs", coursesList, position, COURSES_NAME, listCourses, false);
                builder.show();
            }
        });
        Layout_Row.setListViewHeightBasedOnItems(listClasses);
        Layout_Row.setListViewHeightBasedOnItems(listCourses);

        customQuery.setText(pref.getString(CUSTOMSQL_NAME, ""));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(CUSTOMSQL_NAME, customQuery.getText().toString());
                editor.commit();
            }
        });
    }

    public ArrayList<String> unscrambleRaw(String rawList) {
        ArrayList<String> unscrambledList = new ArrayList<>();
        String buffer = "";

        for (int i = 0; i < rawList.length(); i++) {
            if ((rawList.charAt(i) == ';') || (i == (rawList.length() - 1))) {
                if (i == (rawList.length() - 1))
                    buffer = buffer + rawList.charAt(i);
                unscrambledList.add(buffer);
                buffer = "";
            } else {
                buffer = buffer + rawList.charAt(i);
            }
        }
        unscrambledList.add("+ Hinzufügen");
        return unscrambledList;
    }

    private AlertDialog.Builder getDialog(String title, final ArrayList<String> list, final int position, final String sharedPreferenceName, final ListView listView, final boolean classes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (position != (list.size() - 1))
            input.setText(list.get(position));
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = "";
                if (position != (list.size() - 1)) {
                    for (int i = 0; i < position; i++) {
                        m_Text = m_Text + list.get(i) + ";";
                    }
                    m_Text = m_Text + input.getText().toString();
                    if ((position + 1) < (list.size() - 1))
                        m_Text = m_Text + ";";
                    for (int i = position + 1; i < (list.size() - 1); i++) {
                        m_Text = m_Text + list.get(i);
                        if (i != (list.size() - 2))
                            m_Text = m_Text + ";";
                    }
                } else {
                    for (int i = 0; i < (list.size() - 1); i++) {
                        m_Text = m_Text + list.get(i) + ";";
                    }
                    m_Text = m_Text + input.getText().toString();
                }
                if (m_Text != null && m_Text.length() > 0 && m_Text.charAt(m_Text.length() - 1) == ';')
                    m_Text = m_Text.substring(0, m_Text.length() - 1);
                SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(sharedPreferenceName, m_Text);
                editor.commit();
                ArrayList<String> unscrambled = unscrambleRaw(m_Text);
                if (unscrambled.get(unscrambled.size() - 1) != "+ Hinzufügen")
                    unscrambled.add("+ Hinzufügen");
                listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                        android.R.id.text1, unscrambled));
                listView.deferNotifyDataSetChanged();
                Layout_Row.setListViewHeightBasedOnItems(listView);
                if (classes)
                    classesList = unscrambled;
                else
                    coursesList = unscrambled;
            }
        });
        builder.setNeutralButton("Entfernen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = "";
                for (int i = 0; i < position; i++) {
                    m_Text = m_Text + list.get(i);
                    m_Text = m_Text + ";";
                }
                for (int i = position + 1; i < (list.size() - 1); i++) {
                    m_Text = m_Text + list.get(i);
                    m_Text = m_Text + ";";
                }
                if (m_Text != null && m_Text.length() > 0 && m_Text.charAt(m_Text.length() - 1) == ';')
                    m_Text = m_Text.substring(0, m_Text.length() - 1);
                SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(sharedPreferenceName, m_Text);
                editor.commit();
                ArrayList<String> unscrambled = unscrambleRaw(m_Text);
                if (unscrambled.get(unscrambled.size() - 1) != "+ Hinzufügen")
                    unscrambled.add("+ Hinzufügen");
                listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                        android.R.id.text1, unscrambled));
                listView.deferNotifyDataSetChanged();
                Layout_Row.setListViewHeightBasedOnItems(listView);
                if (classes)
                    classesList = unscrambled;
                else
                    coursesList = unscrambled;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }
}
