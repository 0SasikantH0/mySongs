package mysongs.com.allgo.mysongs;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.channels.Selector;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_main);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        params.width = 400;
        params.height = 400;
        params.setMargins(0, 0, 20, 0);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayer1);        //Adding 2 TextViews
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            newButton.setBackgroundResource(R.drawable.test_image);
            newButton.setLayoutParams(params);
            linearLayout.addView(newButton);
        }

        LinearLayout topChartsLayout =(LinearLayout) findViewById(R.id.topchartlinearlayer);
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            newButton.setBackgroundResource(R.drawable.test_image2);
            newButton.setLayoutParams(params);
            topChartsLayout.addView(newButton);
        }


        LinearLayout.LayoutParams paramsNewSongs = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsNewSongs.width = 200;
        paramsNewSongs.height = 200;
        paramsNewSongs.setMargins(0, 0, 20, 0);
        LinearLayout songsNew = (LinearLayout) findViewById(R.id.newsongsmainlinearlayer);
        for (int i = 0; i < 3; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            for(int j =0; j< 5; j++) {
                final LinearLayout linearLayout1 = new LinearLayout(this);

                Button newButton = new Button(this);
                newButton.setBackgroundResource(R.drawable.test_image3);
                newButton.setLayoutParams(paramsNewSongs);
                /** do this when an item is selected*/
                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        linearLayout1.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                });
                LinearLayout linearLayout2 = new LinearLayout(this);
                TextView titleText = new TextView(this);
                titleText.setText("Song1");
                TextView extraText = new TextView(this);
                extraText.setText("extra things");
                linearLayout2.setPadding(0,20,0,0);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout2.addView(titleText);
                linearLayout2.addView(extraText);

                LinearLayout.LayoutParams paramsLayoutSongs = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsLayoutSongs.width = 800;
                paramsLayoutSongs.setMargins(0,40,70,0);
                linearLayout1.setLayoutParams(paramsLayoutSongs);
                linearLayout1.addView(newButton);
                linearLayout1.addView(linearLayout2);
                linearLayout1.setPadding(30,15,0,15);
                linearLayout1.setBackgroundColor(Color.parseColor("#595959"));
                linearLayout1.setClickable(true);
                /** do this when an item is selected*/
                linearLayout1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        linearLayout1.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                });
                linearLayout.addView(linearLayout1);

            }
            songsNew.setPadding(0,0,0,140);
            songsNew.addView(linearLayout);
        }
        LinearLayout playlistLayout =(LinearLayout) findViewById(R.id.playlistslinearlayer);
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            newButton.setBackgroundResource(R.drawable.test_image);
            newButton.setLayoutParams(params);
            playlistLayout.addView(newButton);
        }

        LinearLayout moodsLayout =(LinearLayout) findViewById(R.id.moodslinearlayer);
        int colors[] =
                {Color.parseColor("#ff0075c9"),Color.parseColor("#ff00518a"),Color.parseColor("#ff002e4f")};
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            GradientDrawable gd = new GradientDrawable();
            LinearLayout.LayoutParams paramsmoodSongs = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsmoodSongs.width = 400;
            paramsmoodSongs.height = 400;
            paramsmoodSongs.setMargins(0, 0, 50, 0);

            gd.setColors(colors);
            gd.setCornerRadius(2000);
            newButton.setBackground(gd);
            newButton.setLayoutParams(paramsmoodSongs);
            moodsLayout.addView(newButton);
        }

        LinearLayout popularRelative = (LinearLayout) findViewById(R.id.popularcontent);
        for(int i = 0; i < 3; i++)
        {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            for(int j = 0; j < 3; j++) {
                final LinearLayout linearLayout1 = new LinearLayout(this);

                Button newButton = new Button(this);
                newButton.setBackgroundResource(R.drawable.test_image3);
                newButton.setLayoutParams(paramsNewSongs);

                LinearLayout linearLayout2 = new LinearLayout(this);
                TextView titleText = new TextView(this);
                titleText.setText("Song1");
                TextView extraText = new TextView(this);
                extraText.setText("extra things");

                linearLayout2.setPadding(0,20,0,0);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout2.addView(titleText);
                linearLayout2.addView(extraText);

                LinearLayout.LayoutParams paramsLayoutSongs = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsLayoutSongs.width = 800;
                paramsLayoutSongs.setMargins(0,40,70,0);
                linearLayout1.setLayoutParams(paramsLayoutSongs);
                linearLayout1.addView(newButton);
                linearLayout1.addView(linearLayout2);
                linearLayout.addView(linearLayout1);
            }
            popularRelative.addView(linearLayout);

        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        viewPager.setNestedScrollingEnabled(false);

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

    @Override
    protected void onPause() {
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
        super.onPause();
    }
}
