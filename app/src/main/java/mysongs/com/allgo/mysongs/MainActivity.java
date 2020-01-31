package mysongs.com.allgo.mysongs;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import static java.lang.Thread.sleep;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener {
    LinearLayout linearLayout;
    String recentVideoUrl = null;
    String recentVideoname = null;
    String recentVideolanguage = null;
    String recentVideomood = null;
    String recentVideoplaylist = null;
    int recentVideoplayvisits = 0;
    long recentVideoId = 0;
    String clickedYoutubeID;
    RelativeLayout youtubeLayout;
    int currentPlayingId = 0;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer youtubePlayerHandle;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

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


        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(ConfigYoutube.YOUTUBE_API_KEY, MainActivity.this);
        /** set youtube visibilty to none*/
        youtubeLayout = findViewById(R.id.youtubevideolayout);
        youtubeLayout.setVisibility(View.GONE);
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

        LinearLayout topChartsLayout = (LinearLayout) findViewById(R.id.topchartlinearlayer);
        /** get the list form db*/
        final DBHelper dbHelper = new DBHelper(MainActivity.this);
        Cursor topTracksDB = dbHelper.getAllRecordsDESC();
        String name = null;
        String language = null;
        String mood = null;
        String playlist = null;
        String link = null;
        int nvisits = 0;
        long id = 0;

        while (topTracksDB.moveToNext()) {
            int index;

            index = topTracksDB.getColumnIndexOrThrow("name");
            name = topTracksDB.getString(index);

            index = topTracksDB.getColumnIndexOrThrow("link");
            link = topTracksDB.getString(index);

            index = topTracksDB.getColumnIndexOrThrow("language");
            language = topTracksDB.getString(index);

            index = topTracksDB.getColumnIndexOrThrow("mood");
            mood = topTracksDB.getString(index);

            index = topTracksDB.getColumnIndexOrThrow("playlist");
            playlist = topTracksDB.getString(index);

            index = topTracksDB.getColumnIndexOrThrow("nvisits");
            nvisits = topTracksDB.getInt(index);

            index = topTracksDB.getColumnIndexOrThrow("id");
            id = topTracksDB.getLong(index);


            String videoid = extractYTId(link);
            String url = "https://img.youtube.com/vi/" + videoid + "/0.jpg";

            Bitmap myImage = getBitmapFromURL(url);

            //Drawable dr = new BitmapDrawable(myImage);
            RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), myImage);

            RBD.setCornerRadius(10.0f);
            RBD.setAntiAlias(true);
            GradientDrawable buttonGD = new GradientDrawable();
            buttonGD.setColor(Color.parseColor("#303030"));
            buttonGD.setCornerRadius(10.0f);
            ImageButton newButton = new ImageButton(this);
            newButton.setImageDrawable(RBD);
            newButton.setBackground(buttonGD);
            newButton.setLayoutParams(params);
            topChartsLayout.addView(newButton);
        }


        updateNewSongs();

        LinearLayout playlistLayout = (LinearLayout) findViewById(R.id.playlistslinearlayer);
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            newButton.setBackgroundResource(R.drawable.test_image);
            newButton.setLayoutParams(params);
            playlistLayout.addView(newButton);
        }

        LinearLayout moodsLayout = (LinearLayout) findViewById(R.id.moodslinearlayer);
        int colors[] =
                {Color.parseColor("#ff0075c9"), Color.parseColor("#ff00518a"), Color.parseColor("#ff002e4f")};
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
        /** adding popular by types language, mood, etc*/
        /*LinearLayout popularRelative = (LinearLayout) findViewById(R.id.popularcontent);
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
*/
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        viewPager.setNestedScrollingEnabled(false);

    }


    private void updateNewSongs()
    {

        LinearLayout.LayoutParams paramsNewSongs = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsNewSongs.width = 200;
        paramsNewSongs.height = 200;
        paramsNewSongs.setMargins(0, 0, 20, 0);
        LinearLayout songsNew = (LinearLayout) findViewById(R.id.newsongsmainlinearlayer);

        /** removing all songs from new songs category*/
        songsNew.removeAllViews();

        /** get the list form db*/
        final DBHelper dbHelper = new DBHelper(MainActivity.this);
        int numberOfRows = 0;
        numberOfRows = dbHelper.numberOfRows();


        if (numberOfRows > 0) {
            int numberofloops = 0;

            if ((numberOfRows % 5) > 0) {
                numberofloops = (numberOfRows / 5) + 1;
            } else {
                numberofloops = (numberOfRows / 5);
            }
            /** make default rows as three*/
            if (numberofloops > 3) {
                numberofloops = 3;
            }
            int countloops = 1;
            int countRowsLoop = 0;

            for (int i = 0; i < numberofloops; i++) {
                LinearLayout linearLayout = new LinearLayout(this);
                if ((numberOfRows / 5.0) > 1.0) {
                    countRowsLoop = 5;
                } else {
                    countRowsLoop = numberOfRows;
                }
                numberOfRows = numberOfRows - 5;
                for (int j = 0; j < countRowsLoop; j++) {
                    final LinearLayout linearLayout1 = new LinearLayout(this);

                    ImageButton newButton = new ImageButton(this);
                    Cursor record = dbHelper.getData(countloops);
                    countloops++;
                    final String name = null;
                    String language = null;
                    String mood = null;
                    String playlist = null;
                    int nvisits = 0;
                    long id = 0;

                    while (record.moveToNext()) {
                        int index;

                        index = record.getColumnIndexOrThrow("name");
                        recentVideoname = record.getString(index);

                        index = record.getColumnIndexOrThrow("link");
                        recentVideoUrl = record.getString(index);

                        index = record.getColumnIndexOrThrow("language");
                        recentVideolanguage = record.getString(index);

                        index = record.getColumnIndexOrThrow("mood");
                        recentVideomood = record.getString(index);

                        index = record.getColumnIndexOrThrow("playlist");
                        recentVideoplaylist = record.getString(index);

                        index = record.getColumnIndexOrThrow("nvisits");
                        recentVideoplayvisits = record.getInt(index);

                        index = record.getColumnIndexOrThrow("id");
                        recentVideoId = record.getLong(index);

                    }
                    /** extract image from url*/

                    String videoid = extractYTId(recentVideoUrl);

                    String url = "https://img.youtube.com/vi/" + videoid + "/mqdefault.jpg";
                    final Bitmap myImage = getBitmapFromURL(url);

                    //Drawable dr = new BitmapDrawable(myImage);
                    RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), myImage);

                    RBD.setCornerRadius(100.0f);
                    RBD.setAntiAlias(true);
                    newButton.setImageDrawable(RBD);
                    GradientDrawable buttonGD = new GradientDrawable();

                    buttonGD.setCornerRadius(1000);
                    buttonGD.setColor(Color.parseColor("#ebebeb"));
                    newButton.setBackground(buttonGD);
                    //newButton.setBackgroundResource(R.drawable.test_image3);
                    newButton.setLayoutParams(paramsNewSongs);
                    final String videoUrl = recentVideoUrl;
                    final String videoName = recentVideoname;
                    final String videoLanguage = recentVideolanguage;
                    final String videoMood = recentVideomood;
                    final String videoPlaylist = recentVideoplaylist;
                    final int videoVisits = recentVideoplayvisits;
                    final int finalvideoId = (int) recentVideoId;


                    TextView titleText = new TextView(this);

                    String testVideoName = recentVideoname.substring(0, Math.min(recentVideoname.length(), 17));
                    if (recentVideoname.length() > 17) {
                        testVideoName = testVideoName + "...";
                    }
                    titleText.setText(testVideoName);
                    titleText.setTextSize(16);
                    titleText.setTextColor(Color.parseColor("#363636"));
                    final TextView extraText = new TextView(this);
                    extraText.setTextColor(Color.parseColor("#525252"));
                    if (videoUrl.contains("youtu.be")) {
                        extraText.setText("Youtube");
                    } else {
                        extraText.setText("Unknown");
                    }


                    /** do this when an item is selected*/
                    final int finalCountloops = countloops;
                    newButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //linearLayout1.setBackgroundColor(Color.parseColor("#ffffff"));
                            String videoId = extractYTId(videoUrl);
                            int visitstoVideo = 0;

                            DBHelper visitsDb = new DBHelper(MainActivity.this);
                            Cursor visitDBrecord = visitsDb.getData((int) finalvideoId);
                            while (visitDBrecord.moveToNext()) {
                                int index;
                                index = visitDBrecord.getColumnIndexOrThrow("nvisits");
                                visitstoVideo = visitDBrecord.getInt(index);

                            }
                            visitstoVideo++;
                            //extraText.setText(" " + visitstoVideo);
                            visitsDb.updateContact(finalvideoId, videoName, videoUrl, videoLanguage, videoMood, videoPlaylist, visitstoVideo);

/*
                             Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                             Intent webIntent = new Intent(Intent.ACTION_VIEW,
                             Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                             try {
                             startActivity(appIntent);
                             } catch (ActivityNotFoundException ex) {
                             startActivity(webIntent);
                             }*/

                            clickedYoutubeID = videoId;
                            playYTLinkBottom(videoId,videoName,finalvideoId);

                        }
                    });
                    LinearLayout linearLayout2 = new LinearLayout(this);

                    linearLayout2.setPadding(0, 20, 0, 0);
                    linearLayout2.setOrientation(LinearLayout.VERTICAL);
                    linearLayout2.addView(titleText);
                    linearLayout2.addView(extraText);

                    LinearLayout.LayoutParams paramsLayoutSongs = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    paramsLayoutSongs.width = 800;
                    paramsLayoutSongs.setMargins(0, 40, 70, 0);
                    linearLayout1.setLayoutParams(paramsLayoutSongs);
                    linearLayout1.addView(newButton);
                    linearLayout1.addView(linearLayout2);
                    linearLayout1.setPadding(15, 15, 0, 15);
                    GradientDrawable songlayoutGD = new GradientDrawable();
                    songlayoutGD.setCornerRadius(1000);
                    songlayoutGD.setColor(Color.parseColor("#ebebeb"));
                    linearLayout1.setBackground(songlayoutGD);
                    //linearLayout1.setBackgroundColor(Color.parseColor("#ebebeb"));
                    linearLayout1.setClickable(true);
                    /** do this when an item is selected*/

                    linearLayout1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String videoId = extractYTId(videoUrl);
                            int visitstoVideo = 0;

                            DBHelper visitsDb = new DBHelper(MainActivity.this);
                            Cursor visitDBrecord = visitsDb.getData((int) finalvideoId);

                            while (visitDBrecord.moveToNext()) {
                                int index;
                                index = visitDBrecord.getColumnIndexOrThrow("nvisits");
                                visitstoVideo = visitDBrecord.getInt(index);
                            }
                            visitstoVideo++;
                            //extraText.setText(" " + visitstoVideo);
                            visitsDb.updateContact(finalvideoId, videoName, videoUrl, videoLanguage, videoMood, videoPlaylist, visitstoVideo);

/*                          Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                            try {
                                startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                startActivity(webIntent);
                            }*/


                            clickedYoutubeID = videoId;
                            playYTLinkBottom(videoId, videoName, finalvideoId);

                        }
                    });
                    linearLayout.addView(linearLayout1);

                }
                songsNew.setPadding(0, 0, 0, 140);
                songsNew.addView(linearLayout);
            }
        }
        else
        {
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams emptySongsList = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            emptySongsList.width = 700;
            emptySongsList.height = 250;
            GradientDrawable emptyListGD = new GradientDrawable();
            emptyListGD.setCornerRadius(1000);
            emptyListGD.setColor(Color.parseColor("#ffffff"));
            linearLayout.setBackground(emptyListGD);
            linearLayout.setLayoutParams(emptySongsList);

            /** open add link activity*/
            linearLayout.setClickable(true);
            /** do this when an item is selected*/
            linearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Intent is used to switch from one activity to another.
                    startActivity(new Intent(MainActivity.this, SaveLInkActivity.class));
                }
            });
            songsNew.setPadding(0, 0, 0, 140);
            songsNew.addView(linearLayout);
        }

    }

    private void playYTLinkBottom(String videoId, String name, int videoid)
    {
        currentPlayingId = videoid;
        LinearLayout.LayoutParams controls = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        /*Bitmap resultBmp = BlurBuilder.blur(MainActivity.this, myImage);
        BitmapDrawable background = new BitmapDrawable(resultBmp);*/
        LinearLayout ytContainer = findViewById(R.id.youtubecontainer);

        //ytContainer.setBackgroundDrawable(background);
        ytContainer.setBackgroundColor(Color.parseColor("#000000"));
        LinearLayout ytOptions = findViewById(R.id.layoutoptions);
        ytOptions.removeAllViews();

        LinearLayout ytOptions1 = new LinearLayout(MainActivity.this);
        LinearLayout ytOptions2 = new LinearLayout(MainActivity.this);
        ytOptions.setOrientation(LinearLayout.VERTICAL);

        Button openinYTpause = new Button(MainActivity.this);
        Button openinYTnext = new Button(MainActivity.this);
        Button openinYTprevious = new Button(MainActivity.this);

        TextView titleText = new TextView(MainActivity.this);
        titleText.setText(name);
        titleText.setTextColor(Color.parseColor("#ffffff"));
        ytOptions1.addView(titleText);

        openinYTnext.setLayoutParams(controls);
        openinYTpause.setLayoutParams(controls);
        openinYTprevious.setLayoutParams(controls);

        openinYTnext.setText("N");
        openinYTnext.setBackgroundColor(Color.TRANSPARENT);
        openinYTnext.setTextColor(Color.parseColor("#000000"));
        openinYTpause.setText("||");
        openinYTpause.setBackgroundColor(Color.TRANSPARENT);
        openinYTpause.setTextColor(Color.parseColor("#000000"));
        openinYTprevious.setText("P");
        openinYTprevious.setBackgroundColor(Color.TRANSPARENT);
        openinYTprevious.setTextColor(Color.parseColor("#000000"));



        GradientDrawable buttonGD = new GradientDrawable();
        buttonGD.setCornerRadius(1000);
        buttonGD.setColor(Color.parseColor("#ebebeb"));
        ytOptions2.setBackground(buttonGD);
        ytOptions2.addView(openinYTnext);
        ytOptions2.addView(openinYTpause);
        ytOptions2.addView(openinYTprevious);
        ytOptions.addView(ytOptions1);
        ytOptions.addView(ytOptions2);

        youtubeLayout.setVisibility(View.VISIBLE);
        youtubePlayerHandle.loadVideo(videoId);
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
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

            int width = myBitmap.getWidth();
            int height = myBitmap.getHeight();
            int newWidth = (height > width) ? width : height;
            int newHeight = (height > width) ? height - (height - width) : height;
            int cropW = (width - height) / 2;
            cropW = (cropW < 0) ? 0 : cropW;
            int cropH = (height - width) / 2;
            cropH = (cropH < 0) ? 0 : cropH;
            Bitmap cropImg = Bitmap.createBitmap(myBitmap, cropW, cropH, newWidth, newHeight);

            return cropImg;
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


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        if(intent != null && intent.getAction().equals(intent.ACTION_SEND))
        {
            updateNewSongs();
        }
        //processExtraData();
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

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            this.youtubePlayerHandle = youTubePlayer;
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
        this.youtubePlayerHandle.play();
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

        int videoDBid = currentPlayingId;

        String vname = null;
        String vurl = null;
        String videoYTId = null;

        DBHelper nextSongDB = new DBHelper(MainActivity.this);
        videoDBid++;
        Cursor record;
        record = nextSongDB.getData(videoDBid);

        while (record.moveToNext()) {
            int index;

            index = record.getColumnIndexOrThrow("name");
            vname = record.getString(index);

            index = record.getColumnIndexOrThrow("link");
            vurl = record.getString(index);
        }

        videoYTId = extractYTId(vurl);

        String url = "https://img.youtube.com/vi/" + videoYTId + "/mqdefault.jpg";
        final Bitmap myImage = getBitmapFromURL(url);
        playYTLinkBottom(videoYTId,vname,videoDBid);
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
