package in.dhingra_shubham.currentcallhistory;

/**
 * Created by Shubham on 28-09-2016.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class History extends ContentObserver {
    Context c;
    public static boolean updateHistory = false;
    public void setHistoryStatus(boolean updateHistory)
    {
        this.updateHistory=updateHistory;
    }
    public static boolean getHistory()
    {
        return updateHistory;
    }
    public History(Handler handler, Context cc) {
        // TODO Auto-generated constructor stub
        super(handler);
        c = cc;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        SharedPreferences sp = c.getSharedPreferences("ZnSoftech", Activity.MODE_PRIVATE);
        String number = sp.getString("number", null);
        if (number != null) {
            getCalldetailsNow();
            sp.edit().putString("number", null).commit();
        }
    }

    private void getCalldetailsNow() {
        // TODO Auto-generated method stub

        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int type1 = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date1 = managedCursor.getColumnIndex(CallLog.Calls.DATE);

        if (managedCursor.moveToFirst() == true) {
            String phNumber = managedCursor.getString(number);
            String callDuration = managedCursor.getString(duration1);

            String type = managedCursor.getString(type1);
            String date = managedCursor.getString(date1);

            String dir = null;
            int dircode = Integer.parseInt(type);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                default:
                    dir = "MISSED";
                    break;
            }

            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");

            String dateString = sdf_date.format(new Date(Long.parseLong(date)));
            String timeString = sdf_time.format(new Date(Long.parseLong(date)));

            DBHelper db = new DBHelper(c, "ZnSoftech.db", null, 2);
            db.insertdata(phNumber, dateString, timeString, callDuration, dir);
            setHistoryStatus(true);

            Cursor c = db.getData();
            if (c.getCount() > 0 && updateHistory==true) {
                c.moveToLast();
                String number2 = c.getString(0);
                String date2 = c.getString(1);
                String time = c.getString(2);
                String duration = c.getString(3);
                String type2 = c.getString(4);
                PhListener.getDetails(number2,date2,time,duration,type2);
            }
        }
            managedCursor.close();
        }


    }
