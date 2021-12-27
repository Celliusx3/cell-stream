package com.cellstudio.cellstream.data.base.repositories.config

import com.cellstudio.cellstream.data.base.models.entities.Source
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.data.commons.environments.Environment

class DefaultConfigRepository(private val environment: Environment) : ConfigRepository {

    override val sources: List<Source> = environment.dataSources.map { Source(it.name, it.name) }

    private val _selectedSource = environment.selectedDataSource
    override val selectedSource: SourceRepository = _selectedSource

    override fun setSource(source: String) {
        environment.setSource(source)
    }
}