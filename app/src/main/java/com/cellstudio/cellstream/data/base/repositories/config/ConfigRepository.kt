package com.cellstudio.cellstream.data.base.repositories.config

import com.cellstudio.cellstream.data.base.models.entities.Source
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository

interface ConfigRepository {
    val sources: List<Source>
    val selectedSource: SourceRepository
    fun setSource(source: String)
}