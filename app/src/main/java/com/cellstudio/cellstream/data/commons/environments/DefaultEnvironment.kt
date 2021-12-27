package com.cellstudio.cellstream.data.commons.environments

import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.data.services.network.NetworkService
import com.cellstudio.cellstream.data.services.storage.StorageService
import com.cellstudio.cellstream.data.services.storage.StorageServiceConstants
import com.cellstudio.cellstream.data.source.full.cableav.repositories.CableAVRepository
import com.cellstudio.cellstream.data.source.full.gimy.repositories.GimyRepository
import com.cellstudio.cellstream.data.source.example.olevod.repositories.OlevodRepository
import com.cellstudio.cellstream.data.source.full.sexkbj.repositories.SexKbjRepository

class DefaultEnvironment(networkService: NetworkService, private val storageService: StorageService) : Environment {
    override val dataSources: List<SourceRepository> = listOf(
        OlevodRepository(networkService)
    )

    private val _selectedDataSource = this.dataSources.find {
        it.name == storageService.getString(StorageServiceConstants.SELECTED_DATA_SOURCE)
    }?: this.dataSources[0]

    override val selectedDataSource: SourceRepository = _selectedDataSource

    override fun setSource(source: String) {
        storageService.commitString(StorageServiceConstants.SELECTED_DATA_SOURCE, source)
    }
}