package com.app.taocalligraphy

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.WindowManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.database.MeditationDbRepo
import com.app.taocalligraphy.database.TaoCalligraphyDatabase
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.SaveUserDetail
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.DownloadTracker
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.logError
import com.facebook.appevents.AppEventsLogger
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DefaultDownloadIndex
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject


@HiltAndroidApp
open class TaoCalligraphy : Application(), Configuration.Provider {

    private var downloadCache: Cache? = null
    private var downloadDirectory: File? = null
    private var databaseProvider: DatabaseProvider? = null
    private var downloadManager: DownloadManager? = null
    private var downloadTracker: DownloadTracker? = null
    private var mediaSourceFactory: MediaSource.Factory? = null
    var trackSelector: DefaultTrackSelector? = null

    private val DOWNLOAD_ACTION_FILE = "actions"
    private val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    private var userAgent: String = ""
    private val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    var isHomeFragmentVisible: Boolean = false
    var isRedirectToResetPassword: Boolean = false
    var resetPasswordToken: String = ""
    var isPlayerPlaying = true
    var isRedirectToReferralCode: Boolean = false
    var referralCode: String = ""
    var subtitleWithLanguage: MeditationContentResponse.SubtitleWithLanguage? = null
    var playerPlaybackSpeed = 1f

    var simpleExoPlayer: ExoPlayer? = null

    var notificationMediaId: String = ""
    var notificationTitle: String = ""
    var notificationImagePath: String = ""
    var notificationDuration: Long = 0
    var notificationImage: Bitmap? = null

    @Inject
    lateinit var mUserHolder: UserHolder

    @Inject
    lateinit var mUserDetail: SaveUserDetail

    var isSubscriptionStatusApiCalled = false

    @Inject
    lateinit var apiHelperBase: ApiHelper

    @Inject
    lateinit var database: TaoCalligraphyDatabase

    val currentDownloadItemList = ArrayList<MeditationContentResponse?>()
    val meditationDbRepo by lazy { MeditationDbRepo(database.meditationContentDao()) }
    var isAppInBackground = false
    var activeActivity: Activity? = null

    companion object {
        lateinit var instance: TaoCalligraphy

        fun getAppContext(): TaoCalligraphy {
            return instance
        }
    }

    override fun onCreate() {
        super<Application>.onCreate()
        AppEventsLogger.activateApp(this)
        instance = this
        userAgent = Util.getUserAgent(this, packageName)
        FirebaseApp.initializeApp(applicationContext)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        initInternetMonitor()
        setupActivityListener()
    }

    fun initializeExoPlayer() {
        val attr = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()
        mediaSourceFactory = DefaultMediaSourceFactory(buildDataSourceFactory())
        trackSelector = DefaultTrackSelector(applicationContext)
        simpleExoPlayer =
            ExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory as DefaultMediaSourceFactory)
                .setTrackSelector(trackSelector!!)
                .setSeekBackIncrementMs(15000L)
                .setSeekForwardIncrementMs(15000L)
                .setAudioAttributes(attr, true).build()
    }

    @Synchronized
    fun getDownloadCache(): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory = File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache =
                SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider())
        }
        return downloadCache as Cache
    }

    private fun getDownloadDirectory(): File? {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null)
            /*if (downloadDirectory == null) {
                downloadDirectory = filesDir
            }*/
        }
        return downloadDirectory
    }

    private fun getDatabaseProvider(): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = ExoDatabaseProvider(this)
        }
        return databaseProvider as DatabaseProvider
    }

    /** Returns a [DataSource.Factory].  */
    fun buildDataSourceFactory(): DataSource.Factory {
        val upstreamFactory = DefaultDataSource.Factory(this, buildHttpDataSourceFactory())
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache())
    }

    private fun buildHttpDataSourceFactory(): HttpDataSource.Factory {
        return DefaultHttpDataSource.Factory().setUserAgent(userAgent)
    }

    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory,
        cache: Cache
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            .setEventListener(null)
    }

    fun buildRenderersFactory(): RenderersFactory {
//        @DefaultRenderersFactory.ExtensionRendererMode
        val extensionRendererMode =
            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
        return DefaultRenderersFactory(/* context= */this)
            .setExtensionRendererMode(extensionRendererMode)
    }

    fun getDownloadManager(): DownloadManager? {
        initDownloadManager()
        return downloadManager
    }

    fun getDownloadTracker(): DownloadTracker? {
        initDownloadManager()
        return downloadTracker
    }

    @Synchronized
    private fun initDownloadManager() {
        if (downloadManager == null) {
            try {
                val downloadIndex = DefaultDownloadIndex(getDatabaseProvider())
                upgradeActionFile(
                    DOWNLOAD_ACTION_FILE, downloadIndex, /* addNewDownloadsAsCompleted= */ false
                )
                upgradeActionFile(
                    DOWNLOAD_TRACKER_ACTION_FILE,
                    downloadIndex, /* addNewDownloadsAsCompleted= */
                    true
                )
                val downloadExecutor = Executor { obj: Runnable -> obj.run() }
                downloadManager = DownloadManager(
                    this,
                    getDatabaseProvider(),
                    getDownloadCache(),
                    buildHttpDataSourceFactory(),
                    downloadExecutor
                )

//                val downloaderConstructorHelper =
//                    DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory())
//                downloadManager = DownloadManager(
//                    this, downloadIndex, DefaultDownloaderFactory(downloaderConstructorHelper)
//                )
                downloadTracker =
                    DownloadTracker(/* context= */this, buildDataSourceFactory(), downloadManager)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun upgradeActionFile(
        fileName: String,
        downloadIndex: DefaultDownloadIndex,
        addNewDownloadsAsCompleted: Boolean
    ) {
//        try {
//            ActionFileUpgradeUtil.upgradeAndDelete(
//                File(getDownloadDirectory(), fileName), /* downloadIdProvider= */
//                null,
//                downloadIndex, /* deleteOnFailure= */
//                true,
//                addNewDownloadsAsCompleted
//            )
//        } catch (e: IOException) {
//        }
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }


    private fun initInternetMonitor() {
        ReactiveNetwork
            .observeInternetConnectivity(internetObservingSettings)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Boolean?> {
                override fun onNext(isConnected: Boolean) {
                    if (isConnected) {
                        EventBus.getDefault().post(IsNetworkAvailableListener(true))
                    } else {
                        EventBus.getDefault().post(IsNetworkAvailableListener(false))
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }
            })
    }

    private val internetObservingSettings = InternetObservingSettings.builder()
        .interval(5000)
        .build()

    private fun setupActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (!BuildConfig.DEBUG) {
                    activity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                }
                activity.requestedOrientation = if (isTablet(activity)) {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            override fun onActivityStarted(activity: Activity) {
                isAppInBackground = false
                logError(msg = "LifeCycle onActivityStarted $isAppInBackground")
                (activity.application as? TaoCalligraphy)?.activeActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                if (activeActivity == activity) {
                    isAppInBackground = true
                    logError(msg = "LifeCycle onActivityStopped $isAppInBackground")
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}

var ExoPlayer.playbackSpeed: Float
    get() = playbackParameters.speed ?: 1f
    set(speed) {
        val pitch = playbackParameters.pitch ?: 1f
        playbackParameters = PlaybackParameters(speed, pitch)
    }

val userHolder: UserHolder by lazy {
    return@lazy TaoCalligraphy.instance.mUserHolder
}

val savedUserDetail: SaveUserDetail by lazy {
    return@lazy TaoCalligraphy.instance.mUserDetail
}
