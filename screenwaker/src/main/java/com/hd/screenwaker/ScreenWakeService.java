package com.hd.screenwaker;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenWakeService extends Service {
  private boolean isWakeLockOpen = false;
    public static final String TAG = "dghost";

  private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      if(!isWakeLockOpen) {
        // acquire wake lock
        PowerManagerWakeLock.acquire(ScreenWakeService.this);
        isWakeLockOpen = true;
      }

      if(!ShowScreenActivity.isShowing && Intent.ACTION_SCREEN_ON.equals(action)) {
        // TODO open screen light
        // 调到屏保页面
        Log.d(TAG, "start screen wake service");
        Intent screenIntent = new Intent(ScreenWakeService.this, ShowScreenActivity.class);
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(screenIntent);

      }
      else if(Intent.ACTION_SCREEN_OFF.equals(action)) {
        // TODO 服务被杀死之后，这里并不会执行到
        // check if screen wake service is closed
        if(!isServiceRunning(ScreenWakeService.class)) {
          Log.d(TAG, "screen wake service is closed");
//          isWakeLockOpen = false;
        }
      }
    }
  };

  private boolean isServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  public ScreenWakeService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    // 关闭默认的屏保
    KeyguardManager manager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    KeyguardManager.KeyguardLock keyguardLock = manager.newKeyguardLock("KeyguardLock");
    keyguardLock.disableKeyguard(); // 这里需要权限的

    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    registerReceiver(screenReceiver, filter);

    // TODO keep service alive for a very long time
    // 这里直接跳转到手机解锁页面然后是程序管理页面
    //    startForeground(Notification.FLAG_ONGOING_EVENT, notification);

    // prepare intent which is triggered if the
    // notification is selected

    // 使用这种方法导致打开屏幕的时候自动跳回密码输入页面
    // 直接忽略密码输入页面真的好吗
/*
    Intent intent = new Intent(this, ShowScreenActivity.class);
    // use System.currentTimeMillis() to have a unique ID for the pending intent
    PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

    // build notification
    // the addAction re-use the same intent to keep the example short
    Notification n  = new Notification.Builder(this)
            .setContentTitle("DGhost Future")
            .setContentText("Thinking Makes You Strong")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(pIntent)
            .build();


    NotificationManager notificationManager =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    notificationManager.notify(0, n);*/

  }

  /**
   * 释放资源
   */
  public void onDestroy() {
    PowerManagerWakeLock.release();
    unregisterReceiver(screenReceiver);
  }
}
