package visitor.app.visitorapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import visitor.app.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences s;
    private boolean active = false;
    private boolean wasInactive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        s = getSharedPreferences(Constants.SMS_ACTIVE, 0);
        String str = s.getString("ACTIVE", "").trim();
        if(!str.isEmpty())
        {
            active = true;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                int check1 = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECEIVE_SMS);
                int check2 = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_SMS);

                if((check1 != PackageManager.PERMISSION_GRANTED) || (check2 != PackageManager.PERMISSION_GRANTED))
                {
                    Toast.makeText(getApplicationContext(), "Go to permissions and \nEnable the SMS permission", Toast.LENGTH_LONG).show();
                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + SplashActivity.this.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    SplashActivity.this.startActivity(i);
                }
                else if(active == true)
                {
                    startActivity(new Intent(SplashActivity.this, ViewActivity.class));
                    finish();
                }
                else
                {
                    //Create AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Application not active. Please contact Admin");
                    builder.setCancelable(false);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    SplashActivity.this.finish();
                                }
                            }
                    );

                    builder.show();
                }

            }
        }, 1500);
    }

    @Override
    protected void onPause() {
        wasInactive = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(wasInactive)
        {
            if(active == true)
            {
                startActivity(new Intent(SplashActivity.this, ViewActivity.class));
                finish();
            }
            else
            {
                //Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Application not active. Please contact Admin");
                builder.setCancelable(false);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SplashActivity.this.finish();
                            }
                        }
                );

                builder.show();
            }
        }

    }
}
