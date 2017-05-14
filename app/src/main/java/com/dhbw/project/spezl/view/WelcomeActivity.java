package com.dhbw.project.spezl.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dhbw.project.spezl.R;
import com.dhbw.project.spezl.controller.PrefManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WelcomeActivity extends AppCompatActivity {

    // Viewpager with slides.
    private ViewPager viewPager;

    // Layout with the dots.
    private LinearLayout dotsLayout;

    // Int with slides.
    private int[] layouts;

    // Button to skip or next.
    private Button btnSkip, btnNext;

    // PrefManager for check if user never started the app before.
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);

        // If user want to show the Intro again.
        boolean showIntro = false;
        Intent intent = getIntent();
        if (intent.hasExtra("SHOW_INTRO")) {
            showIntro = intent.getBooleanExtra("SHOW_INTRO", false);
            Log.d("SHOW_INTRO", "" + showIntro);
        }

        // Check if user lauchnes the app for the first time.
        if (!prefManager.isFirstTimeLaunch()) {
            if (showIntro) {
                showIntro();
            } else {
                launchHomeScreen();
            }
        }

        super.onCreate(savedInstanceState);
    }

    /**
     * Show the slides.
     */
    public void showIntro() {
        // Making notification bar transparent.
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);

        // Find the views.
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        // Find the slides.
        layouts = new int[]{R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots.
        addBottomDots(0);

        // making notification bar transparent.
        changeStatusBarColor();

        // Setup viewPager.
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        // Setup skipButton.
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        // Setup nextButton.
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Launch Main Activity.
     */
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Add Buttons to the LinearLayout.
     *
     * @param currentPage Current Page.
     */
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                dots[i].setText(Html.fromHtml("&#8226;"));
            }
            dots[i].setTextSize(35);
            dots[i].setGravity(Gravity.TOP);
            dots[i].setTextColor(ContextCompat.getColor(WelcomeActivity.this, R.color.colorAccentCloudy));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(WelcomeActivity.this, R.color.colorAccent));
    }

    /**
     * @param i item number.
     * @return curren Item.
     */
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText("Los Spezln");
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.text_next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * View pager adapter.
     */
    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/AmaticSC-Regular.ttf");

            switch (position) {
                case 0://First Slide
                    TextView label0 = (TextView) view.findViewById(R.id.app_name_label);
                    label0.setTypeface(typeFace);
                    break;
                case 1:
                    TextView label1 = (TextView) view.findViewById(R.id.app_name_label);
                    label1.setTypeface(typeFace);
                    ImageView icon1 = (ImageView) view.findViewById(R.id.icon1);
                    ImageView icon2 = (ImageView) view.findViewById(R.id.icon2);
                    ImageView icon3 = (ImageView) view.findViewById(R.id.icon3);
                    ImageView icon4 = (ImageView) view.findViewById(R.id.icon4);
                    ImageView icon5 = (ImageView) view.findViewById(R.id.icon5);
                    ImageView icon6 = (ImageView) view.findViewById(R.id.icon6);
                    try {
                        icon1.setBackground(getAssetImage(WelcomeActivity.this, "icon1"));
                        icon2.setBackground(getAssetImage(WelcomeActivity.this, "icon2"));
                        icon3.setBackground(getAssetImage(WelcomeActivity.this, "icon3"));
                        icon4.setBackground(getAssetImage(WelcomeActivity.this, "icon4"));
                        icon5.setBackground(getAssetImage(WelcomeActivity.this, "icon5"));
                        icon6.setBackground(getAssetImage(WelcomeActivity.this, "icon6"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    TextView label2 = (TextView) view.findViewById(R.id.app_name_label);
                    label2.setTypeface(typeFace);

                    ImageView icon7 = (ImageView) view.findViewById(R.id.slide3_icon);
                    try {
                        icon7.setBackground(getAssetImage(WelcomeActivity.this, "icon1"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    TextView label3 = (TextView) view.findViewById(R.id.app_name_label);
                    label3.setTypeface(typeFace);

                    ImageView icon8 = (ImageView) view.findViewById(R.id.slide4_icon);
                    try {
                        icon8.setBackground(getAssetImage(WelcomeActivity.this, "popUp"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public static Drawable getAssetImage(Context context, String filename) throws IOException {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = new BufferedInputStream((assets.open("drawable/" + filename + ".png")));
        Bitmap bitmap = BitmapFactory.decodeStream(buffer);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
