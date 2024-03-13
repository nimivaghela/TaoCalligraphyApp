/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.taocalligraphy.utils;

import android.app.Notification;

import androidx.annotation.Nullable;

import com.app.taocalligraphy.R;
import com.app.taocalligraphy.TaoCalligraphy;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

/** A service for downloading media. */
public class DownloadSongService extends DownloadService {

  private static final String CHANNEL_ID = "download_channel";
  private static final int JOB_ID = 1;
  private static final int FOREGROUND_NOTIFICATION_ID = 100;

  public static int nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1;

  public static DownloadNotificationHelper notificationHelper;

  public DownloadSongService() {
    super(FOREGROUND_NOTIFICATION_ID,DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,CHANNEL_ID, R.string.exo_download_notification_channel_name,/* channelDescriptionResourceId= */ 0);
    nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    notificationHelper = new DownloadNotificationHelper(this, CHANNEL_ID);
  }

  @Override
  protected DownloadManager getDownloadManager() {
    return ((TaoCalligraphy) getApplication()).getDownloadManager();
  }

  @Override
  protected PlatformScheduler getScheduler() {
    return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
  }

  @Override
  protected Notification getForegroundNotification(List<Download> downloads, @Requirements.RequirementFlags int notMetRequirements) {
    return notificationHelper.buildProgressNotification(getApplicationContext(),
        R.drawable.vd_download_icon, /* contentIntent= */ null, /* message= */ null, downloads, notMetRequirements);
  }

//  @Override
//  protected void onDownloadChanged(Download download, @Nullable Exception finalException) {
//    Notification notification;
//    if (download.state == Download.STATE_COMPLETED) {
//      notification =
//          notificationHelper.buildDownloadCompletedNotification(getApplicationContext(),
//              R.drawable.vd_download_complete,
//              /* contentIntent= */ null,
//              Util.fromUtf8Bytes(download.request.data));
//    } else if (download.state == Download.STATE_FAILED) {
//      notification =
//          notificationHelper.buildDownloadFailedNotification(getApplicationContext(),
//              R.drawable.vd_download_icon,
//              /* contentIntent= */ null,
//              Util.fromUtf8Bytes(download.request.data));
//    } else {
//      return;
//    }
//
//    NotificationUtil.setNotification(this, nextNotificationId++, notification);
//  }
}
