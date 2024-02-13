package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.repositories.ChaptersRepository

class GetChaptersUseCase(private val chaptersRepository: ChaptersRepository) {
    operator suspend fun invoke() = chaptersRepository.fetchChapters()
    operator suspend fun invoke(radius: Double) = chaptersRepository.fetchChapters(radius)
    operator suspend fun invoke(pilotId: String) = chaptersRepository.fetchChapters(pilotId)
}