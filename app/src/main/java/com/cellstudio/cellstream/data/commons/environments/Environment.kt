package com.cellstudio.cellstream.data.commons.environments

import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository

interface Environment {
    val dataSources: List<SourceRepository>
    val selectedDataSource: SourceRepository

    fun setSource(source: String)
}