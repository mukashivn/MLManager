package com.javiersantos.mlmanager.fragment;

import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hi on 12/12/16.
 */

public class HiddenAppFragment extends Fragment {

  @BindView(R.id.rv_hidden_app)
  RecyclerView rvInstallAppView;
  @BindView(R.id.progress)
  ProgressBar progressBar;
  @BindView(R.id.txt_nodata_view)
  TextView mNoDataView;
  AppAdapter rvInstallAppAdapter;
  List<AppInfo> rvListApp;
  View mRootView;
  // Load Settings
  private AppPreferences appPreferences;

  public static HiddenAppFragment newInstance() {

    Bundle args = new Bundle();

    HiddenAppFragment fragment = new HiddenAppFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mRootView = (View)inflater.inflate(R.layout.fragment_hidden_app,container,false);
    return mRootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    init();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this,view);
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
      Set<String> hiddenApps = appPreferences.getHiddenApps();
      // Hidden Apps
      for (String app : hiddenApps) {
        AppInfo tempApp = new AppInfo(app);
        Drawable tempAppIcon = UtilsApp.getIconFromCache(getContext(), tempApp);
        tempApp.setIcon(tempAppIcon);
        rvListApp.add(tempApp);
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      progressBar.setVisibility(View.GONE);
      rvInstallAppAdapter.notifyDataSetChanged();
      if(rvListApp.size()==0){
        mNoDataView.setVisibility(View.VISIBLE);
      }
    }


  }

  public RecyclerView getRecycleView(){
    return rvInstallAppView;
  }
}
