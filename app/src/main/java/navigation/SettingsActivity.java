package navigation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import db.DatabaseHandler;
import probeginners.whattodo.R;

public class SettingsActivity extends AppCompatActivity {
//public static int width,height,count=2;
    Toolbar toolbar;
    public static String chosenRingtone;
    public static boolean vibrate=false;
    boolean decide;
    TextView v,t;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        v=(TextView)findViewById(R.id.vibrate);
        t=(TextView)findViewById(R.id.tone);
    }
    public void changetone(View view){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);
    }
    public void setvibrate(final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = {"On","Off"};
        builder.setTitle("Vibrate")
                .setSingleChoiceItems(array, 1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                            decide=true;
                        else decide=false;

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                          if(decide==true){
                              vibrate=true;
                              v.setText("On");
                              decide=false;
                          }
                        else{
                              vibrate=false;
                              v.setText("Off");
                          }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        Dialog dialog= builder.create();
        dialog.show();
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                chosenRingtone = uri.toString();
                Ringtone ringtone = RingtoneManager.getRingtone(SettingsActivity.this, uri);
                title = ringtone.getTitle(this);
                t.setText(title);
                Log.e("tone",title);

            }
            else
            {
                chosenRingtone = null;
                t.setText("None");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences=getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("ringtone",chosenRingtone);
        editor.putBoolean("vibrate",vibrate);
        editor.putString("title",title);
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences("settings", Context.MODE_PRIVATE);
        chosenRingtone=sharedPreferences.getString("ringtone","");
        vibrate=sharedPreferences.getBoolean("vibrate",false);
        title=sharedPreferences.getString("title","");
        if(title.equals(""))
            t.setText("None");
        else
        t.setText(title);
        if(vibrate)v.setText("On");
        else v.setText("Off");
        Log.e("tone","hi");
    }

    public void deleteall(View view){
        DatabaseHandler handler=new DatabaseHandler(this);
        handler.deleteall();
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
    }
}
