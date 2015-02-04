package ru.seasonvar.seasonvarmobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.SeasonvarHttpClient;

/**
 * Created by Andrey_Demidenko on 2/3/2015 5:06 PM.
 */
public class MainActivity extends Activity {


    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main_form);
        preferences = getApplicationContext().getSharedPreferences("SeasonvarSettings", Context.MODE_PRIVATE);

        Button btnCancel = (Button) findViewById(R.id.cancelBtn);
        Button btnLogin = (Button) findViewById(R.id.loginBtn);
        final EditText login = (EditText) findViewById(R.id.login);
        final EditText password = (EditText) findViewById(R.id.password);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBarIndeterminateVisibility(true);
                new AsyncTask(){
                    boolean result = false;
                    @Override
                    protected Object doInBackground(Object[] params) {
                        result = SeasonvarHttpClient.getInstance().login(login.getText().toString(), password.getText().toString());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        setProgressBarIndeterminateVisibility(false);
                        if (result) {
                            preferences.edit()
                                    .putString("login", login.getText().toString())
                                    .putString("password", password.getText().toString())
                                    .apply();
                            Intent list = new Intent(MainActivity.this, MovieListActivity.class);
                            startActivity(list);
                        }
                    }
                }.execute();
            }
        });
        login.setText(preferences.getString("login", null));
        password.setText(preferences.getString("password", null));
    }
}
