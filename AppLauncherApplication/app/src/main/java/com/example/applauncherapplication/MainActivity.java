package com.example.applauncherapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.Toast;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import static com.example.applauncherapplication.ApplicationClass.softwareApps;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String[] sortingMethods = {"Sort by name", "Sort by recently used", "Sort by memory", "Sort by package"};
    private AutoCompleteTextView autoCompleteText;
    private ArrayAdapter<String> sortAdapter;
    private Button btnEnable;
    private SearchView swFilter;
    private FragmentRefreshListener fragmentRefreshListener;

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteText = (AutoCompleteTextView) findViewById(R.id.autoComplete);
        btnEnable = (Button) findViewById(R.id.btnEnable);
        swFilter = (SearchView) findViewById(R.id.swFilter);
        setUpDropDownMenu();
        setUpSearchBar();

    }

    private void setUpSearchBar() {
        swFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<SoftwareApp> filteredApps = new ArrayList<>();
                for (SoftwareApp sApp : softwareApps) {
                    if (sApp.getAppName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredApps.add(sApp);
                    }
                }
                if (filteredApps.size() == 0) {
                    SoftwareApp app = new SoftwareApp();
                    app.setAppName("Search with Google Play");
                    app.setDescription(newText);
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getDrawable(R.drawable.test3);
                    app.setAppIcon(drawable);
                    filteredApps.add(app);
                }
                getFragmentRefreshListener().onRefresh("None", filteredApps);
                return false;
            }
        });
    }

    private void setUpDropDownMenu() {
        sortAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_list_item, sortingMethods);
        autoCompleteText.setAdapter(sortAdapter);

        autoCompleteText.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            getFragmentRefreshListener().onRefresh(item, softwareApps);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isPermissionGranted()) {
            setUpUsageStats();
            setUpStorageStats();
            btnEnable.setVisibility(View.GONE);
        } else {
            btnEnable.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        }
    }

    private boolean isPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.unsafeCheckOpNoThrow(OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            return (mode == MODE_ALLOWED);
        }
    }

    private void setUpUsageStats() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -10);
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(cal.getTimeInMillis(), System.currentTimeMillis());
        for (SoftwareApp sApp : ApplicationClass.softwareApps) {
            if (usageStatsMap.containsKey(sApp.getPackageName())) {
                UsageStats usageStats = usageStatsMap.get(sApp.getPackageName());
                assert usageStats != null;
                sApp.setLastTimeUsed(usageStats.getLastTimeUsed());
                sApp.setTotalTimeInForeground(usageStats.getTotalTimeInForeground());
            } else {
                sApp.setLastTimeUsed(-1);
                sApp.setTotalTimeInForeground(-1);
            }
        }
    }


    private void setUpStorageStats() {
        StorageStatsManager storageStatsManager = (StorageStatsManager) getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null || storageStatsManager == null) {
            return;
        }
        UserHandle user = android.os.Process.myUserHandle();
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        for (StorageVolume storageVolume : storageVolumes) {
            if (storageVolume.isPrimary()) {
                String uuidStr = storageVolume.getUuid();
                UUID uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
                for (SoftwareApp sApp : ApplicationClass.softwareApps) {
                    try {
                        StorageStats storageStats = storageStatsManager.queryStatsForPackage(uuid, sApp.getPackageName(), user);
                        sApp.setAppSize(storageStats.getAppBytes());
                    } catch (IOException | PackageManager.NameNotFoundException e) {
                        //Log.d("DEBUG", "Hello from function");
                        if (sApp.getAppSize() == 0) {
                            sApp.setAppSize(0);
                        }
                    }
                }
            }
        }
    }

    public interface FragmentRefreshListener {
        void onRefresh(String sort, ArrayList<SoftwareApp> softwareApps);
    }
}
