package com.loopbookinc.sonder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import com.loopbookinc.sonder.helper.BottomNavigationViewHelper;

public class Navbar extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                   // mTextMessage.setText(R.string.title_home);
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                  //  mTextMessage.setText(R.string.title_dashboard);
                    selectedFragment = new DashboardFragment();
                    break;
                case R.id.navigation_notifications:
                   // mTextMessage.setText(R.string.title_notifications);
                    selectedFragment = new NotificationsFragment();
                    break;
                case R.id.navigation_person:
                   // mTextMessage.setText(R.string.title_people);
                    selectedFragment = new PeopleFragment();
                    break;
            }
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            replaceFragment(selectedFragment);
            return true;
        }
    };

    private void replaceFragment (Fragment fragment){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        //disabled shiftmode
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
