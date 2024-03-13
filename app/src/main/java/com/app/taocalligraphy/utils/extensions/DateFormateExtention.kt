package com.app.taocalligraphy.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import com.app.taocalligraphy.R
import com.app.taocalligraphy.userHolder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

var dateFormatter_utc_HH_mm_ss =
    SimpleDateFormat(
        "HH:mm:ss",
        Locale.getDefault()
    )
var dateFormatter_yyyy_mm_dd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

var dateFormatter_local_h_mm_aa = SimpleDateFormat("h:mm a", Locale.getDefault())
var dateFormatter_local_h_mm = SimpleDateFormat("H:mm", Locale.getDefault())
var dateFormatter_local_hh_mm_aa = SimpleDateFormat("hh:mm a", Locale.getDefault())
var dateFormatter_local_hh_mm = SimpleDateFormat("HH:mm", Locale.getDefault())
var dateFormatter_local_hh_mm_ss = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
var dateFormatter_YYYY_MM_dd_HH_mm_ss = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

val formatterDateStats: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd") //"dd/MM/yyyy"
val formatterDayStats: DateTimeFormatter = DateTimeFormat.forPattern("EEE") //"dd/MM/yyyy"

@SuppressLint("SimpleDateFormat")
fun getMonthFromDate(date: String?): String {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    val format2: DateFormat = SimpleDateFormat("MMM")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun getMonthDayFromDate(date: String?): String {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    val format2: DateFormat = SimpleDateFormat("MMM dd")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun getDateFromYYYYMMDD(date: String?): Date {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    return date?.let { format1.parse(it) } as Date
}

@SuppressLint("SimpleDateFormat")
fun formatDate(date: String?): String {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    val format2: DateFormat = SimpleDateFormat("MMM dd, yyy")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun formatDateYYYYMMMDD(dt1: Date?): String {
    val format2: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun formatDate(dt1: Date?): String {
    val format2: DateFormat = SimpleDateFormat("MMM dd, yyy")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun getDayFromDate(date: String?): String {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    val format2: DateFormat = SimpleDateFormat("dd")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun getYearFromDate(date: String?): String {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    val format2: DateFormat = SimpleDateFormat("yyyy")
    return format2.format(dt1)
}

@SuppressLint("SimpleDateFormat")
fun getDateFromString(date: String?): Date {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    return dt1
}

@SuppressLint("SimpleDateFormat")
fun getDateTimeFromTimeStamp(s: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val netDate = Date(s)
    return sdf.format(netDate)
}

@SuppressLint("SimpleDateFormat")
fun getFormattedDate(date: String?): String {
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val dt1: Date = date?.let { format1.parse(it) } as Date
    val format2: DateFormat = SimpleDateFormat("MMM dd yyyy")
    return format2.format(dt1)
}


fun getTimeStampFromTime(date: String?): Long {
    if (date != null) {
        val localTime = org.joda.time.LocalTime.parse(date)
        return localTime.millisOfDay.toLong()
    }
    return 0L
}

fun getCurrentDate(): Date {
    val date = dateFormatter_YYYY_MM_dd_HH_mm_ss.format(Calendar.getInstance().timeInMillis)
    val cal = Calendar.getInstance()
    cal.timeInMillis = dateFormatter_YYYY_MM_dd_HH_mm_ss.parse(date)?.time ?: Date().time
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    return dateFormatter_YYYY_MM_dd_HH_mm_ss.parse(dateFormatter_YYYY_MM_dd_HH_mm_ss.format(cal.time))
        ?: Date()
}

fun getCurrentDateUtc(): Date {
    val timeZone = TimeZone.getTimeZone("UTC")
    val date = dateFormatter_YYYY_MM_dd_HH_mm_ss.format(Calendar.getInstance(timeZone).timeInMillis)
    val cal = Calendar.getInstance()
    cal.timeInMillis = dateFormatter_YYYY_MM_dd_HH_mm_ss.parse(date)?.time ?: Date().time
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    return dateFormatter_YYYY_MM_dd_HH_mm_ss.parse(dateFormatter_YYYY_MM_dd_HH_mm_ss.format(cal.time))
        ?: Date()
}

fun getSecondsFromTime(date: String?): String {
    var time = ""

    try {
        if (date != null) {
            val localTime = org.joda.time.LocalTime.parse(date)
            val seconds = localTime.secondOfMinute.toLong()
            val minutes = localTime.minuteOfHour.toLong()
            val hours = localTime.hourOfDay.toLong()

            if (hours < 24 && hours != 0L) {
                time = if (hours.toInt() == 1) {
                    "$hours hour"
                } else {
                    "$hours hours"
                }
            } else if (minutes < 60 && minutes != 0L) {
                time = "$minutes mins"
            } else if (seconds < 60 && seconds != 0L) {
                time = "$seconds seconds"
            }
            time.ifEmpty { time = "0 min" }
            return time
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "0 min"
}

fun String?.getContentTime(): String {
    var time = ""

    this?.let {
        val localTime = org.joda.time.LocalTime.parse(it)
        val seconds = localTime.secondOfMinute.toLong()
        val minutes = localTime.minuteOfHour.toLong()
        val hours = localTime.hourOfDay.toLong()

        if (hours < 24 && hours != 0L) {
            time =
                if (hours.toInt() == 1) "$hours hour"
                else "$hours hours"
        }
        if (minutes < 60 && minutes != 0L) {
            time = time.plus(
                if (minutes.toInt() == 1) " $minutes min"
                else " $minutes mins"
            )
        }
        if (seconds < 60 && seconds != 0L) {
            time = time.plus(" $seconds s")
        }
        time.ifEmpty { time = "0 min" }
        return time
    }
    return "0 min"
}

fun formatTimeInHHmmss(date: String?): String {
    var time = ""

    if (date != null) {
        try {
            val localTime = org.joda.time.LocalTime.parse(date)
            val minutes = localTime.minuteOfHour.toLong()
            val hours = localTime.hourOfDay.toLong()

            if (hours < 24 && hours != 0L) {
                time = hours.toString() + "h "
            }
            if (minutes < 60 && minutes != 0L) {
                time = time + minutes.toString() + "m "
            }
        } catch (e: Exception) {
        }

        return time
    }
    return ""
}

fun getddMMMFromDate(date: String?): String {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat: DateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val formattedDate: Date? = inputFormat.parse(date.toString())
    return outputFormat.format(formattedDate!!)
}

fun getddMMMYYYYFromDate(date: String?): String {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate: Date? = inputFormat.parse(date.toString())
    return outputFormat.format(formattedDate!!)
}

fun getMinutesFromHHmm(date: String?): Float {
    if (date != null) {
        val localTime = org.joda.time.LocalTime.parse(date)

        val minutes = localTime.minuteOfHour.toLong()
        val hours = localTime.hourOfDay.toLong()

        val time = (hours * 60) + minutes
        return time.toFloat()
    }
    return 0f
}

fun Context.getStatsTime(date: String?): String {
    if (date != null) {
        val localTime = org.joda.time.LocalTime.parse(date)

        val minutes = localTime.minuteOfHour.toLong()
        val hours = localTime.hourOfDay.toLong()

        return if (hours.toInt() == 0) this.getString(
            R.string.daily_meditation_time_only_minute,
            minutes.toString()
        ) else
            this.getString(
                R.string.daily_meditation_time_with_hour,
                hours.toString(),
                minutes.toString()
            )
    }
    return this.getString(R.string.daily_meditation_time_only_minute, "0")
}

fun getMinutesFromTime(date: String?): Long {
    if (!date.isNullOrEmpty()) {
        val localTime = org.joda.time.LocalTime.parse(date)
        return localTime.minuteOfHour.toLong()
    }
    return 0L
}

fun getHoursFromTime(date: String?): Float {
    if (!date.isNullOrEmpty()) {
        val localTime = org.joda.time.LocalTime.parse(date)
        return localTime.hourOfDay.toFloat()
    }
    return 0f
}

fun getCurrentDateWithFormatyyyyMMddFormat(): String {
    val c: Date = Calendar.getInstance().time
    println("Current time => $c")

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return simpleDateFormat.format(c)
}

fun String?.isValidDate(): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    this?.let {
        val endDate = sdf.parse(this)
        return Date().before(endDate)
    } ?: kotlin.run {
        return false
    }
}

fun String.calculateDuration(): String {
    val duration = this.split(":")
    val hourOfDay = duration[0].toInt()
    val minutesOfDay = duration[1].toInt()
//    var secondsOfDay = 0
    if (duration.size > 2) {
//        secondsOfDay = duration[2].toInt()
        return "${
            if (hourOfDay > 0) {
                if (hourOfDay > 1) {
                    "$hourOfDay hrs "
                } else {
                    "$hourOfDay hr "
                }
            } else {
                ""
            }
        }${
            if (minutesOfDay > 0) {
                if (minutesOfDay > 1) {
                    "$minutesOfDay mins "
                } else {
                    "$minutesOfDay min "
                }
            } else {
                ""
            }
        }"
//        ${
//            if (secondsOfDay > 0) {
//                if (secondsOfDay > 1) {
//                    "$secondsOfDay secs"
//                } else {
//                    "$secondsOfDay sec"
//                }
//            } else {
//                ""
//            }
//        }
    } else {
        return "${
            if (hourOfDay > 0) {
                if (hourOfDay > 1) {
                    "$hourOfDay hrs "
                } else {
                    "$hourOfDay hr "
                }
            } else {
                ""
            }
        }${
            if (minutesOfDay > 0) {
                if (minutesOfDay > 1) {
                    "$minutesOfDay mins "
                } else {
                    "$minutesOfDay min "
                }
            } else {
                ""
            }
        }"
    }

}

fun String.calculateDurationOnlyHandM(): String {
    val duration = this.split(":")
    val hourOfDay = duration[0].toInt()
    val minutesOfDay = duration[1].toInt()
//    var secondsOfDay = 0
    if (duration.size > 2) {
//        secondsOfDay = duration[2].toInt()
        return "${
            if (hourOfDay > 0) {
                "${hourOfDay}h"

            } else {
                ""
            }
        } ${
            if (minutesOfDay > 0) {
                "${minutesOfDay}m"
            } else {
                if (hourOfDay == 0) {
                    "0m"
                } else
                    ""
            }
        }"
    } else {
        return "${
            if (hourOfDay > 0) {
                "${hourOfDay}h"
            } else {
                ""
            }
        } ${
            if (minutesOfDay > 0) {
                "${minutesOfDay}m"
            } else {
                if (hourOfDay == 0) {
                    "0m"
                } else
                    ""
            }
        }"
    }

}


fun printDifference(
    startDate: Date,
    endDate: Date,
    displayMonths: Boolean = false,
    isFromNotification: Boolean = false
): String {
    //milliseconds
    var different = endDate.time - startDate.time
    println("startDate : $startDate")
    println("endDate : $endDate")
    println("different : $different")
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    val elapsedDays = different / daysInMilli
    different %= daysInMilli
    val elapsedHours = different / hoursInMilli
    different %= hoursInMilli
    val elapsedMinutes = different / minutesInMilli
    different %= minutesInMilli
    val elapsedSeconds = different / secondsInMilli
    System.out.printf(
        "%d days, %d hours, %d minutes, %d seconds%n",
        abs(elapsedDays), abs(elapsedHours), abs(elapsedMinutes), abs(elapsedSeconds)
    )
    val week = abs(elapsedDays) / 7
    return if (displayMonths) {
        getTotalTime(
            abs(elapsedDays),
            abs(elapsedHours),
            abs(elapsedMinutes),
            abs(elapsedSeconds),
            isFromNotification
        )
    } else {
        getTotalTime(
            abs(week),
            abs(elapsedDays),
            abs(elapsedHours),
            abs(elapsedMinutes),
            abs(elapsedSeconds),
            isFromNotification
        )
    }
}

private fun getTotalTime(
    week: Long,
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    isFromNotification: Boolean
): String {
    return "${
        if (week > 0) {
            if (week > 1) {
                "$week Weeks "
            } else {
                "$week Week "
            }
        } else {
            ""
        }
    }${
        if (week > 0) {
            ""
        } else {
            if (days > 0) {
                if (days > 1) {
                    "$days Days "
                } else {
                    "$days Day "
                }
            } else {
                ""
            }
        }
    }${
        if (days > 0) {
            ""
        } else {
            if (hours > 0) {
                if (hours > 1) {
                    if (isFromNotification) "$hours hrs " else "$hours Hours "
                } else {
                    if (isFromNotification) "$hours hr " else "$hours Hours "
                }
            } else {
                ""
            }
        }
    }${
        if (hours > 0) {
            ""
        } else {
            if (minutes > 0) {
                if (minutes > 1) {
                    if (isFromNotification) "$minutes m " else "$minutes Minutes "
                } else {
                    if (isFromNotification) "$minutes m " else "$minutes Minute "
                }
            } else {
                ""
            }
        }
    }${
        if (minutes > 0) {
            ""
        } else {
            if (seconds > 0) {
                if (seconds > 1) {
                    if (isFromNotification) "$seconds s " else "$seconds Seconds "
                } else {
                    if (isFromNotification) "$seconds s " else "$seconds Second "
                }
            } else {
                ""
            }
        }
    }ago"
}

private fun getTotalTime(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    isFromNotification: Boolean
): String {
    val month = abs(days) / 31
    return "${
        if (month > 0) {
            if (month > 1) {
                "$month Months "
            } else {
                "$month Month "
            }
        } else {
            ""
        }
    }${
        if (month > 0) {
            ""
        } else {
            if (days > 0) {
                if (days > 1) {
                    "$days Days "
                } else {
                    "$days Day "
                }
            } else {
                ""
            }
        }
    }${
        if (days > 0) {
            ""
        } else {
            if (hours > 0) {
                if (hours > 1) {
                    if (isFromNotification) "$hours hrs " else "$hours Hours "
                } else {
                    if (isFromNotification) "$hours hr " else "$hours Hours "
                }
            } else {
                ""
            }
        }
    }${
        if (hours > 0) {
            ""
        } else {
            if (minutes > 0) {
                if (minutes > 1) {
                    if (isFromNotification) "$minutes m " else "$minutes Minutes "
                } else {
                    if (isFromNotification) "$minutes m " else "$minutes Minute "
                }
            } else {
                ""
            }
        }
    }${
        if (minutes > 0) {
            ""
        } else {
            if (seconds > 0) {
                if (seconds > 1) {
                    if (isFromNotification) "$seconds s " else "$seconds Seconds "
                } else {
                    if (isFromNotification) "$seconds s " else "$seconds Second "
                }
            } else {
                ""
            }
        }
    }ago"
}

fun Long.getDurationTime(): String {
    if (TimeUnit.MILLISECONDS.toHours(this) == 0L) {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)
            ),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        )
    } else {
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)
            ),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        )
    }
}

fun getTimePickerTimeFormat(context: Context): Boolean {
    return android.text.format.DateFormat.is24HourFormat(context)
}

fun Calendar.getTimeBasedOnTimeFormat(context: Context): String {
    val is24HourFormat =
        !userHolder.is12HourFormat ?: android.text.format.DateFormat.is24HourFormat(context)
    return if (is24HourFormat) {
        dateFormatter_local_h_mm.format(this.time)
    } else {
        dateFormatter_local_h_mm_aa.format(this.time)
    }
}

fun String.getLocalTimeInUTC(context: Context): String {
    val is24HourFormat =
        !userHolder.is12HourFormat ?: android.text.format.DateFormat.is24HourFormat(context)

    val date = if (is24HourFormat) {
        dateFormatter_local_hh_mm.parse(this)
    } else {
        dateFormatter_local_hh_mm_aa.parse(this)
    }
    dateFormatter_local_hh_mm_ss.timeZone = TimeZone.getTimeZone("UTC")
    date?.let {
        return dateFormatter_local_hh_mm_ss.format(date)
    } ?: kotlin.run {
        return dateFormatter_local_hh_mm_ss.format(Date())
    }
}

fun String.getUTCTimeInLocal(): String {
    dateFormatter_local_hh_mm_ss.timeZone = TimeZone.getTimeZone("UTC")
    dateFormatter_local_hh_mm.timeZone = TimeZone.getDefault()
    val date = dateFormatter_local_hh_mm_ss.parse(this)
    date?.let {
        return dateFormatter_local_hh_mm.format(date)
    } ?: kotlin.run {
        return dateFormatter_local_hh_mm.format(Date())
    }
}

fun String.getLocalTime(context: Context): String {
    val is24HourFormat =
        !userHolder.is12HourFormat ?: android.text.format.DateFormat.is24HourFormat(context)

    val date = dateFormatter_local_hh_mm.parse(this)
    if (is24HourFormat) {
        date?.let {
            return dateFormatter_local_hh_mm.format(date)
        } ?: kotlin.run {
            return dateFormatter_local_hh_mm.format(Date())
        }
    } else {
        date?.let {
            return dateFormatter_local_hh_mm_aa.format(date)
        } ?: kotlin.run {
            return dateFormatter_local_hh_mm_aa.format(Date())
        }
    }
}

fun getAlarmDateTimeFromHourAndMinute(hour: Int, minute: Int, isAfterLogin: Boolean): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    if ((calendar.time.time <= Calendar.getInstance().time.time) && !isAfterLogin) {
        calendar.add(Calendar.DATE, 1)
    }
    return calendar
}

fun getAlarmTimePassed(hour: Int, minute: Int): Boolean {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    return calendar.time.time <= Calendar.getInstance().time.time
}


fun String.getProgramDayFromDate(): String {
    return if (this == dateFormatter_yyyy_mm_dd.format(Calendar.getInstance().time)) "Today"
    else "Tomorrow"
}

fun DateTime.getFormattedDateForStats(): String {
    return formatterDateStats.print(this)
}

fun DateTime.getFormattedDayForStats(): String {
    return formatterDayStats.print(this)
}