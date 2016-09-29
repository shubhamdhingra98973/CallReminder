package in.dhingra_shubham.currentcallhistory;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Provider;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public  static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    public  static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
    DBHelper db;
    TextView tv,tv2;
    Button btn1;
    boolean isTimerSet=false;
    ComponentName receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);
        tv2=(TextView) findViewById(R.id.msg1);
        tv2.setText(timeHour + ":" + timeMinute);
        isTimerSet=false;
        btn1 = (Button) findViewById(R.id.setbutton);
        Typeface face4 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-BoldItalic.ttf");
        tv.setTypeface(face4);
         receiver = new ComponentName(this, PhListener.class);

        View.OnClickListener listener1 = new View.OnClickListener() {
            public void onClick(View view) {
                if (isTimerSet) {
                    showAlertDialog(MainActivity.this, "Alert",
                            "Cancel the previous Reminder", false);
                } else {
                    tv2.setText("");
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.HOUR, timeHour);
                    bundle.putInt(Constants.MINUTE, timeMinute);
                    MyDialogFragment fragment = new MyDialogFragment(new MyHandler());
                    fragment.setArguments(bundle);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(fragment, Constants.TIME_PICKER);
                    transaction.commit();
                isTimerSet=true;
                }
        }};
        btn1.setOnClickListener(listener1);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage (Message msg){
            Bundle bundle = msg.getData();
            timeHour = bundle.getInt(Constants.HOUR);
            timeMinute = bundle.getInt(Constants.MINUTE);
            Toast.makeText(MainActivity.this, "Timer Set ", Toast.LENGTH_SHORT).show();
            setAlarm();
        }
    }
    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timeHour);
        calendar.set(Calendar.MINUTE, timeMinute);
        tv2.setText(timeHour + ":" + timeMinute);
        PhListener.setTime_and_hour();
    }

    public void stopService(View view)
    {
        Toast.makeText(MainActivity.this, "Reminder Cancelled", Toast.LENGTH_SHORT).show();
        PhListener.cancelReminder();
    }
    public void enableBroadcastReceiver(View view)
    {
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(this, "Service Start !!", Toast.LENGTH_SHORT).show();
    }

    public void disableBroadcastReceiver(View view)
    {
        PhListener.cancelReminder();
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(this, "Service Stop !!", Toast.LENGTH_SHORT).show();
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
}