package com.app.taocalligraphy.other

object Constants {

    val appName: String? = "Oneness Heart"

    //IOS dynamic links param
    const val IOS_BUNDLE_ID = "com.app.taocalligraphy"
    const val IOS_APPLE_ID = "1559219504"
    const val IOS_MINIMUM_VERSION = "1.0"
    const val isFromProfilePreview = "isFromProfilePreview"

    const val CUSTOM_ERROR = "CustomError"
    const val NETWORK_ERROR = "InternetError"

    //Search API used constants
    const val latest = "latest"
    const val A_Z = "A-Z"
    const val desc = "desc"
    const val asc = "asc"

    //Fetch notification API used constants
    const val all = "All"
    const val programs = "Programs"
    const val subscription = "Subscription"
    const val meditation = "Meditation"
    const val dailyWisdom = "DailyWisdom"

    const val challenges = "Challenges"
    const val live_sessions = "Live Sessions"
    const val meditations = "Meditations"
    const val light_transmissions = "Light Transmissions"

//     Stats constant

    const val day = "Day"
    const val week = "Week"
    const val month = "Month"

    //Error Type Constant
    const val SUCCESS = "SUCCESS"
    const val WARNING = "WARNING"
    const val INFO = "INFO"
    const val ERROR = "ERROR"

    const val content = "content"


    const val PREF_NAME = "pref_tao"
    const val PREF_DOWNLOAD = "pref_tao_download"
    const val API_VERSION = "v1"
    const val isHost = "isHost"
    const val alpha = "alpha"
    const val isCreateNewSession = "isCreateNewSession"
    const val selectedUser = "selectedUser"
    const val videoType = "videoType"
    const val meditationVideo = "meditationVideo"
    const val howToMeditateVideo = "howToMeditateVideo"
    const val playlistVideo = "playlistVideo"
    const val isEdit = "isEdit"
    const val imageUri = "imageUri"
    const val DEVICE_TYPE = "ANDROID"
    const val OS_TYPE = "Android"
    const val BROWSER_TYPE = "Android App"
    const val AMBIENT_SOUND = "AMBIENT_SOUND"
    const val START_SOUND = "START_SOUND"
    const val END_SOUND = "END_SOUND"
    const val MeditationContent = "meditationContent"
    const val subtitle = "subtitle"
    const val VIDEO = "Video"
    const val MUSIC = "Music"
    const val GUIDED_AUDIO = "Guided Audio"
    const val AUDIO = "Audio"
    const val Guided_Meditation_Audio = "Guided_Meditation_Audio"
    const val GUIDED = "Guided"
    const val TEXT = "TEXT"
    const val inProgress = "In Progress"
    const val forYou = "For You"
    const val howToMeditate = "HowToMeditate"
    const val completed = "Completed"
    const val newRelease = "New Release"
    const val alarmContent = "alarmContent"
    const val FROM = "from"
    const val ProgramContent = "programContent"
    const val ProgramJoined = "programJoined"
    const val ProgramFromHistory = "programHistory"
    const val UserExperience = "userExperience"

    const val FROM_PLAYER_NOTIFICATION = "from_player_notification"
    const val FROM_ALARM_NOTIFICATION = "from_alar_notification"

    const val alarmContentId = "alarmContentId"
    const val alarmContentTitle = "alarmContentTitle"
    const val alarmContentImageUrl = "alarmContentImageUrl"
    const val alarmContentFileUrl = "alarmContentFileUrl"
    const val alarmRepeat = "alarmRepeat"
    const val alarmRequestCode = "alarmRequestCode"

    const val snoozeAlarm = "snoozeAlarm"
    const val startMeditation = "startMeditation"
    const val cancelAlarm = "cancelAlarm"

    const val notificationPlayerContentUrl = "notificationPlayerContentUrl"
    const val notificationPlayerContentImage = "notificationPlayerContentImage"
    const val notificationPlayerContentTitle = "notificationPlayerContentTitle"
    const val notificationPlayerIsFromPlayList = "notificationPlayerIsFromPlayList"

    var IS_PROFILE_PICTURE_UPDATE = false
    var profileUpdatedMessage = ""

    var NOTIFICATION_ON_OFF = true
    var IS_VIDEO_SHOW = false
    var cropImageURl = ""
    var like = "like"
    var dislike = "dislike"
    var ratings = "ratings"
    var watch = "Watch"
    var read = "Read"

    var ITEM_PROGRESS = 0
    var ITEM_DATA = 1

    var isPlaylistChangedShowSnackBar = false
    var playListChangeSuccessMessage = ""

    var isResetPasswordShowSnackBar = false
    var resetPasswordSuccessMessage = ""

    //deepLinkingConstant
    var DEEP_LINK_PATH_PROFILE = "/profile"
    var QUERY_PARAM_USERID = "userId"
    var DEEP_LINK_PATH_MEDITATION_CONTENT = "/meditation-content"
    var DEEP_LINK_PATH_READ_MEDITATION_CONTENT = "/meditation-read"
    var DEEP_LINK_PATH_WATCH_MEDITATION_CONTENT = "/resources-content"
    var QUERY_PARAM_CONTENTID = "contentId"
    var DEEP_LINK_PATH_FORGOT_PASSWORD = "/forgot-password"
    var DEEP_LINK_PATH_REFERRAL_CODE = "/signup"
    var DEEP_LINK_PATH_PROGRAM = "/program-details"
    var QUERY_PARAM_PROGRAM_ID = "programId"
    var DEEP_LINK_PATH_MEDITATION_TIMER = "/meditation-timer"
    var QUERY_PARAM_MEDITATION_TIMER_ID = "meditationId"
    var DEEP_LINK_PATH_DAILY_WISDOM = "/home"
    var QUERY_PARAM_DAILY_WISDOM_ID = "id"
    var QUERY_PARAM_RESET_PASSWORD_VERIFICATION = "id"
    var QUERY_PARAM_REFERRAL_CODE = "referralCode"

    //    https://vimeo.com/611903407
//    https://vimeo.com/611903407/72da3d792a
//https://player.vimeo.com/video/358296408/config
//    const val vimeoVideoLink = "https://player.vimeo.com/video/358296408"
    const val vimeoVideoLink = "https://vimeo.com/611903407/72da3d792a"

    const val INTENT_EMAIL = "intentEmail"
    const val INTENT_MESSAGE = "intentMsg"

    val referralLink: String = "ReferralLink"

    //Constant for show selected screen of Dashboard when rotate tablet
    const val home = "Home"
    const val search = "Search"
    const val wellnessDialog = "WellnessDialog"
    const val notification = "Notification"
    const val wellness = "Wellness"
    const val about = "About"
    const val review = "Review"

    object Param {
        val isFromSignup: String? = "isFromSignup"

        //    Meditation Timer Constants
        const val isForEdit = "isForEdit"
        const val meditationList = "meditationList"


        const val params = "params"
        const val position = "position"

        //Auth
        const val firstName = "firstName"
        const val lastName = "lastName"
        const val email = "email"
        const val password = "password"
        const val verificationToken = "verificationToken"
        const val signupType = "signupType"
        const val socialId = "socialId"
        const val deviceType = "deviceType"
        const val deviceToken = "deviceToken"
        const val emailId = "emailId"
        const val sessionId = "sessionId"
        const val otp = "otp"
        const val soundType = "soundType"
        const val id = "id"
        const val meditationTimerId = "meditationTimerId"
        const val languageId = "languageId"
        const val regionId = "regionId"
        const val programContentId = "programContentId"
        const val contentId = "contentId"
        const val contentTitle = "contentTitle"
        const val contentDesc = "contentDesc"
        const val ratingNumber = "ratingNumber"
        const val playTime = "playTime"
        const val currentPassword = "currentPassword"
        const val newPassword = "newPassword"
        const val programType = "programType"
        const val listType = "listType"
        const val selectedType = "selectedType"
        const val type = "type"
        const val subCategory = "subCategory"
        const val categoryId = "categoryId"
        const val notificationId = "notificationId"
        const val programId = "programId"
        const val programName = "programName"
        const val isFromQuestionnaires = "isFromQuestionnaires"
        const val isFromHistoryCompletedProgram = "isFromHistoryCompletedProgram"
        const val dayNo = "dayNo"
        const val isRedirectedFromHistory = "isRedirectedFromHistory"
        const val date = "date"
        const val days = "days"
        const val meditationTime = "meditationTime"
        const val totalMeditationTime = "totalMeditationTime"
        const val isInitialUser = "isInitialUser"
        const val isSubtitleVisible = "isSubtitleVisible"
        const val videoPosition = "videoPosition"
        const val isFromNotification = "isFromNotification"
        const val isFromDownload = "isFromDownload"
        const val isFromProgram = "isFromProgram"
        const val redirectToSearch = "redirectToSearch"
        const val isFromMeditate = "isFromMeditate"
        const val alarmRequestCode = "alarmRequestCode"
        const val subscribeErrorMsg = "subscribeErrorMsg"

        //        Notification Params
        const val title = "title"
        const val message = "message"
        const val userId = "userId"
        const val notificationType = "notificationType"
        const val programImage = "programImage"

        //        How to meditate request
        const val current_page = "current_page"
        const val per_page = "per_page"
        const val search = "search"
        const val sort_field = "sort[field]"
        const val sort_order = "sort[order]"
    }

    object Subscription {
        const val FREE_TRIAL = "free-trial"
        const val SUBSCRIBE = "subscribe"
        const val SUBSCRIBE_MONTHLY = "subscribe-monthly"
        const val SUBSCRIBE_YEARLY = "subscribe-yearly"
        const val LEVEL_0 = "Level 0"
        const val LEVEL_1 = "Trial User"
    }

    val cancelDate: String = "cancelDate"
    val isCancel: String = "isCancel"
    val PLAYSTORE: String = "PLAY_STORE"

    object StatusCode {
        const val STATUS_1 = 1
        const val STATUS_0 = 0
        const val STATUS_404 = 404
        const val STATUS_401 = 401
        const val STATUS_412 = 412
        const val STATUS_403 = 403
        const val STATUS_500 = 500
        const val STATUS_422 = 422
        const val STATUS_409 = 409
        const val STATUS_426 = 426
    }

    object StaticContent {
        const val PRIVACY_POLICY = "privacy-policy"
        const val TERMS_CONDITIONS = "terms-and-conditions"
        const val COMMUNITY_GUIDELINES = "community-guidelines"
    }


    object BroadcastIntentFilter {
        const val BR_FAVOURITES_CHANGED = "br_favourites_changed"
        const val BR_SUBSCRIPTION_CHANGED = "br_subscription_changed"
        const val BR_ACCESS_LEVEL_CHANGED = "br_access_level_changed"
        const val CONTENT_ID = "content_id"
    }


    object SubTitles {
        const val SUBTITLE_VTT = "vtt"
    }
}

enum class MeditationType {
    CHOOSE_CATEGORY, CHOOSE_TOPIC, CHOOSE_TAGS
}