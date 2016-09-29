package in.dhingra_shubham.currentcallhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Shubham on 28-09-2016.
 */
@SuppressLint("SimpleDateFormat")
public class PhListener extends BroadcastReceiver {

    public static String number = null;
    public static String number1=null;
    public static boolean ishistory = false;
    public static String date = null;
    public static String duration = null;
    public static String type = null, time = null;
    public static Context cc;
    public static AlarmManager manager;
    public static Intent myIntent;
    public static PendingIntent pendingIntent;
    public static boolean isTimer=true;
    @Override
    public void onReceive(Context c, Intent i) {
        // TODO Auto-generated method stub
        cc = c;
        manager = (AlarmManager) c.getSystemService(c.ALARM_SERVICE);
        myIntent = new Intent(c, AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(c, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(" BROADCAST RECEIVER :", "STARTED");
        Bundle bundle = i.getExtras();
        if (bundle == null)
            return;

        SharedPreferences sp = c.getSharedPreferences("ZnSoftech", Activity.MODE_PRIVATE);
        String s = bundle.getString(TelephonyManager.EXTRA_STATE);
        Log.d("STATE PRINT-----   " + s, " ");
        if (i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String number = i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            sp.edit().putString("number", number).commit();
            sp.edit().putString("state", s).commit();
        } else if (s.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = bundle.getString("incoming_number");
            sp.edit().putString("number", number).commit();
            sp.edit().putString("state", s).commit();
        } else if (s.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            sp.edit().putString("state", s).commit();
        } else if (s.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            String state = sp.getString("state", null);
            if (!state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                sp.edit().putString("state", null).commit();
                History h = new History(new Handler(), c);
                c.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, h);

            }
            sp.edit().putString("state", s).commit();
        }

    }


    // Run your task here


    public static void getDetails(String number1, String date1, String time1, String duration1, String type1) {
        number = number1;
        date = date1;
        time = time1;
        duration = duration1;
        type = type1;
        ishistory = true;
        alertNotification(ishistory, type, number, duration);

    }

    private static void alertNotification(boolean ishistory, String type, final String number, String duration) {
        Toast.makeText(cc.getApplicationContext(), "CALL DISCONNECTED ", Toast.LENGTH_LONG).show();
       // PhListener.cancelReminder();
        if (ishistory && number!=null) {
            if (type.equals("OUTGOING") && duration.equals("0")) {
                AlertDialog alertDialog = new AlertDialog.Builder(cc.getApplicationContext())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                number1=number;
                                setReminder();
                            }
                        })
                        .setTitle("Hello User !!")
                        .setMessage("Do you want to set a reminder to call  " + number)
                        .setCancelable(false)
                        .setNegativeButton("No", null)
                        .create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alertDialog.show();
            }
        }
    }
    public static Calendar setTime_and_hour()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, MainActivity.timeHour);
        calendar.set(Calendar.MINUTE, MainActivity.timeMinute);
        return calendar;
    }
    public static void setReminder()
    {
        Calendar calendar=setTime_and_hour();
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(cc.getApplicationContext(), "ALARM SET", Toast.LENGTH_LONG).show();
    }

    public static void cancelReminder()
       {
           if (manager != null) {
                    manager.cancel(pendingIntent);
                    isTimer=false;
                    number=null;
                }
       }


}



