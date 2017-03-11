package com.sfuronlabs.livescore.football.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.sfuronlabs.livescore.football.R;
import com.sfuronlabs.livescore.football.fragments.LineupsFragment;
import com.sfuronlabs.livescore.football.fragments.MatchInfoFragment;
import com.sfuronlabs.livescore.football.model.MatchDetails;
import com.sfuronlabs.livescore.football.service.DefaultMessageHandler;
import com.sfuronlabs.livescore.football.service.NetworkService;
import com.sfuronlabs.livescore.football.util.RoboAppCompatActivity;
import com.squareup.picasso.Picasso;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author Ripon
 */
@ContentView(R.layout.activity_match_details)
public class MatchDetailsActivity extends RoboAppCompatActivity{

    @Inject
    private NetworkService networkService;

    @InjectView(R.id.tv_local_team)
    private TextView localTeam;

    @InjectView(R.id.tv_date)
    private TextView date;

    @InjectView(R.id.tv_score)
    private TextView scoreline;

    @InjectView(R.id.tv_time)
    private TextView time;

    @InjectView(R.id.tv_visitor_team)
    private TextView visitorTeam;

    @InjectView(R.id.logo_local_team)
    private ImageView localTeamLogo;

    @InjectView(R.id.logo_visitor_team)
    private ImageView visitorTeamLogo;

    private MatchDetails matchDetails;

    String[] titleText = new String[]{"Match Info", "Lineups"};

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private String matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        matchId = getIntent().getStringExtra("matchId");
        mViewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        loadData();
    }

    private void loadData() {
        Log.d("ripon", "http://holoduke.nl/footapi/matches/"+matchId+".json");

        networkService.fetchMatchDetails(matchId, new DefaultMessageHandler(this,true){
            @Override
            public void onSuccess(Message msg) {
                matchDetails = (MatchDetails) msg.obj;

                Log.d("ripon", matchDetails.toString());
                localTeam.setText(matchDetails.getLocalTeam());
                visitorTeam.setText(matchDetails.getVisitorTeam());
                date.setText(matchDetails.getDate());
                scoreline.setText(matchDetails.getScoreLine());
                time.setText(matchDetails.getStatus() + "'");

                Picasso.with(MatchDetailsActivity.this).load("http://static.holoduke.nl/footapi/images/teams_gs/"+
                        matchDetails.getLocalTeamId()+"_small.png").into(localTeamLogo);

                Picasso.with(MatchDetailsActivity.this).load("http://static.holoduke.nl/footapi/images/teams_gs/"+
                        matchDetails.getVisitorTeamId()+"_small.png").into(visitorTeamLogo);

                localTeamLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamKey", matchDetails.getLocalTeamId());
                        startActivity(intent);
                    }
                });

                visitorTeamLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatchDetailsActivity.this, TeamDetailsActivity.class);
                        intent.putExtra("teamKey", matchDetails.getVisitorTeamId());
                        startActivity(intent);
                    }
                });

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                MatchInfoFragment fragment = new MatchInfoFragment();
                Bundle args = new Bundle();
                args.putSerializable("matchdetails", matchDetails);
                fragment.setArguments(args);
                return fragment;
            } else {
                LineupsFragment fragment = new LineupsFragment();
                Bundle args = new Bundle();
                args.putSerializable("matchdetails", matchDetails);
                fragment.setArguments(args);
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return titleText.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleText[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load:
                loadData();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
