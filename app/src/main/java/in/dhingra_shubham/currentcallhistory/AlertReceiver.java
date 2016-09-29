package in.dhingra_shubham.currentcallhistory;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by Shubham on 28-09-2016.
 */

public class AlertReceiver extends WakefulBroadcastReceiver {
    NotificationManager notificationManager;
    public String str2 = null;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (PhListener.number1 != null) {
            Vibrator vibrator;
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.default_song);
            mp.start();
            AlertDialog alertDialog = new AlertDialog.Builder(context.getApplicationContext())
                    .setTitle("Call Reminder")
                    .setMessage("Reminder to call " + PhListener.number1)
                    .setCancelable(false)
                    .setPositiveButton("call", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mp.pause();
                            PhListener.cancelReminder();
                            str2 = "tel:" + PhListener.number1;
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            callIntent.setData(Uri.parse(str2));
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            context.startActivity(callIntent);

                            Toast.makeText(context, "MAKE A CALL", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mp.pause();
                        }
                    })
                    .create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();

            Toast.makeText(context, "Call Reminder", Toast.LENGTH_SHORT).show();
        }
    }
}