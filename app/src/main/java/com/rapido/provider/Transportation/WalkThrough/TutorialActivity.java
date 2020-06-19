package com.rapido.provider.Transportation.WalkThrough;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;


import com.rapido.provider.R;
import com.viewpagerindicator.CirclePageIndicator;


public class TutorialActivity extends FragmentActivity /*implements OnFaceBookLogin*/ {

    private ViewPager mPager;
    private MyPagerAdapter mAdapter;
    int positionFinal=0;
    CirclePageIndicator indicator ;

    ImageView iv_background_1, iv_background_2, iv_background_3, iv_background_4 ,iv_background_5 ,
            iv_background_6/*, iv_background_7 ,iv_background_8*/;
    private static final String TAG = "TutorialActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.tutorial);


        mAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        mPager.setCurrentItem(0);


        mPager.setOnPageChangeListener(new FragmentPageChangerListener());

        iv_background_1 = findViewById(R.id.iv_background_1);
        iv_background_2 = findViewById(R.id.iv_background_2);
        iv_background_3 = findViewById(R.id.iv_background_3);
        iv_background_4 = findViewById(R.id.iv_background_4);

       /* iv_background_5 = (ImageView) findViewById(R.id.iv_background_5);

        iv_background_6 = (ImageView) findViewById(R.id.iv_background_6);*/
//        iv_background_7 = (ImageView) findViewById(R.id.iv_background_7);
//        iv_background_8 = (ImageView) findViewById(R.id.iv_background_8);
    }


    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:

                    return Splashfragment1.newInstance();

                case 1:
                    return Splashfragment1.newInstance();

                case 2:
                    return Splashfragment1.newInstance();


                case 3:
                    return Splashfragment1.newInstance();

                case 4:
                    return Splashfragment1.newInstance();

                default:
                    return Splashfragment1.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getItemPosition(Object object) {

            System.out.println(object);
            return super.getItemPosition(object);
        }
    }


    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.textView9:

//                Prefs.makeStringPref(TutorialActivity.this, Prefs.isSkip,
//                        Constant.IS_SKIPP);
//                Intent   info;
//
//                if (Prefs.getStringPref(Prefs.USER_ID, TutorialActivity.this).isEmpty() && Prefs.getStringPref(Prefs.USER_NAME, TutorialActivity.this).isEmpty()) {
//                    info = new Intent(TutorialActivity.this,
//                            OTPActivity.class);
//                    startActivity(info);
//
//                } else {
//                    info = new Intent(TutorialActivity.this,
//                            Home.class);
//                    startActivity(info);
//
//                }

                break;
          /*  case R.id.textView8:


                if (positionFinal == 0) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCiX-yZgWtB04yz3ZH7DRpJQ/")));
                } else if (positionFinal == 1) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/G60xojGZp3E/")));
                } else if (positionFinal == 2) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/sLJPx47Gldo/")));
                } else if (positionFinal == 3) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/mG_PCjJXDPA/")));
                }else if (positionFinal == 4) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCiX-yZgWtB04yz3ZH7DRpJQ/")));
                }

                else if (positionFinal == 5) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/z9QFtCOFTZY/")));
                }
                else if (positionFinal == 6) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/4sDv6SJlpzw/")));
                }
                else if (positionFinal == 7) {
                   // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));

                }

                break;*/
        }

    }



    class FragmentPageChangerListener implements ViewPager.OnPageChangeListener {

        float alpha_rising = 0.95f;
        float alpha_diming = 0.75f;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            positionFinal=position;

            if (position == 0) {
                iv_background_1.setAlpha(1f / positionOffset);
                iv_background_2.setAlpha(positionOffset * alpha_rising);
                iv_background_3.setAlpha(0f);
                iv_background_4.setAlpha(0f);
         //       iv_background_5.setAlpha(0f);
         //       iv_background_6.setAlpha(0f);
//                iv_background_7.setAlpha(0f);
//                iv_background_8.setAlpha(0f);

                //indicator.setFillColor(getResources().getColor(R.color.);
            } else if (position == 1) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(1f / positionOffset);
                iv_background_3.setAlpha(positionOffset * alpha_rising);
                iv_background_4.setAlpha(0f);
         //       iv_background_5.setAlpha(0f);
          //      iv_background_6.setAlpha(0f);
//                iv_background_7.setAlpha(0f);
//                iv_background_8.setAlpha(0f);
            } else if (position == 2) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(0f);
                iv_background_3.setAlpha(1f / positionOffset);
                iv_background_4.setAlpha(positionOffset * alpha_rising);
        //        iv_background_5.setAlpha(0f);
        //        iv_background_6.setAlpha(0f);
//                iv_background_7.setAlpha(0f);
//                iv_background_8.setAlpha(0f);
            } else if (position == 3) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(0f);
                iv_background_3.setAlpha(0f);
                iv_background_4.setAlpha(1f / positionOffset);
         //       iv_background_5.setAlpha(positionOffset * alpha_rising);
       //         iv_background_6.setAlpha(0f);
//                iv_background_7.setAlpha(0f);
//                iv_background_8.setAlpha(0f);
            }else if (position == 4) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(0f);
                iv_background_3.setAlpha(0f);
                iv_background_4.setAlpha(0f);
       //         iv_background_5.setAlpha(1f / positionOffset);
       //         iv_background_6.setAlpha(positionOffset * alpha_rising);
//                iv_background_7.setAlpha(0f);
//                iv_background_8.setAlpha(0f);
            }

            else if (position == 5) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(0f);
                iv_background_3.setAlpha(0f);
                iv_background_4.setAlpha(0f);
       //         iv_background_5.setAlpha(0f);
      //          iv_background_6.setAlpha(1f / positionOffset);
//                iv_background_7.setAlpha(positionOffset * alpha_rising);
//                iv_background_8.setAlpha(0f);
            }

            /*else if (position == 6) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(0f);
                iv_background_3.setAlpha(0f);
                iv_background_4.setAlpha(0f);
                iv_background_5.setAlpha(0f);
                iv_background_6.setAlpha(0f);
                iv_background_7.setAlpha(1f / positionOffset);
                iv_background_8.setAlpha(positionOffset * alpha_rising);
            }

            else if (position == 7) {

                iv_background_1.setAlpha(0f);
                iv_background_2.setAlpha(0f);
                iv_background_3.setAlpha(0f);
                iv_background_4.setAlpha(0f);
                iv_background_5.setAlpha(0f);
                iv_background_6.setAlpha(0f);
                iv_background_7.setAlpha(0f);
                iv_background_8.setAlpha(1f / positionOffset);
            }*/

            if (positionOffset == 0) {
                indicator.setCurrentItem(position);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


}
