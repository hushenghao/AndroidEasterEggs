package com.dede.android_eggs.util

import androidx.annotation.IntRange
import okio.buffer
import okio.source
import kotlin.math.min

object AGPUtils {

    /**
     * Get Vcs revision
     *
     * [VcsInfo](https://developer.android.google.cn/reference/tools/gradle-api/8.3/com/android/build/api/dsl/VcsInfo)
     */
    fun getVcsRevision(@IntRange(from = 0L) len: Int = Int.MAX_VALUE): String? {
        if (len <= 0) return null

        val stream = AGPUtils::class.java
            .getResourceAsStream("/META-INF/version-control-info.textproto") ?: return null
        val textProto = stream.use {
            it.source().buffer().readString(Charsets.UTF_8)
        }

        /**
         * repositories {
         *   system: GIT
         *   local_root_path: "$PROJECT_DIR"
         *   revision: "368dd400a33e5c06cfcdc67f017517bb8b19f1ec"
         * }
         */
        val regex = Regex("revision:\\s*\"(\\S+)\"")
        val matchResult = regex.find(textProto) ?: return null
        val revision = matchResult.groupValues.getOrNull(1) ?: return null
        if (revision.isEmpty()) {
            return null
        }
        return revision.substring(0, min(len, revision.length))
    }
}