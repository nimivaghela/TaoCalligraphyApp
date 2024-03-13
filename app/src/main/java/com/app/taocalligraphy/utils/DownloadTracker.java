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

import static com.app.taocalligraphy.utils.DownloadSongService.nextNotificationId;
import static com.app.taocalligraphy.utils.DownloadSongService.notificationHelper;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;

import com.app.taocalligraphy.R;
import com.app.taocalligraphy.TaoCalligraphy;
import com.app.taocalligraphy.base.BaseViewModel;
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadCursor;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadIndex;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

import kotlin.collections.CollectionsKt;

/**
 * Tracks media that has been downloaded.
 */
public class DownloadTracker {

    /**
     * Listens for changes in the tracked downloads.
     */
    public interface Listener {

        /**
         * Called when the tracked downloads changed.
         */
        void onDownloadsChanged();
    }

    private static final String TAG = "DownloadTracker";

    private final Context context;
    private final DataSource.Factory dataSourceFactory;
    private final CopyOnWriteArraySet<Listener> listeners;
    private final HashMap<Uri, Download> downloads;
    private final DownloadIndex downloadIndex;
    private BaseViewModel viewModel;

    @Nullable
    private StartDownloadDialogHelper startDownloadDialogHelper;

    public DownloadTracker(
            Context context, DataSource.Factory dataSourceFactory, DownloadManager downloadManager) {
        this.context = context.getApplicationContext();
        this.dataSourceFactory = dataSourceFactory;
        listeners = new CopyOnWriteArraySet<>();
        downloads = new HashMap<>();
        downloadIndex = downloadManager.getDownloadIndex();
        downloadManager.addListener(new DownloadManagerListener());
        loadDownloads();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public boolean isDownloaded(Uri uri) {
        Download download = downloads.get(uri);
        return download != null && download.state == Download.STATE_COMPLETED;
    }

    @SuppressWarnings("unchecked")
    public DownloadRequest getDownloadRequest(Uri uri) {
        Download download = downloads.get(uri);
        return download != null && download.state != Download.STATE_FAILED ? download.request : null;
    }

    public void downloadFile(
            String name,
            Uri uri,
            String extension,
            RenderersFactory renderersFactory,
            BaseViewModel viewModel) {
        Download download = downloads.get(uri);
        this.viewModel = viewModel;
        if (download == null) {
            if (startDownloadDialogHelper != null) {
                startDownloadDialogHelper.release();
            }
            startDownloadDialogHelper =
                    new StartDownloadDialogHelper(getDownloadHelper(uri, extension, renderersFactory), name);
        }
    }

    public void removeDownload(Uri uri) {
        Download download = downloads.get(uri);
        if (download != null) {
            DownloadService.sendRemoveDownload(
                    context, DownloadSongService.class, download.request.id, /* foreground= */ false);

            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    TaoCalligraphy.instance.getDownloadCache().removeResource(download.request.id);
//                    CacheUtil.remove(TaoCalligraphy.instance.getDownloadCache(), download.request.id);
                }
            }.start();
        }
    }

    public void removeAllDownload() {
        DownloadService.sendRemoveAllDownloads(
                context, DownloadSongService.class,/* foreground= */ false);
        for (String keys : TaoCalligraphy.instance.getDownloadCache().getKeys()) {
//            CacheUtil.remove(TaoCalligraphy.instance.getDownloadCache(), keys);
            TaoCalligraphy.instance.getDownloadCache().removeResource(keys);
        }
    }

    private void loadDownloads() {
        try (DownloadCursor loadedDownloads = downloadIndex.getDownloads()) {
            while (loadedDownloads.moveToNext()) {
                Download download = loadedDownloads.getDownload();
                downloads.put(download.request.uri, download);
            }
        } catch (Exception e) {
        }
    }

    private DownloadHelper getDownloadHelper(
            Uri uri, String extension, RenderersFactory renderersFactory) {
        int type = Util.inferContentType(uri, extension);
        MediaItem mediaItem = new MediaItem.Builder().setUri(uri).build();
        switch (type) {
            case C.TYPE_DASH:
                return DownloadHelper.forMediaItem(mediaItem, DownloadHelper.getDefaultTrackSelectorParameters(context), renderersFactory, dataSourceFactory);
            case C.TYPE_SS:
                return DownloadHelper.forMediaItem(mediaItem, DownloadHelper.getDefaultTrackSelectorParameters(context), renderersFactory, dataSourceFactory);
            case C.TYPE_HLS:
                return DownloadHelper.forMediaItem(mediaItem, DownloadHelper.getDefaultTrackSelectorParameters(context), renderersFactory, dataSourceFactory);
            case C.TYPE_OTHER:
                return DownloadHelper.forMediaItem(context, mediaItem);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private class DownloadManagerListener implements DownloadManager.Listener {

        @Override
        public void onDownloadChanged(DownloadManager downloadManager, Download download, @Nullable Exception finalException) {
            downloads.put(download.request.uri, download);
            Notification notification;
            if (download.state == Download.STATE_COMPLETED) {
                notification =
                        notificationHelper.buildDownloadCompletedNotification(context,
                                R.drawable.vd_download_complete,
                                /* contentIntent= */ null,
                                Util.fromUtf8Bytes(download.request.data));
            } else if (download.state == Download.STATE_FAILED) {
                notification =
                        notificationHelper.buildDownloadFailedNotification(context,
                                R.drawable.vd_download_icon,
                                /* contentIntent= */ null,
                                Util.fromUtf8Bytes(download.request.data));
            } else {
                return;
            }

            NotificationUtil.setNotification(context, nextNotificationId++, notification);
            if ((download.getBytesDownloaded() == download.contentLength) && (download.state != Download.STATE_REMOVING)) {
//                int index = CollectionsKt.indexOfFirst(TaoCalligraphy.instance.getCurrentDownloadItemList(), s -> Objects.equals(s.getContentFile(), download.request.uri.toString()));
                int index = CollectionsKt.indexOfFirst(TaoCalligraphy.instance.getCurrentDownloadItemList(), s -> Objects.equals((s.getContentFileForDownload() != null && !s.getContentFileForDownload().isEmpty()) ? s.getContentFileForDownload() : s.getContentFile(), download.request.uri.toString()));
                if (index != -1) {
                    MeditationContentResponse meditationContentResponse = TaoCalligraphy.instance.getCurrentDownloadItemList().get(index);
                    viewModel.addMeditationToStorage(meditationContentResponse);
                    TaoCalligraphy.instance.getCurrentDownloadItemList().remove(index);
                }
            }
            for (Listener listener : listeners) {
                listener.onDownloadsChanged();
            }
        }

        @Override
        public void onDownloadRemoved(DownloadManager downloadManager, Download download) {
            downloads.remove(download.request.uri);
            for (Listener listener : listeners) {
                listener.onDownloadsChanged();
            }
        }
    }

    private final class StartDownloadDialogHelper
            implements DownloadHelper.Callback,
            DialogInterface.OnClickListener,
            DialogInterface.OnDismissListener {

        private final DownloadHelper downloadHelper;
        private final String name;


        public StartDownloadDialogHelper(
                DownloadHelper downloadHelper, String name) {
            this.downloadHelper = downloadHelper;
            this.name = name;
            downloadHelper.prepare(this);
        }

        public void release() {
            downloadHelper.release();
        }

        // DownloadHelper.Callback implementation.

        @Override
        public void onPrepared(DownloadHelper helper) {
            if (helper.getPeriodCount() == 0) {
                startDownload();
                downloadHelper.release();
                return;
            }
        }

        @Override
        public void onPrepareError(DownloadHelper helper, IOException e) {
        }

        // DialogInterface.OnClickListener implementation.

        @Override
        public void onClick(DialogInterface dialog, int which) {
            for (int periodIndex = 0; periodIndex < downloadHelper.getPeriodCount(); periodIndex++) {
                downloadHelper.clearTrackSelections(periodIndex);
            }
            DownloadRequest downloadRequest = buildDownloadRequest();

            if (downloadRequest.streamKeys.isEmpty()) {
                // All tracks were deselected in the dialog. Don't start the download.
                return;
            }
            startDownload(downloadRequest);
        }

        // DialogInterface.OnDismissListener implementation.

        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            downloadHelper.release();
        }

        // Internal methods.
        private void startDownload() {
            startDownload(buildDownloadRequest());
        }

        private void startDownload(DownloadRequest downloadRequest) {
            DownloadService.sendAddDownload(
                    context, DownloadSongService.class, downloadRequest,/* foreground= */
                    false);
        }

        private DownloadRequest buildDownloadRequest() {
            return downloadHelper.getDownloadRequest(Util.getUtf8Bytes(name));
        }
    }
}
