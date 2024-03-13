package com.app.taocalligraphy.database

import androidx.room.TypeConverter
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataConvertors {

    @TypeConverter
    fun fromStringToList(dataList: List<MeditationContentResponse.Chapter>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToList(data: String?): List<MeditationContentResponse.Chapter>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<List<MeditationContentResponse.Chapter>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromStringToCategoryDetailsList(dataList: List<MeditationContentResponse.CategoryDetails>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToCategoryDetailsList(data: String?): List<MeditationContentResponse.CategoryDetails>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<List<MeditationContentResponse.CategoryDetails>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromStringToSubtitleWithLanguageList(dataList: List<MeditationContentResponse.SubtitleWithLanguage>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToSubtitleWithLanguageList(data: String?): List<MeditationContentResponse.SubtitleWithLanguage>? {
        if (data == null) {
            return null
        }
        val type =
            object : TypeToken<List<MeditationContentResponse.SubtitleWithLanguage>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromStringToReviewsList(dataList: List<MeditationContentResponse.Reviews>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToReviewsList(data: String?): List<MeditationContentResponse.Reviews>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<List<MeditationContentResponse.Reviews>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromStringToHeartDetails(dataList: MeditationContentResponse.HeartDetails?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToHeartDetails(data: String?): MeditationContentResponse.HeartDetails? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<MeditationContentResponse.HeartDetails>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromStringToSubscription(dataList: MeditationContentResponse.Subscription?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToSubscription(data: String?): MeditationContentResponse.Subscription? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<MeditationContentResponse.Subscription>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromStringToRatingDetails(dataList: MeditationContentResponse.RatingDetails?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToRatingDetails(data: String?): MeditationContentResponse.RatingDetails? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<MeditationContentResponse.RatingDetails>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromFeedbackTagListToString(dataList: List<MeditationContentResponse.FeedbackTag>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToFeedbackTagList(data: String?): List<MeditationContentResponse.FeedbackTag>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<List<MeditationContentResponse.FeedbackTag>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromCurrencyListToString(dataList: List<MeditationContentResponse.Currencies>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToCurrencyList(data: String?): List<MeditationContentResponse.Currencies>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<List<MeditationContentResponse.Currencies>>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromTagListToString(dataList: ArrayList<String>?): String? {
        if (dataList == null)
            return null

        return Gson().toJson(dataList)
    }

    @TypeConverter
    fun toStringToTagList(data: String?): ArrayList<String>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(data, type)
    }
}