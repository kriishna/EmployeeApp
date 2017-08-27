package com.example.pulkit.employeeapp.MainViews;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.example.pulkit.employeeapp.Customer.custFrag;
import com.example.pulkit.employeeapp.EmployeeLogin.EmployeeSession;
import com.example.pulkit.employeeapp.R;
import com.example.pulkit.employeeapp.chat.chatFrag;
import com.example.pulkit.employeeapp.drawer;
import com.example.pulkit.employeeapp.helper.MarshmallowPermissions;
import com.example.pulkit.employeeapp.services.LocServ;

import java.util.ArrayList;
import java.util.List;

public class TaskHome extends drawer {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String emp_id, desig;
    EmployeeSession session;
    private MarshmallowPermissions marshmallowPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        getLayoutInflater().inflate(R.layout.activity_task_home, frame);

        marshmallowPermissions = new MarshmallowPermissions(this);
        if (!marshmallowPermissions.checkPermissionForCamera())
            marshmallowPermissions.requestPermissionForCamera();
        if (!marshmallowPermissions.checkPermissionForExternalStorage())
            marshmallowPermissions.requestPermissionForExternalStorage();
        checkForLoc();
        session = new EmployeeSession(getApplicationContext());
        if(session.get_ShortCutInstalled()==false)
        {
            createShortCut();
        }
        if (getIntent().hasExtra("emp_id")) {
            emp_id = getIntent().getStringExtra("emp_id");
            desig = getIntent().getStringExtra("desig");
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (desig.toLowerCase().equals("quotation"))
            adapter.addFragment(new custFrag(), "Customers");
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
    private void checkForLoc() {
        if(!marshmallowPermissions.checkPermissionForLocations()){
            marshmallowPermissions.requestPermissionForLocations();
        }
        if(marshmallowPermissions.checkPermissionForLocations())
        {
            final LocationManager manager = (LocationManager) getSystemService( this.LOCATION_SERVICE );
            if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Intent in = new Intent(this,LocServ.class);
                startService(in);
            }

        }
    }
    public void createShortCut(){
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), TaskHome.class));
        sendBroadcast(shortcutintent);
        session.set_ShortCutInstalled();
    }


}
