package probeginners.whattodo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import welcome.WelcomeActivity;

public class NewList extends AppCompatActivity {
    Toolbar toolbar;
    EditText editText;
    boolean flag;
    MenuItem done;
    private String name;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(this);
        int a=sharedPreferences1.getInt("myback",0);
        if(WelcomeActivity.myback(a)!=0)
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
        else getWindow().setBackgroundDrawableResource(R.drawable.backcolor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        try{
            toolbar=(Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add New List");
            editText=(EditText)findViewById(R.id.newtaskname);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    overridePendingTransition(0,R.anim.push_up_out);
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(s.length()==0){
                        flag=false;
                        done.setEnabled(false);
                    }
                    else if(!flag){
                        flag=true;
                        done.setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });}
        catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.newlistmenu, menu);
            done = menu.findItem(R.id.done);
            done.setEnabled(false);
            flag = false;
            return true;
        }
        catch (Exception e){return false;}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        try {
            name = editText.getText().toString().trim();
            if (item.getItemId() == R.id.done && flag) {
                Intent intent = new Intent();
                intent.putExtra("name", name);
                setResult(11, intent);
                finish();
                overridePendingTransition(0, R.anim.push_up_out);
                return true;
            }
            if (item.getItemId() == android.R.id.home) {
                return true;
            }

            return false;
        }
        catch (Exception e){return false;}
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
