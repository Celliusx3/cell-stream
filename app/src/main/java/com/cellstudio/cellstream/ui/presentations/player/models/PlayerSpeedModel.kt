package com.cellstudio.cellstream.ui.presentations.player.models

import com.cellstudio.cellstream.player.models.CellPlayerPlaySpeed
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel

class PlayerSpeedModel (val speed: CellPlayerPlaySpeed): ActionModel{
    override val title: String = speed.text

}