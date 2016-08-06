package com.hd.screenwaker.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;

import com.hd.screenwaker.appmodel.AppAdapter;
import com.hd.screenwaker.appmodel.AppInfo;
import com.hd.screenwaker.appmodel.AppInfoRich;
import com.hd.screenwaker.appmodel.AppList;
import com.hd.screenwaker.util.AppUtils;
import com.hd.screenwaker.util.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 异步获取app列表
 */
public class AppFetcher {
  private static final String TAG = "dghost";
  private Context context;
  private File fileDir;
  private AppAdapter adapter;

  public List<AppInfo> getAppInfos() {
    return appInfos;
  }

  private List<AppInfo> appInfos;

  public AppFetcher(Context context, AppAdapter adapter) {
    this.context = context;
    this.adapter = adapter;
    getFilesDir(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<File>() {
              @Override
              public void onCompleted() {
              }

              @Override
              public void onError(Throwable e) {
              }

              @Override
              public void onNext(File file) {
                fileDir = file;
                Log.d(TAG, "file dir -> " + fileDir.getAbsolutePath());
                refreshList();
              }
            });
  }

  public static Observable<File> getFilesDir(Context context) {
    return Observable.create(subscriber -> {
      subscriber.onNext(context.getFilesDir());
      subscriber.onCompleted();
    });
  }

  /**
   * 刷新app应用列表
   */
  public void refreshList() {
    getAppList().subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<AppList>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
              e.printStackTrace();
            }

            @Override
            public void onNext(AppList appList) {
              // TODO update data in adapter
              List<AppInfo> list = appList.getList();
              Log.d(TAG, "app list -> " + list);
              adapter.addApps(list);
            }
          });
  }

  /**
   * 第一次获取app列表，之后的应用列表都会存在内存或者数据库中
   * 数据库更新与同步问题
   *
   * @return
   */
  public Observable<AppList> getAppList() {
    return Observable.create(subscriber -> {
      List<AppInfoRich> appList = new ArrayList<>();
      appInfos = new ArrayList<>();

      // get all app infos
      final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
      List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(mainIntent, 0);

      // store infos to richinfos
      for (ResolveInfo info : resolveInfos) {
        appList.add(new AppInfoRich(context, info));
        Log.d(TAG, "resolve info -> " + info);
        Log.d(TAG, "activity info -> " + info.activityInfo.packageName + "/" + info.activityInfo.name);
      }

      Log.d(TAG, "resolved app info -> " + resolveInfos);

      // 分别将当前app列表保存在内存，并且做压缩缓存
      for (AppInfoRich richInfo : appList) {
        Bitmap icon = BitmapUtils.drawableToBitmap(richInfo.getIcon());
        String name = richInfo.getName();
        String iconPath = fileDir + File.separator + name;
        String packName = richInfo.getPackageName();
        String actiName = richInfo.getActivityName();
        BitmapUtils.storeBitmap(context, icon, name); // 缓存，这里可以考虑借鉴ReactiveCache

        AppInfo appInfo = new AppInfo(richInfo.getLastUpdateTime(), name, iconPath, packName, actiName);
        appInfos.add(appInfo);

        if (subscriber.isUnsubscribed()) {
          return;
        }

      }

      // 将app列表保存在全局上下当中
      AppList.getInstance().setList(appInfos);
      subscriber.onNext(AppList.getInstance());

      // remember to completed
      if (!subscriber.isUnsubscribed()) {
        subscriber.onCompleted();
      }
    });
  }
}
