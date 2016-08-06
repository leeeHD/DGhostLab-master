package com.hd.screenwaker.util;

import android.content.Context;

import java.io.File;

import rx.Observable;

/**
 * Created by hd on 2016/8/6 0006.
 */
public class AppUtils {
  public static Observable<File> getFilesDir(Context context) {
    return Observable.create(subscriber -> {
      subscriber.onNext(context.getFilesDir());
      subscriber.onCompleted();
    });
  }
}
