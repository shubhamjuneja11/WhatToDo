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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import db.DatabaseHandler;
import probeginners.whattodo.R;

public class SettingsActivity extends AppCompatActivity {
    public static String chosenRingtone;
    public static boolean vibrate = false,color=true;
    Toolbar toolbar;
    boolean decide,decide2;
    int back;
    TextView v, t,co,bg;
    String title,mycolor;
    SharedPreferences sharedPreferences,sharedPreferences1;
    SharedPreferences.Editor editor,editor1;
    CharSequence array[] = {"Image 1","Image 2"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor1=sharedPreferences1.edit();
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Settings");
            v = (TextView) findViewById(R.id.vibrate);
            t = (TextView) findViewById(R.id.tone);
            co=(TextView)findViewById(R.id.color);
            bg=(TextView)findViewById(R.id.backgroundss);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    overridePendingTransition(0, R.anim.slide_out_left);

                }
            });

        } catch (Exception e) {
        }
    }

    public void changetone(View view) {
        try {

            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            this.startActivityForResult(intent, 5);
        } catch (Exception e) {
        }
    }

    public void setvibrate(final View view) {
        try {
int a=vibrate?0:1;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            CharSequence[] array = {"On", "Off"};
            builder.setTitle("Vibrate")
                    .setSingleChoiceItems(array,a, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0)
                                decide = true;
                            else decide = false;

                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            if (decide) {
                                vibrate = true;
                                v.setText("On");
                                decide = false;
                            } else {
                                vibrate = false;
                                v.setText("Off");
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            Dialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        try {

            if (resultCode == Activity.RESULT_OK && requestCode == 5) {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    chosenRingtone = uri.toString();
                    Ringtone ringtone = RingtoneManager.getRingtone(SettingsActivity.this, uri);
                    title = ringtone.getTitle(this);
                    t.setText(title);

                } else {
                    chosenRingtone = null;
                    t.setText("None");
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {

            editor.putString("ringtone", chosenRingtone);
            editor.putBoolean("vibrate", vibrate);
            editor.putString("title", title);
            editor.putBoolean("color",color);
            editor1.putInt("myback",back);
            editor.commit();
            editor1.commit();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            //SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
            chosenRingtone = sharedPreferences.getString("ringtone", "");
            vibrate = sharedPreferences.getBoolean("vibrate", false);
            title = sharedPreferences.getString("title", "");
            color=sharedPreferences.getBoolean("color",true);
            back=sharedPreferences1.getInt("myback",0);
            if(color)
                co.setText("Colorful");
            else co.setText("White");
            if (title.equals(""))
                t.setText("None");
            else
                t.setText(title);
            if (vibrate) v.setText("On");
            else v.setText("Off");
           switch (back){
               case 0:bg.setText("Image 1");break;
               case 1:bg.setText("Image 2");break;
           }
        } catch (Exception ep) {
            ep.printStackTrace();
        }
    }

    public void deleteall(View view) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear all data?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    DatabaseHandler handler = new DatabaseHandler(SettingsActivity.this);
                    handler.deleteall();
                    Toast.makeText(SettingsActivity.this, "Cleared", Toast.LENGTH_SHORT).show();

                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).show();


        } catch (Exception e) {
        }
    }
public void changecolor(View view){
    decide2=true;
int a=color?1:0;
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    CharSequence[] array = {"White","Colorful"};
    builder.setTitle("Set Color")
            .setSingleChoiceItems(array,a, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                        decide2 = false;
                    else decide2 = true;

                }
            })
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (decide2) {
                        color = true;
                        co.setText("Colorful");
                        decide2 = false;
                    } else {
                        color=false;
                        co.setText("White");
                    }
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });

    Dialog dialog = builder.create();
    dialog.show();
}
    public void changeback(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Set Background Image")

                .setSingleChoiceItems(array,back, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      back=which;

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       bg.setText(array[back]);
                        Toast.makeText(SettingsActivity.this, "Background changed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
