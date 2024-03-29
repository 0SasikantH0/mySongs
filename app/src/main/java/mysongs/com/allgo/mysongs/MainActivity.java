package mysongs.com.allgo.mysongs;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import static java.lang.Thread.sleep;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
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
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer youtubePlayerHandle;

    String imgurl;

    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;

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
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        params.width = 400;
        params.height = 250;
        params.setMargins(0, 0, 0, 0);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayer1);        //Adding 2 TextViews
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            newButton.setBackgroundResource(R.drawable.test_image);
            newButton.setLayoutParams(params);
            linearLayout.addView(newButton);
        }

        LinearLayout topChartsLayout = (LinearLayout) findViewById(R.id.topchartlinearlayer);
        LinearLayout topChartsLayout2 = (LinearLayout) findViewById(R.id.topchartlinearlayer2);
        LinearLayout topOfTopCharts = (LinearLayout) findViewById(R.id.topoftopcharts);

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
        int topTracksCount = 0;

        while (topTracksDB.moveToNext()) {
            int index;
            topTracksCount ++;
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
            final String url = "https://img.youtube.com/vi/" + videoid + "/0.jpg";

            final ImageButton newButton = new ImageButton(this);

            TextCrawler textCrawler = new TextCrawler();

            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    // Any work that needs to be done before generating the preview. Usually inflate
                    // your custom preview layout here.
                }

                @Override
                public void onPos(SourceContent sourceContent, boolean b) {
                    // Populate your preview layout with the results of sourceContent.
                    List<String> imgList = sourceContent.getImages();

                    if(imgList.size() >= 1)
                    {
                        Bitmap myImage;
                        //myImage = getBitmapFromURL(imgList.get(1));
                        myImage = getBitmapFromURL(imgList.get(imgList.size()-1));
                        //Drawable dr = new BitmapDrawable(myImage);
                        RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), myImage);
                        RBD.setCornerRadius(10.0f);
                        RBD.setAntiAlias(true);

                        newButton.setScaleType(ImageView.ScaleType.FIT_XY);
                        newButton.setImageDrawable(RBD);
                        newButton.setPaddingRelative(0,0,0,0);
                    }
                }
            };

            newButton.setLayoutParams(params);
            //TextView testText = findViewById(R.id.topcharthead);
            if (link.contains("amazon")) {
                //myImage[0] = getBitmapFromURL(imgurl);

                TextView testText = findViewById(R.id.topcharthead);
                String[] amazonEmbdLInk = link.replace("albums","embed").split("\\?");
                textCrawler.makePreview( linkPreviewCallback, amazonEmbdLInk[0]);
            }
            else if(link.contains("spotify"))
            {
                //textCrawler.makePreview( linkPreviewCallback, "https://open.spotify.com/track/0dnDTvdUco2UbaBjUtPxNS");
            }
            else {
                /*Bitmap myImage;
                //myImage = getBitmapFromURL(imgList.get(1));
                myImage = getBitmapFromURL(url);
                //Drawable dr = new BitmapDrawable(myImage);

                RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), myImage);
                RBD.setCornerRadius(10.0f);
                RBD.setAntiAlias(true);

                newButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                newButton.setImageDrawable(RBD);*/
                textCrawler.makePreview( linkPreviewCallback, "https://img.youtube.com/vi/" + videoid + "/0.jpg");
            }

            GradientDrawable buttonGD = new GradientDrawable();

            buttonGD.setColor(Color.TRANSPARENT);
            if(link.contains("spotify"))
            {
                buttonGD.setColor(Color.parseColor("#0aa83a"));
            }
            //buttonGD.setColor(Color.parseColor("#ffffff"));
            buttonGD.setCornerRadius(10.0f);
            newButton.setBackground(buttonGD);
            newButton.setPaddingRelative(0,0,0,0);

            LinearLayout topBtnWrapLayout = new LinearLayout(MainActivity.this);
            topBtnWrapLayout.setOrientation(LinearLayout.VERTICAL);

            TextView topBtnText = new TextView(MainActivity.this);
            topBtnText.setText(name);
            topBtnText.setTextColor(Color.parseColor("#ffffff"));
            topBtnText.setPadding(0,20,0,0);
            topBtnText.setGravity(Gravity.CENTER_HORIZONTAL);
            //topBtnText.setBackgroundColor(Color.parseColor("#000000"));
            final String finalLink = link;
            final String finalName = name;
            final long finalId = id;
            final String finalLink1 = link;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickedYoutubeID = extractYTId(finalLink1);
                    if(finalLink.contains("amazon")) {

                        playAmazonLink(finalLink);
                    }
                    else
                    {
                        playYTLinkBottom(clickedYoutubeID, finalName, (int)finalId);
                    }

                }
            });
            if (topTracksCount == 1) {
                LinearLayout.LayoutParams topoftopchartsparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                topoftopchartsparams.width = 700;
                topoftopchartsparams.height = 700;
                //topoftopchartsparams.setMargins(0,0,100,0);

                 newButton.setLayoutParams(topoftopchartsparams);
                topBtnText.setTextSize(18);
                topBtnText.setPadding(0,40,0,0);
                topBtnWrapLayout.addView(newButton);
                topBtnWrapLayout.addView(topBtnText);
                topBtnWrapLayout.setPadding(0,0,100,0);
                topOfTopCharts.addView(topBtnWrapLayout);
            } else if(topTracksCount > 1 && topTracksCount <= 6) {
                topBtnWrapLayout.addView(newButton);
                topBtnWrapLayout.addView(topBtnText);
                topBtnWrapLayout.setPadding(0,0,80,0);
                topChartsLayout.addView(topBtnWrapLayout);
            }
            else
            {
                topBtnWrapLayout.addView(newButton);
                topBtnWrapLayout.addView(topBtnText);
                topChartsLayout2.addView(topBtnWrapLayout);
                topBtnWrapLayout.setPadding(0,0,80,0);
                topChartsLayout2.setPadding(0,90,0,0);
            }
        }

        updateNewSongs();
        LinearLayout.LayoutParams paramsPlaylistSongs = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsPlaylistSongs.width = 400;
        paramsPlaylistSongs.height = 400;
        LinearLayout playlistLayout = (LinearLayout) findViewById(R.id.playlistslinearlayer);
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(this);
            newButton.setBackgroundResource(R.drawable.test_image);
            
            newButton.setLayoutParams(paramsPlaylistSongs);
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
        LinearLayout.LayoutParams paramsNewSongs = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsNewSongs.width = 200;
        paramsNewSongs.height = 200;
        paramsNewSongs.setMargins(0, 0, 20, 0);
        LinearLayout popularRelative = (LinearLayout) findViewById(R.id.popularcontent);
        for(int i = 0; i < 3; i++)
        {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView popularTitle = new TextView(MainActivity.this);
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
            popularTitle.setText("Popular Title");
            popularTitle.setTextColor(Color.parseColor("#ffffff"));
            popularRelative.addView(popularTitle);
            popularRelative.addView(linearLayout);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        viewPager.setNestedScrollingEnabled(false);

    }


    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
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

                    final ImageButton newButton = new ImageButton(this);

                    /** extract image from url*/

                    String videoid = extractYTId(recentVideoUrl);

                    String url = "https://img.youtube.com/vi/" + videoid + "/mqdefault.jpg";
                    final Bitmap myImage = getBitmapFromURL(url);

                    //Drawable dr = new BitmapDrawable(myImage);
                    RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), myImage);

                    RBD.setCornerRadius(100.0f);
                    RBD.setAntiAlias(true);
                    GradientDrawable gradDrawable = new GradientDrawable();
                    gradDrawable.setColor(Color.parseColor("#270075"));
                    gradDrawable.setCornerRadius(1000.0f);

                    LinearLayout.LayoutParams nonYTThumparam = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    nonYTThumparam.width = 181;
                    nonYTThumparam.height = 181;
                    nonYTThumparam.rightMargin = 30;
                    nonYTThumparam.leftMargin = 10;
                    nonYTThumparam.topMargin = 10;
                    nonYTThumparam.bottomMargin = 10;
                    TextCrawler textCrawler = new TextCrawler();
                    LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                        @Override
                        public void onPre() {

                        }

                        @Override
                        public void onPos(SourceContent sourceContent, boolean b) {
                            List<String> imgList = sourceContent.getImages();

                            if(imgList.size() >= 1)
                            {
                                Bitmap myImage;
                                myImage = getBitmapFromURL(imgList.get(imgList.size()-1));
                                RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), myImage);
                                RBD.setCornerRadius(1000.0f);
                                RBD.setAntiAlias(true);
                                newButton.setScaleType(ImageView.ScaleType.FIT_XY);
                                newButton.setImageDrawable(RBD);
                            }
                        }
                    };

                    if (recentVideoUrl.contains("amazon")) {
                        TextView testText = findViewById(R.id.topcharthead);
                        String[] amazonEmbdLInk = recentVideoUrl.replace("albums", "embed").split("\\?");
                        textCrawler.makePreview(linkPreviewCallback, amazonEmbdLInk[0]);
                        GradientDrawable buttonGD = new GradientDrawable();
                        buttonGD.setCornerRadius(100.0f);
                        //buttonGD.setColor(Color.TRANSPARENT);
                        buttonGD.setColor(Color.parseColor("#000000"));
                        newButton.setLayoutParams(nonYTThumparam);
                        newButton.setPaddingRelative(0,0,0,0);
                        newButton.setBackground(buttonGD);

                    } else {
                        newButton.setImageDrawable(RBD);
                        GradientDrawable buttonGD = new GradientDrawable();
                        buttonGD.setCornerRadius(100.0f);
                        buttonGD.setColor(Color.parseColor("#ebebeb"));
                        newButton.setBackground(buttonGD);
                        newButton.setLayoutParams(paramsNewSongs);
                    }
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
                    } else if (videoUrl.contains("amazon"))
                    {
                        extraText.setText("Prime Music");
                    }
                    else
                    {
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
                            if(videoUrl.contains("amazon")) {

                                playAmazonLink(videoUrl);
                             /*   WebView myWebView = (WebView) findViewById(R.id.webview);
                                myWebView.loadUrl("https://music.amazon.in/albums/B07YXYZN1R");*/
                            }
                            else
                            {
                                playYTLinkBottom(videoId,videoName,finalvideoId);
                            }

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
                    paramsLayoutSongs.height = 230;
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
                            if(videoUrl.contains("amazon")) {
                                playAmazonLink(videoUrl);
                            }
                            else
                            {
                                playYTLinkBottom(videoId,videoName,finalvideoId);
                            }

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
                    //startActivity(new Intent(MainActivity.this, SaveLInkActivity.class));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(MainActivity.this)) {
                        startService(new Intent(MainActivity.this, FloatingWidgetService.class));
                    } else {
                        errorToast();
                    }
                }
            });
            songsNew.setPadding(0, 0, 0, 140);
            songsNew.addView(linearLayout);
        }

    }

    private void playAmazonLink(String link)
    {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    private void playYTLinkBottom(String videoId, String name, int videoid)
    {
        currentPlayingId = videoid;
        LinearLayout.LayoutParams controls = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        controls.width = 100;
        controls.height = 100;

        controls.bottomMargin = 25;
        controls.topMargin = 25;
        controls.leftMargin = 50;
        controls.rightMargin = 50;

        LinearLayout.LayoutParams controlsTrack = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        controlsTrack.width = 60;
        controlsTrack.height = 60;
        controlsTrack.bottomMargin = 30;
        controlsTrack.topMargin = 45;
        controlsTrack.leftMargin = 60;
        controlsTrack.rightMargin = 60;

        LinearLayout.LayoutParams optionParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        optionParam.gravity = Gravity.CENTER;
        LinearLayout ytContainer = findViewById(R.id.youtubecontainer);

        ytContainer.setBackgroundColor(Color.parseColor("#000000"));
        LinearLayout ytOptions = findViewById(R.id.layoutoptions);
        ytOptions.removeAllViews();

        LinearLayout ytOptions1 = new LinearLayout(MainActivity.this);
        ytOptions1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout ytOptions2 = new LinearLayout(MainActivity.this);
        ytOptions.setOrientation(LinearLayout.VERTICAL);

        final Button openinYTpause = new Button(MainActivity.this);
        Button openinYTnext = new Button(MainActivity.this);
        Button openinYTprevious = new Button(MainActivity.this);

        TextView titleText = new TextView(MainActivity.this);
        titleText.setText(name);
        titleText.setTextColor(Color.parseColor("#ffffff"));
        titleText.setTextSize(16);
        titleText.setPadding(0,0,0,5);
        ytOptions1.addView(titleText);

        TextView extraText = new TextView(MainActivity.this);
        extraText.setText("Youtube");
        extraText.setTextColor(Color.parseColor("#808080"));
        extraText.setPadding(0,0,0,30);
        ytOptions1.addView(extraText);
        ytOptions1.setPadding(70,30,0,0);


        openinYTnext.setLayoutParams(controlsTrack);
        openinYTpause.setLayoutParams(controls);
        openinYTprevious.setLayoutParams(controlsTrack);

        openinYTnext.setBackgroundResource(R.drawable.nexttrack);
        openinYTnext.setTextColor(Color.parseColor("#000000"));
        openinYTpause.setBackgroundResource(R.drawable.pause);
        openinYTpause.setTextColor(Color.parseColor("#000000"));
        openinYTprevious.setBackgroundResource(R.drawable.previoustrack);

        openinYTprevious.setTextColor(Color.parseColor("#000000"));



        GradientDrawable buttonGD = new GradientDrawable();
        buttonGD.setCornerRadius(1000);
        buttonGD.setColor(Color.parseColor("#ebebeb"));
        ytOptions2.setLayoutParams(optionParam);
        ytOptions2.setBackground(buttonGD);

        openinYTpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(youtubePlayerHandle.isPlaying())
                {
                openinYTpause.setBackgroundResource(R.drawable.playicon);
                youtubePlayerHandle.pause();}
                else
                {
                    openinYTpause.setBackgroundResource(R.drawable.pause);
                    youtubePlayerHandle.play();
                }
            }
        });

        ytOptions2.addView(openinYTprevious);
        ytOptions2.addView(openinYTpause);
        ytOptions2.addView(openinYTnext);

        LinearLayout.LayoutParams optionsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        ytOptions.setLayoutParams(optionsParams);
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

 /*           int width2 = cropImg.getWidth();
            int height2 = cropImg.getHeight();

            float scaleWidth = ((float) 250) / width2;
            float scaleHeight = ((float) 250) / height2;

// CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();

// RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

// RECREATE THE NEW BITMAP
            Bitmap resizeBitmap = Bitmap.createBitmap(cropImg, 0, 0, width2, height2, matrix, false);

            return resizeBitmap;*/

            return  cropImg;
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
        // To prevent starting the service if the required permission is NOT granted.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            startService(new Intent(MainActivity.this, FloatingWidgetService.class).putExtra("activity_background", true));
            finish();
        } else {
            errorToast();
        }
    }

    private void errorToast() {
        Toast.makeText(this, "Draw over other app permission not available. Can't start the application without the permission.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //Permission is not available. Display error text.
                    errorToast();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            this.youtubePlayerHandle = youTubePlayer;
            this.youtubePlayerHandle.setPlayerStateChangeListener(playerStateChangeListener);
            this.youtubePlayerHandle.setPlaybackEventListener(playbackEventListener);
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            //showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            //showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            //showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            showMessage("video Ended");

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
            // Called when an error occurs.
        }
    }
}
