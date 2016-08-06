package com.hd.screenwaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.hd.screenwaker.appmodel.AppAdapter;
import com.hd.screenwaker.appmodel.AppInfo;
import com.hd.screenwaker.data.AppFetcher;
import com.hd.screenwaker.event.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowScreenActivity extends AppCompatActivity {

  private static final String TAG = "dghost";
  public static boolean isShowing = false;
  @Bind(R.id.app_list_rv)
  RecyclerView appListRv;
  @Bind(R.id.setteings)
  ImageView setteings;
  private AppFetcher fetcher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 全屏显示
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_show_screen);
    ButterKnife.bind(this);

    // app列表的展示
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    AppAdapter adapter = new AppAdapter(new ArrayList(), R.layout.layout_app_icon);
    appListRv.setLayoutManager(layoutManager);
    appListRv.setAdapter(adapter);

    // 列表点击事件添加
    appListRv.addOnItemTouchListener(new RecyclerItemClickListener(ShowScreenActivity.this,
            new RecyclerItemClickListener.OnItemClickListener() {
              @Override
              public void onItemClick(View view, int position) {
                AppInfo appInfo = fetcher.getAppInfos().get(position);

                // 启动app
                Intent intent = new Intent();
                //包名 包名+类名（全路径）
                intent.setClassName(appInfo.getPackName(), appInfo.getmActivityName());
                startActivity(intent);
              }
            }));

    // 显示app列表
    fetcher = new AppFetcher(this, adapter);

    // 设置
    setteings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(ShowScreenActivity.this, "setting", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    isShowing = true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    isShowing = false;
  }

}
