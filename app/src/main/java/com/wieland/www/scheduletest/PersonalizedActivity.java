package com.wieland.www.scheduletest;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class PersonalizedActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    ListView listView;
    Switch aSwitch;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalized);

        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.personalized_list);
        aSwitch = (Switch) findViewById(R.id.switch1);
        textView = (TextView) findViewById(R.id.textView2);

        final Context context = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleHandler scheduleHandler = new ScheduleHandler(1, context);
                try {
                    String a = editText.getText().toString();
                    if((editText.getText().toString().charAt(0) != '0') && (editText.getText().toString().charAt(0) != '1') && (editText.getText().toString().charAt(0) != '2'))
                        a = "0" + a;
                    ArrayList<String> list;
                    String command;
                    if (!aSwitch.isChecked())
                        command = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COL_1 + " = '" + a + "'";
                    else
                        command = "SELECT * FROM " + DatabaseHelper.TABLE_NAME2 + " WHERE " + DatabaseHelper.COL_1 + " = '" + a + "'";

                    textView.setText(command);
                    list = scheduleHandler.getCustomizedClassInfo(command);

                    listView.setAdapter(new ArrayAdapter<>(context,
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            list));
                } catch (Exception e) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("Fehler");
                    alertDialogBuilder.setMessage(e.getMessage());
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
        });
    }
}
