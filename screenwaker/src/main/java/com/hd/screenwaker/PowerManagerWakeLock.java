package com.hd.screenwaker;

import android.content.Context;
import android.os.PowerManager;

/**
 * 唤醒锁管理
 */
public class PowerManagerWakeLock {
  private static android.os.PowerManager.WakeLock wakeLock;

  /**
   * 获取唤醒锁
   * @param context
   */
  public static void acquire(Context context) {
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
            "PowerManagerWakeLock");
    wakeLock.acquire();
  }

  /**
   * 释放唤醒锁
   * @param context
   */
  public static void release() {
    if(wakeLock != null) {
      wakeLock.release();
      wakeLock = null;
    }
  }
}
