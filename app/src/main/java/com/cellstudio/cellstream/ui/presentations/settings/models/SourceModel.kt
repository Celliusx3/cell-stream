package com.cellstudio.cellstream.ui.presentations.settings.models

import com.cellstudio.cellstream.data.base.models.entities.Source
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel

data class SourceModel (
    val id: String,
    override val title: String
): ActionModel {
    companion object {
        @JvmStatic fun create(model: Source): SourceModel {
            return SourceModel(model.id, model.title)
        }
    }
}