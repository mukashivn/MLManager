package com.javiersantos.mlmanager.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.javiersantos.mlmanager.R;
import com.javiersantos.mlmanager.adapters.AppAdapter;
import com.javiersantos.mlmanager.fragment.FavouriteAppFragment;
import com.javiersantos.mlmanager.fragment.HiddenAppFragment;
import com.javiersantos.mlmanager.fragment.InstalledAppFragment;
import com.javiersantos.mlmanager.fragment.SystemAppFragment;
import com.javiersantos.mlmanager.utils.UtilsApp;
import com.javiersantos.mlmanager.utils.UtilsDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

  private SearchView searchView;
  private MenuItem searchItem;
  private Toolbar toolbar;
  private BottomBar mBottomBar;
  private FrameLayout mMainContent;
  private Map<Integer,Fragment> mMap = new HashMap<>();
  private FragmentManager mFragmentManager;
  private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;
  private Context context;
  private int mCurrentTab;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);
    ButterKnife.bind(this);
    initManager();
    initToolBar();
    initBottomBar();
    checkAndAddPermissions(this);
    setAppDir();
    this.context = this;
  }
  void initManager(){
    mMainContent = (FrameLayout)findViewById(R.id.main_content);
    mFragmentManager = getSupportFragmentManager();
  }
  private void initToolBar(){
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle(null);
    }
  }

  private void initBottomBar(){
    mBottomBar = (BottomBar)findViewById(R.id.bottomBar);
    mBottomBar.selectTabAtPosition(0);
    mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
      @Override
      public void onTabSelected(@IdRes int tabId) {
        handleTabListener(tabId);
      }
    });
    mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
      @Override
      public void onTabReSelected(@IdRes int tabId) {
        handleTabListener(tabId);
      }
    });
  }

  private void checkAndAddPermissions(Activity activity) {
    UtilsApp.checkPermissions(activity);
  }

  private void setAppDir() {
    File appDir = UtilsApp.getAppFolder();
    if(!appDir.exists()) {
      appDir.mkdir();
    }
  }
  private void resetSearchView(){
    if (searchView != null){
      searchView.setQuery("",true);
      searchView.clearFocus();
    }
  }
  private void handleTabListener(int tabId){
    mCurrentTab = tabId;
    //Reset search view when change tab
    resetSearchView();
    showOrHideSearch(tabId);

    switch (tabId){
      case R.id.tab_system_app:
        showFragment(tabId);
        break;
      case R.id.tab_setting:
        gotoSettingAc();
        break;
      case R.id.tab_favorites:
        showFragment(tabId);
        break;
      case R.id.tab_hidden_app:
        showFragment(tabId);
        break;
      case R.id.tab_install_app:
        showFragment(tabId);
        break;
    }
  }

  private void hideOrShowSearch(boolean isShow){
    if(searchItem != null)
      searchItem.setVisible(isShow);
  }

  private void showOrHideSearch(int tabId){
    switch (tabId){
      case R.id.tab_favorites:
        hideOrShowSearch(false);
        break;
      case R.id.tab_hidden_app:
        hideOrShowSearch(false);
        break;
      case R.id.tab_install_app:
        hideOrShowSearch(true);
        break;
      case R.id.tab_system_app:
        hideOrShowSearch(true);
        break;
    }
  }

  private void gotoSettingAc(){
    Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
    startActivity(intent);
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    searchItem = menu.findItem(R.id.action_search);
    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setOnQueryTextListener(this);

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

    return true;
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String search) {
    /*
    if (search.isEmpty()) {
      ((AppAdapter) recyclerView.getAdapter()).getFilter().filter("");
    } else {
      ((AppAdapter) recyclerView.getAdapter()).getFilter().filter(search.toLowerCase());
    }

    return false;
    */
    switch (mCurrentTab){
      case R.id.tab_install_app:
        searchInstallApp(search);
      break;
      case R.id.tab_system_app:
        searchSystemApp(search);
        break;
    }
    return false;
  }

  private void searchInstallApp(String search){
    InstalledAppFragment fragment = (InstalledAppFragment)mMap.get(R.id.tab_install_app);
    RecyclerView recyclerView = fragment.getRecycleView();
    if (search.isEmpty()) {
      ((AppAdapter) recyclerView.getAdapter()).getFilter().filter("");
    } else {
      ((AppAdapter) recyclerView.getAdapter()).getFilter().filter(search.toLowerCase());
    }
  }
  private void searchSystemApp(String search){
    SystemAppFragment fragment = (SystemAppFragment) mMap.get(R.id.tab_system_app);
    if(fragment != null) {
      RecyclerView recyclerView = fragment.getRecycleView();
      if (recyclerView != null) {
        if (search.isEmpty()) {
          ((AppAdapter) recyclerView.getAdapter()).getFilter().filter("");
        } else {
          ((AppAdapter) recyclerView.getAdapter()).getFilter().filter(search.toLowerCase());
        }
      }
    }
  }

  private void showFragment(int fragmentId){
    FragmentTransaction transaction = mFragmentManager.beginTransaction();

    //Hide all fragment
    for(Fragment fragment:mMap.values()){
      if(fragment != null)
        transaction.hide(fragment);
    }
    Fragment fragment = mMap.get(fragmentId);
    if(fragment != null){
      //show it
      transaction.show(fragment).commit();
    }else{
      fragment = createFragment(fragmentId);
      transaction.add(R.id.main_content,fragment).commit();

    }
  }
  private Fragment createFragment(int fragmentId){
    Fragment fragment = null;
    switch (fragmentId){
      case R.id.tab_favorites:
        fragment = FavouriteAppFragment.newInstance();
        break;
      case R.id.tab_hidden_app:
        fragment = HiddenAppFragment.newInstance();
        break;
      case R.id.tab_install_app:
        fragment = InstalledAppFragment.newInstance();
        break;
      case R.id.tab_system_app:
        fragment = SystemAppFragment.newInstance();
        break;
    }
    mMap.put(fragmentId,fragment);
    return fragment;
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_WRITE_READ: {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          UtilsDialog.showTitleContent(context, getResources().getString(R.string.dialog_permissions), getResources().getString(R.string.dialog_permissions_description));
        }
      }
    }
  }

}
