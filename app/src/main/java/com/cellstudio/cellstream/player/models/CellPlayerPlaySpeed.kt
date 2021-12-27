package com.cellstudio.cellstream.player.models

enum class CellPlayerPlaySpeed (val speed: Float, val text: String){
    NORMAL(1.0f, "1.0x"),
    SLIGHTLY_FAST (1.25f, "1.25x"),
    MEDIUM_FAST(1.5f, "1.5x"),
    FAST(1.75f, "1.75x"),
    FASTEST(2.0f, "2.0x")
}