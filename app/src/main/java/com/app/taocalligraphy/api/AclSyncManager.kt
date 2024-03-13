package com.app.taocalligraphy.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.WorkerThread
import androidx.hilt.work.HiltWorker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.di.AppModule
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.UserModulePermission
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.longToast
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltWorker
class UpdateAclPermissionsWorkManager @AssistedInject constructor(@Assisted var context: Context,@Assisted workerParams: WorkerParameters, var apiService: ApiService) :
    Worker(context, workerParams) {

    companion object {
        fun startWorkManager() {
            val aclWorkManager = OneTimeWorkRequest.Builder(UpdateAclPermissionsWorkManager::class.java)
            WorkManager.getInstance(TaoCalligraphy.instance).enqueue(aclWorkManager.build())
        }
    }

    override fun doWork(): Result {

                    apiService.getAClModulePermissionApi()
                        .enqueue(object : Callback<CommonResponseModel<List<UserModulePermission>>> {
                            override fun onFailure(call: Call<CommonResponseModel<List<UserModulePermission>>>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<CommonResponseModel<List<UserModulePermission>>>,
                                response: Response<CommonResponseModel<List<UserModulePermission>>>
                            ) {
                                if(response.body()?.type == Constants.SUCCESS){
                                    val it = response.body()!!
                                    var enumSet = UserHolder.EnumUserModulePermission.values().toHashSet()
                                    it.data?.let {
                                        it.forEach { item ->
                                            if(enumSet.any { it.name.equals(item.moduleName ?: "") }) {
                                                UserHolder.EnumUserModulePermission.valueOf(item.moduleName ?: "").permission = item
                                            }
                                        }
                                    }
                                    TaoCalligraphy.instance.mUserHolder.canAccessDownload = UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess ?: false
                                    val intent = Intent(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
                                    LocalBroadcastManager.getInstance(TaoCalligraphy.instance).sendBroadcast(intent)
                                }else{

                                }
                            }
                        })

        return Result.success()
    }
}