package com.cellstudio.cellstream.player.models

class QualityLevel (
        val trackIndex: Int = -1,
        val groupIndex: Int = -1,
        val rendererIndex: Int = -1,
        val bitrate: Int = -1,
        val label: String = "",
        val width: Int = -1,
        val height: Int = -1
) {
    fun getText(): String {
        return label
    }
}