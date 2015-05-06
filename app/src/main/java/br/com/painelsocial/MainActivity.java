package br.com.painelsocial;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mLeftDrawerView;

    private Fragment mContent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);

        if (Config.getInstance().getToken() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            finish();
            return;
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawerView = findViewById(R.id.left_drawer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.menu, R.string.menu) {

            public void onDrawerClosed(View drawerView) {
                if(drawerView.equals(mLeftDrawerView)) {
                    supportInvalidateOptionsMenu();
                }
            }

            public void onDrawerOpened(View drawerView) {
                if(drawerView.equals(mLeftDrawerView)) {
                    supportInvalidateOptionsMenu();
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(drawerView.equals(mLeftDrawerView)) {
                    super.onDrawerSlide(drawerView, slideOffset);
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }

        if (mContent == null) {
            mContent = new MapFragment();
        }

        // Menu
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_drawer, new MenuFragment())
                .commit();

        // Conteudo
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.toogleMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void toogleMenu() {
        if (mDrawerLayout.isDrawerOpen(mLeftDrawerView)) {
            mDrawerLayout.closeDrawer(mLeftDrawerView);
        } else {
            mDrawerLayout.openDrawer(mLeftDrawerView);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mLeftDrawerView)) {
            mDrawerLayout.closeDrawer(mLeftDrawerView);
            return;
        }

        if (getSupportFragmentManager().popBackStackImmediate()) {
            mContent = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            return;
        }

        this.moveTaskToBack(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "mContent", getSupportFragmentManager().findFragmentById(R.id.content_frame));
    }

    public void switchContent(Fragment fragment) {
        switchContent(fragment, false);
    }

    public void switchContent(Fragment fragment, boolean dontStore) {
        mDrawerLayout.closeDrawer(mLeftDrawerView);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (currentFragment != null && currentFragment.getClass() == fragment.getClass()) {
            return;
        }

        mContent = fragment;

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();

        tx.replace(R.id.content_frame, fragment);

        if (!dontStore) {
            tx.addToBackStack(null);
        }

        tx.commit();
    }

}
