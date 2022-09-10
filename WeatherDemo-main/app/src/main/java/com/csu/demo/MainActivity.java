package com.csu.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.csu.demo.fragments.DatabaseFragment;
import com.csu.demo.fragments.MapFragment;
import com.csu.demo.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> onBottomMenuItemClicked(item));
        bottomNavigationView.setSelectedItemId(R.id.search);

    }

    //perform bottom menu item action clicked
    private boolean onBottomMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.database:
                loadFragment(new DatabaseFragment());
                return true;
            case R.id.search:
                loadFragment(new SearchFragment());
                return true;
            case R.id.map:
                loadFragment(new MapFragment());
                return true;
        }
        return false;
    }

    //loads selected fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}