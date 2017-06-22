package com.wieland.www.scheduletest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    EditText classes, courses;
    Button confirm;
    Context context;

    public static final String CLASSES_NAME = "Classes";
    public static final String COURSES_NAME = "Courses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        classes = (EditText) findViewById(R.id.editTextClass);
        courses = (EditText) findViewById(R.id.editTextCourses);
        confirm = (Button) findViewById(R.id.buttonSettingsConfirm);
        context = this;

        SharedPreferences sharedPreferences = context.getSharedPreferences("Tralala", MODE_PRIVATE);

        classes.setText(sharedPreferences.getString(CLASSES_NAME, ""));
        courses.setText(sharedPreferences.getString(COURSES_NAME, ""));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(COURSES_NAME, courses.getText().toString());
                editor.putString(CLASSES_NAME, classes.getText().toString());
                editor.commit();
            }
        });
    }
}
