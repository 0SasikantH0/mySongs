package mysongs.com.allgo.mysongs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

/**
 * Created by sasikanth on 12/27/19.
 */

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar  = null;
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    private static int activity = 1;
    //After completion of 2000 ms, the next activity will get started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/**
 View decorView = getWindow().getDecorView();
 decorView.setSystemUiVisibility(
 View.SYSTEM_UI_FLAG_IMMERSIVE
 // Set the content to appear under the system bars so that the
 // content doesn't resize when the system bars hide and show.
 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
 | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
 | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 // Hide the nav bar and status bar, optional for full screen
 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
 | View.SYSTEM_UI_FLAG_FULLSCREEN);
 */
        if(activity == 1)
        {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //This method is used so that your splash activity
            //can cover the entire screen.

            setContentView(R.layout.splashscreen);
            //this will bind your MainActivity.class file with activity_main.

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this,
                            MainActivity.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.

                    finish();
                    //the current activity will get finished.
                }
            }, SPLASH_SCREEN_TIME_OUT);
        }
        else
        {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this,
                            MainActivity.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.
                    finish();
                    //the current activity will get finished.
                }
            });

        }
        activity++;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(activity==1)
            finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(activity==1)
            finish();
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        if(activity==1)
            finish();
    }

    @Override
    protected void onResume() {

        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
