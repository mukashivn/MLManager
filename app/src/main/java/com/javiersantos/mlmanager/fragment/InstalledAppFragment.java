package com.javiersantos.mlmanager.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.javiersantos.mlmanager.AppInfo;
import com.javiersantos.mlmanager.MLManagerApplication;
import com.javiersantos.mlmanager.R;
import com.javiersantos.mlmanager.adapters.AppAdapter;
import com.javiersantos.mlmanager.utils.AppPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hi on 12/9/16.
 */

//@EFragment
public class InstalledAppFragment extends Fragment {
  @BindView(R.id.rv_install_app)
  RecyclerView rvInstallAppView;
  @BindView(R.id.progress)
  ProgressBar progressBar;
  AppAdapter rvInstallAppAdapter;
  List<AppInfo> rvListApp;
  View mRootView;
  // Load Settings
  private AppPreferences appPreferences;

  public static InstalledAppFragment newInstance() {

    Bundle args = new Bundle();

    InstalledAppFragment fragment = new InstalledAppFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_install_app, container, false);
    ButterKnife.bind(this, mRootView);
    init();
    return mRootView;
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

  private void getInstallApp() {
    new GetInstallAppAsync().execute();
  }

  class GetInstallAppAsync extends AsyncTask<Void, String, Void> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressBar.setVisibility(View.VISIBLE);
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
              rvListApp.add(tempApp);
            } catch (OutOfMemoryError e) {
              //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, getResources().getDrawable(R.drawable.ic_android), false);
              rvListApp.add(tempApp);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }

      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      progressBar.setVisibility(View.GONE);
      rvInstallAppAdapter.notifyDataSetChanged();
    }

  }

  public RecyclerView getRecycleView(){
    return rvInstallAppView;
  }
}
