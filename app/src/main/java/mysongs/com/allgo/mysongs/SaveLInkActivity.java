package mysongs.com.allgo.mysongs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class SaveLInkActivity extends AppCompatActivity {
    EditText editText;
    LinearLayout linearLayout;
    Button addLinkButton = null;
    int addButtonLanguageClicks = 0;
    boolean isaddLanguageBtnClicked = false;
    String whichLanguageButtonCLicked = new String();

    boolean isaddMoodBtnClicked = false;
    String whichMoodButtonCLicked = new String();

    AlertDialog.Builder builder;
    boolean isaddPlaylistBtnClicked = false;
    String whichPlaylistButtonCLicked = new String();

    String[] languages = {"Telugu","Hindi","English","Kannada","Punjabi"};
    String[] moods = {"Sad","Happy","Stressed","Angry","Peaceful"};
    String[] playlist = {"Playlist1","Playlist2","Playlist3","Playlist4","Playlist5"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_save_link);
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

        editText = findViewById(R.id.editText2);
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            String link = extras.getString(Intent.EXTRA_TEXT);
            TextView newButton = new TextView(this);
            newButton.setText(link);
            newButton.setTextColor(Color.parseColor("#1fc5f2"));
            editText.setText(link);
        }
        linearLayout = (LinearLayout) findViewById(R.id.languagelinearlayer);        //Adding 2 TextViews
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.height = 120;
        params.setMargins(0, 0, 20, 0);
        for (int i = 0; i < 5; i++) {
            final Button newButton = new Button(this);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#ffffff"));
            gd.setCornerRadius(2000);
            newButton.setBackground(gd);
            newButton.setLayoutParams(params);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setText(languages[i]);
            newButton.setPadding(60,0,60,0);
            newButton.setTextSize(12);
            final int finalI = i;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isaddLanguageBtnClicked == false) {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor("#ffeba3"));
                        gd.setCornerRadius(2000);
                        newButton.setBackground(gd);
                        isaddLanguageBtnClicked = true;
                        whichLanguageButtonCLicked = languages[finalI];
                    }
                    else if(isaddLanguageBtnClicked == true && whichLanguageButtonCLicked == languages[finalI])
                    {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor("#ffffff"));
                        gd.setCornerRadius(2000);
                        newButton.setBackground(gd);
                        isaddLanguageBtnClicked = false;
                    }
                }
            });
            linearLayout.addView(newButton);
        }

        LinearLayout moodLinearLayout = (LinearLayout) findViewById(R.id.moodlinearlayer);
        for (int i = 0; i < 5; i++) {
            final Button newButton = new Button(this);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#ffffff"));
            gd.setCornerRadius(2000);
            newButton.setBackground(gd);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setLayoutParams(params);
            newButton.setText(moods[i]);
            newButton.setPadding(60,0,60,0);
            newButton.setTextSize(12);
            final int finalI = i;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isaddMoodBtnClicked == false) {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor("#8dfcfa"));
                        gd.setCornerRadius(2000);
                        newButton.setBackground(gd);
                        isaddMoodBtnClicked = true;
                        whichMoodButtonCLicked = moods[finalI];
                    }
                    else if(isaddMoodBtnClicked == true && whichMoodButtonCLicked == moods[finalI])
                    {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor("#ffffff"));
                        gd.setCornerRadius(2000);
                        newButton.setBackground(gd);
                        isaddMoodBtnClicked = false;
                    }
                }
            });
            moodLinearLayout.addView(newButton);
        }

        LinearLayout playlistLinearLayout = (LinearLayout) findViewById(R.id.playlistlinearlayer);
        for (int i = 0; i < 5; i++) {
            final Button newButton = new Button(this);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#ffffff"));
            gd.setCornerRadius(2000);
            newButton.setBackground(gd);
            newButton.setTextColor(Color.parseColor("#000000"));
            newButton.setLayoutParams(params);
            newButton.setText(playlist[i]);
            newButton.setPadding(60,0,60,0);
            newButton.setTextSize(12);
            final int finalI = i;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isaddPlaylistBtnClicked == false) {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor("#d45719"));
                        gd.setCornerRadius(2000);
                        newButton.setBackground(gd);
                        isaddPlaylistBtnClicked = true;
                        whichPlaylistButtonCLicked = playlist[finalI];
                    }
                    else if(isaddPlaylistBtnClicked == true && whichPlaylistButtonCLicked == playlist[finalI])
                    {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor("#ffffff"));
                        gd.setCornerRadius(2000);
                        newButton.setBackground(gd);
                        isaddPlaylistBtnClicked = false;
                    }
                }
            });
            playlistLinearLayout.addView(newButton);
        }

        addLinkButton = (Button) findViewById(R.id.addlinkbutton);
        addLinkButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Button Pressed
                    addLinkButton.setBackgroundResource(R.drawable.addliftbutton);
                    addLinkButton.setTextColor(Color.parseColor("#ffffff"));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    addLinkButton.setBackgroundResource(R.drawable.addroundbutton);
                    addLinkButton.setTextColor(Color.parseColor("#ff00518a"));
                }
                return false;
            }
        });
        addLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                String videoId = new String();
                EditText linkEditText = findViewById(R.id.editText2);
                String link = linkEditText.getText().toString();
                videoId = extractYTId(link);
                String url = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                Bitmap myImage = getBitmapFromURL(url);
                Button testButton = findViewById(R.id.addlinkbutton);

                Drawable dr = new BitmapDrawable(myImage);
                testButton.setBackgroundDrawable(dr);*/


                //finish();*/

            }
        });
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }


    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
