package com.cellstudio.cellmovie.player.utils

import java.util.concurrent.TimeUnit

object TimeUtils {
    /**
     * Returns a formatted string mm:ss to be used in the video player controls
     *
     * @param milliseconds Number of milliseconds to transform
     * @param isNegative   true if the String representation should contain a "-" character
     * @return A String object with the time in the format "mm:ss"
     */
    fun getTimePlayerFormat(milliseconds: Long, isNegative: Boolean): String {
        val minutes = milliseconds.toInt() / (MINUTE_TO_SECOND * SECOND_TO_MS)
        val seconds = milliseconds.toInt() / SECOND_TO_MS % MINUTE_TO_SECOND
        var time = String.format("%02d:%02d", minutes, seconds)
        if (isNegative) {
            time = "- $time"
        }
        return time
    }

    val SECOND_TO_MS = 1000
    val MINUTE_TO_SECOND = 60

    /**
     * Returns a formatted Duration String as hh:mm:ss
     * @param durationSeconds Long
     * @return String
     */
    fun getFormattedDurationString(durationSeconds: Long): String {

        var string = String.format("%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(durationSeconds),
                TimeUnit.SECONDS.toMinutes(durationSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(durationSeconds)),
                TimeUnit.SECONDS.toSeconds(durationSeconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.SECONDS.toMinutes(durationSeconds)));

        //removes `hours` if there is no value
        string = if (string.startsWith("00:")) string.substring("00:".length) else string

        return string
    }
}