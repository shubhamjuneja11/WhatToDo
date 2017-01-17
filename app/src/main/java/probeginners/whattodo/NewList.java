package probeginners.whattodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class NewList extends AppCompatActivity {
Toolbar toolbar;
    private String name;

    EditText editText;
    boolean flag;
    MenuItem done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editText=(EditText)findViewById(R.id.newtaskname);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()==0){
                    flag=false;
                    done.setEnabled(false);
                    //done.getIcon().setAlpha(50);
                }
                else if(!flag){
                    flag=true;
                    done.setEnabled(true);
                    //done.getIcon().setAlpha(200);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       //done button should not work on starting
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.newlistmenu,menu);
        done=menu.findItem(R.id.done);
        done.setEnabled(false);

        flag=false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        name=editText.getText().toString();
        if(item.getItemId()==R.id.done && flag)
        {
            Intent intent=new Intent();
            intent.putExtra("name",name);
            setResult(11,intent);
            finish();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
           // setResult(10);
            //finish(); // close this activity and return to preview activity (if there is any)
            return true;
        }
        return false;
    }
}
