package com.enroute.enroute.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enroute.enroute.R;
import com.enroute.enroute.utility.GlobalVars;


public class InputActivity extends ActionBarActivity {

    private Button mOkButton;
    private EditText mStartLocationEditText;
    private EditText mMiddleLocationEditText;
    private EditText mDestinationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        initializeViews();
        getSupportActionBar().hide();
        EditText editText = (EditText) findViewById(R.id.destination);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    startMainActivity();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void initializeViews() {
        mOkButton = (Button) findViewById(R.id.submit_button);
        mStartLocationEditText = (EditText)findViewById(R.id.start_location);
        mMiddleLocationEditText = (EditText)findViewById(R.id.middle_location);
        mDestinationEditText = (EditText)findViewById(R.id.destination);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        String StartLocationText = mStartLocationEditText.getText().toString();
        String MiddleLocationText = mMiddleLocationEditText.getText().toString();
        String DestinationText = mDestinationEditText.getText().toString();

        mMiddleLocationEditText.setError(null);
        mDestinationEditText.setError(null);

        boolean hasError = false;
        if (MiddleLocationText.equals("")) {
            mMiddleLocationEditText.setError("Where do you want to stop by?");
            hasError = true;
        }
        if (DestinationText.equals("")) {
            mDestinationEditText.setError("Where is your final destination?");
            hasError = true;
        }
        // REMOVE FOR DEMO
        hasError = false;
        if (hasError) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra(GlobalVars.START_LOC, StartLocationText);
        i.putExtra(GlobalVars.MIDDLE_LOC, MiddleLocationText);
        i.putExtra(GlobalVars.DEST, DestinationText);
        startActivity(i);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input, menu);
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
