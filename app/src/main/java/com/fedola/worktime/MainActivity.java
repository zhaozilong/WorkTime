package com.fedola.worktime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataHelper helper = new DataHelper(this);
        helper.getWritableDatabase();
        super.setContentView(R.layout.activity_main);
        initView();//初期化

    }

    private void initView()
    {
        /*
             初期化
         */
        TextView workDay = (TextView) findViewById(R.id.workDay);
        final Calendar calendar = Calendar.getInstance();
        String str = calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
        workDay.setText(str);//仕事の日期を初期化する
        workDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;

                datePickerDialog = new DatePickerDialog(MainActivity.this, workDaySelect, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        EditText workTimeStart = (EditText) findViewById(R.id.workTimeStart);
        str = "9時00分";
        workTimeStart.setText(str);
        banding(workTimeStart);

        EditText workTimeEnd = (EditText) findViewById(R.id.workTimeEnd);
        str = "18時00分";
        workTimeEnd.setText(str);
        banding(workTimeEnd);

        EditText launchTimeStart = (EditText) findViewById(R.id.launchTimeStart);
        str = "12時00分";
        launchTimeStart.setText(str);
        banding(launchTimeStart);

        EditText launchTimeEnd = (EditText) findViewById(R.id.launchTimeEnd);
        str = "13時00分";
        launchTimeEnd.setText(str);
        banding(launchTimeEnd);

        EditText dinnerTimeStart = (EditText) findViewById(R.id.dinnerTimeStart);
        str = "18時00分";
        dinnerTimeStart.setText(str);

        banding(dinnerTimeStart);

        EditText dinnerTimeEnd = (EditText) findViewById(R.id.dinnerTimeEnd);
        str = "18時30分";
        dinnerTimeEnd.setText(str);
        banding(dinnerTimeEnd);

        EditText nighRestStart = (EditText) findViewById(R.id.nightRestStart);
        str = "21時30分";
        nighRestStart.setText(str);
        banding(nighRestStart);

        EditText nightRestEnd = (EditText) findViewById(R.id.nightRestEnd);
        str = "22時00分";
        nightRestEnd.setText(str);
        banding(nightRestEnd);
        /*
        今日の日付を初期化する
         */

    }
    public void banding(final EditText editText)
    {
       final TimePickerDialog.OnTimeSetListener timeSeclect = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String min = Integer.toString(minute);
                if (minute < 10)
                {
                    min="0"+Integer.toString(minute);
                }
                String str = hourOfDay+"時"+min+"分";

                editText.setText(str);

            }
        };

        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("EditTextの上で何があるの",editText.getText().toString());
                String editString = editText.getText().toString();
                 int hour = Integer.parseInt(editString.substring(0, editString.indexOf("時")));
                 int minutre = Integer.parseInt(editString.substring(editString.indexOf("時")+1, editString.indexOf("分")));
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(MainActivity.this,timeSeclect, hour,minutre,true);
                timePickerDialog.show();
            }
        });

    }
    DatePickerDialog.OnDateSetListener workDaySelect = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String str = year +"年"+(monthOfYear+1)+"月"+dayOfMonth+"日";
            TextView workDay = (TextView) findViewById(R.id.workDay);
            //Log.d("選択したの日期",str);
            workDay.setText(str);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
