package com.app.taocalligraphy.ui

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentDataModel
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.ContentData
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.models.response.program.InProgressProgramListResponse
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.SearchContentDatamodel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.how_to_meditate.ReadMeditateActivity
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.utils.extensions.showUnlockImageDialog

class NavigationHandler {
    companion object{
        fun handleNavigation(dataModel: ProgramDataModel, activity : AppCompatActivity, startSubscriptionActivityForResult : ActivityResultLauncher<Intent>){
//            if ((dataModel?.subscription?.isAccessible ?: true) == true && dataModel?.isPaidContent == true && dataModel?.isPurchased == false) {
//                    GET
//                showUnlockImageDialog(activity)

//                if (dataModel.type == Constants.content) {
//                    if (dataModel.contentType == Constants.TEXT) {
//                        ReadMeditateActivity.startActivity(
//                            activity, dataModel.id ?: ""
//                        )
//                    } else {
//                        MeditationDetailActivity.startActivity(
//                            activity, dataModel.id ?: ""
//                        )
//                    }
//                } else {
//                    activity?.let {
//                        ProgramDetailsActivity.startActivity(
//                            it,
//                            dataModel.id ?: "",
//                            false
//                        )
//                    }
//                }
//                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
//            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
////                    lock
//                if (dataModel.unlockDays != null) {
//                    if (dataModel.unlockDays!! < 1)
//                        showUnlockImageDialog(activity)
//                } else if (dataModel.unlockDays == null) {
//                    showUnlockImageDialog(activity)
//                }
//            } else if ((dataModel?.subscription?.isAccessible ?: true) == false) {
////                        Subscribe
//                SubscriptionActivity.startActivityForResult(
//                    activity as AppCompatActivity,
//                    startSubscriptionActivityForResult
//                )
//            } else {
//             can access
                if (dataModel.type == Constants.content) {
                    if (dataModel.contentType == Constants.TEXT) {
                        ReadMeditateActivity.startActivity(
                            activity as AppCompatActivity, dataModel.id ?: ""
                        )
                    } else {
                        MeditationDetailActivity.startActivity(
                            activity as AppCompatActivity, dataModel.id ?: ""
                        )
                    }
                } else {
                    activity?.let {
                        ProgramDetailsActivity.startActivity(
                            it,
                            dataModel.id ?: "",
                            false
                        )
                    }
                }
//            }
        }

        fun handleFavouriteNavigation(dataModel: FavoriteContentDataModel, activity : AppCompatActivity, startSubscriptionActivityForResult : ActivityResultLauncher<Intent>){
                MeditationDetailActivity.startActivity(activity, dataModel.id ?: "-1")
        }

        fun handleSearchNavigation(dataModel: SearchContentDatamodel, activity : AppCompatActivity, startSubscriptionActivityForResult : ActivityResultLauncher<Intent>){
                if (dataModel.type == Constants.TEXT) {
                    ReadMeditateActivity.startActivity(
                        activity,
                        dataModel.id
                    )
                } else {
                    MeditationDetailActivity.startActivity(
                        activity,
                        dataModel.id!!
                    )
                }
        }

        fun handleContentNavigation(dataModel: ContentData, activity : AppCompatActivity, startSubscriptionActivityForResult : ActivityResultLauncher<Intent>){
                MeditationDetailActivity.startActivity(
                    activity, dataModel.id.toString()
                )
        }


         fun handleProgramInProgressNavigation(dataModel: InProgressProgramListResponse.InProgressProgramsList.Program?,activity : AppCompatActivity, startSubscriptionActivityForResult : ActivityResultLauncher<Intent>) {
                 ProgramDetailsActivity.startActivity(activity, dataModel?.id ?: "-1", false)
        }


        fun handleHowToMeditateNavigation(dataModel: HowToMeditateDataModel, activity : AppCompatActivity, startSubscriptionActivityForResult : ActivityResultLauncher<Intent>){
                    MeditationDetailActivity.startActivity(
                        activity,
                        dataModel.contentId ?: "",
                        isFromMeditate = true
                    )
                }
        }


}