package pl.org.seva.checkers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper
import pl.org.seva.checkers.data.repository.PiecesLiveRepository
import pl.org.seva.checkers.domain.BlackMoveUseCase
import pl.org.seva.checkers.domain.FetchPiecesUseCase
import pl.org.seva.checkers.domain.repository.PiecesRepository
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.ui.mapper.PiecesPresentationToUiMapper

@Module
@InstallIn(SingletonComponent::class)
class PiecesPresentationModule {

    @Provides
    fun providePiecesPresentationToUiMapper() = PiecesPresentationToUiMapper()

    @Provides
    fun providePiecesDomainToPresentationMapper() = PiecesDomainToPresentationMapper()

    @Provides
    fun providePiecesPresentationToDomainMapper() = PiecesPresentationToDomainMapper()

    @Provides
    fun provideBlackMoveUseCase(piecesRepository: PiecesRepository) = BlackMoveUseCase(piecesRepository)

    @Provides
    fun provideFetchPiecesUseCase(piecesRepository: PiecesRepository) = FetchPiecesUseCase(piecesRepository)

    @Provides
    fun providePiecesRepository(
        piecesDatasource: PiecesDatasource,
        piecesDataToDomainMapper: PiecesDataToDomainMapper,
    ): PiecesRepository = PiecesLiveRepository(piecesDatasource, piecesDataToDomainMapper)

}
