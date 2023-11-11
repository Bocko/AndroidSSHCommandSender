package com.lgbotond.androidsshcommandsender.util.xtensions

import android.view.View
import android.view.animation.AlphaAnimation

fun View.fadeIn(durationMillis: Long = 250, startOffsetMillis: Long = 1000) {
    this.startAnimation(AlphaAnimation(0f, 1f).apply {
        startOffset = startOffsetMillis
        duration = durationMillis
        fillAfter = true
    })
}

fun View.fadeOut(durationMillis: Long = 250, startOffsetMillis: Long = 1000) {
    this.startAnimation(AlphaAnimation(1f, 0f).apply {
        startOffset = startOffsetMillis
        duration = durationMillis
        fillAfter = true
    })
}