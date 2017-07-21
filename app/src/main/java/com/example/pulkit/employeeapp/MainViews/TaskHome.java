package com.example.pulkit.employeeapp.MainViews;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.Notification.NotificationActivity;
import com.example.pulkit.employeeapp.Quotation.QuotationGroups;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.chat.chatFrag;

import java.util.ArrayList;
import java.util.List;

public class TaskHome extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String emp_id,desig;
    EmployeeSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_home);

        session = new EmployeeSession(getApplicationContext());
        emp_id = getIntent().getStringExtra("emp_id");
        desig = getIntent().getStringExtra("desig");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.notif:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                intent.putExtra("Username",session.getUsername());
                startActivity(intent);
                break;
        }
        return true;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(desig.toLowerCase().equals("quotation"))
            adapter.addFragment(new QuotationGroups(), "Groups");
        else
            adapter.addFragment(new taskFrag(), "Tasks");
        adapter.addFragment(new chatFrag(), "Chat");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}