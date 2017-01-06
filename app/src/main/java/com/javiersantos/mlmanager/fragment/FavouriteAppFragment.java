package com.javiersantos.mlmanager.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.javiersantos.mlmanager.AppInfo;
import com.javiersantos.mlmanager.MLManagerApplication;
import com.javiersantos.mlmanager.R;
import com.javiersantos.mlmanager.adapters.AppAdapter;
import com.javiersantos.mlmanager.utils.AppPreferences;
import com.javiersantos.mlmanager.utils.UtilsApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hi on 12/12/16.
 */

public class FavouriteAppFragment extends Fragment{
  private View mRootView;
  @BindView(R.id.rv_fav_app)
  RecyclerView rvInstallAppView;
  @BindView(R.id.progress)
  ProgressBar progressBar;
  AppAdapter rvInstallAppAdapter;
  List<AppInfo> rvListApp;
  @BindView(R.id.txt_nodata_view)
  TextView mNodataView;

  private List<AppInfo> appList;
  private List<AppInfo> appSystemList;

  // Load Settings
  private AppPreferences appPreferences;

  public static FavouriteAppFragment newInstance() {
    
    Bundle args = new Bundle();
    
    FavouriteAppFragment fragment = new FavouriteAppFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mRootView = (View)inflater.inflate(R.layout.fragment_favourite_app,container,false);
    return mRootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this,view);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
   // init();
  }
  private void init() {
    rvListApp = new ArrayList<>();
    rvInstallAppAdapter = new AppAdapter(rvListApp, getContext());
    rvInstallAppView.setAdapter(rvInstallAppAdapter);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rvInstallAppView.setLayoutManager(layoutManager);
    this.appPreferences = MLManagerApplication.getAppPreferences();
    getInstallApp();
  }

  private void refresh(){
    init();
  }

  @Override
  public void onResume() {
    super.onResume();
    refresh();
  }

  private void getInstallApp() {
    new GetInstallAppAsync().execute();
  }

  class GetInstallAppAsync extends AsyncTask<Void, String, Void> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressBar.setVisibility(View.VISIBLE);
      appList = new ArrayList<>();
      appSystemList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... voids) {
      final PackageManager packageManager = getContext().getPackageManager();
      List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

      switch (appPreferences.getSortMode()) {
        default:
          // Comparator by Name (default)
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              return packageManager.getApplicationLabel(p1.applicationInfo).toString().toLowerCase().compareTo(packageManager.getApplicationLabel(p2.applicationInfo).toString().toLowerCase());
            }
          });
          break;
        case "2":
          // Comparator by Size
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              Long size1 = new File(p1.applicationInfo.sourceDir).length();
              Long size2 = new File(p2.applicationInfo.sourceDir).length();
              return size2.compareTo(size1);
            }
          });
          break;
        case "3":
          // Comparator by Installation Date (default)
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              return Long.toString(p2.firstInstallTime).compareTo(Long.toString(p1.firstInstallTime));
            }
          });
          break;
        case "4":
          // Comparator by Last Update
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              return Long.toString(p2.lastUpdateTime).compareTo(Long.toString(p1.lastUpdateTime));
            }
          });
          break;
      }

      // Installed & System Apps
      for (PackageInfo packageInfo : packages) {
        if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {

          if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            try {
              // Non System Apps
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
              appList.add(tempApp);
            } catch (OutOfMemoryError e) {
              //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, getResources().getDrawable(R.drawable.ic_android), false);
              appList.add(tempApp);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            try {
              // System Apps
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), true);
              appSystemList.add(tempApp);
            } catch (OutOfMemoryError e) {
              //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, getResources().getDrawable(R.drawable.ic_android), false);
              appSystemList.add(tempApp);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }

      }
      getFavoriteList(appList,appSystemList);

      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      progressBar.setVisibility(View.GONE);
      if(rvListApp.size()==0){
        mNodataView.setVisibility(View.VISIBLE);
      }else {
        mNodataView.setVisibility(View.GONE);
      }
      rvInstallAppAdapter.notifyDataSetChanged();
    }

  }

  public RecyclerView getRecycleView(){
    return rvInstallAppView;
  }

  private void getFavoriteList(List<AppInfo> appList, List<AppInfo> appSystemList) {


    for (AppInfo app : appList) {
      if (UtilsApp.isAppFavorite(app.getAPK(), appPreferences.getFavoriteApps())) {
        rvListApp.add(app);
      }
    }
    for (AppInfo app : appSystemList) {
      if (UtilsApp.isAppFavorite(app.getAPK(), appPreferences.getFavoriteApps())) {
        rvListApp.add(app);
      }
    }
  }
}
