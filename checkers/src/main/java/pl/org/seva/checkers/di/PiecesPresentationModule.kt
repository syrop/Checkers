package pl.org.seva.checkers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper
import pl.org.seva.checkers.data.mapper.PiecesDomainToDataMapper
import pl.org.seva.checkers.data.repository.PiecesLiveRepository
import pl.org.seva.checkers.domain.usecase.BlackMoveUseCase
import pl.org.seva.checkers.domain.usecase.FetchPiecesUseCase
import pl.org.seva.checkers.domain.repository.PiecesRepository
import pl.org.seva.checkers.domain.usecase.ResetUseCase
import pl.org.seva.checkers.domain.usecase.WhiteMoveUseCase
import pl.org.seva.checkers.presentation.mapper.PiecesDomainToPresentationMapper
import pl.org.seva.checkers.presentation.mapper.PiecesPresentationToDomainMapper
import pl.org.seva.checkers.ui.mapper.PiecesPresentationToUiMapper
import javax.inject.Singleton

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
    fun provideWhiteMoveUseCase(piecesRepository: PiecesRepository) = WhiteMoveUseCase(piecesRepository)

    @Provides
    fun provideBlackMoveUseCase(piecesRepository: PiecesRepository) = BlackMoveUseCase(piecesRepository)

    @Provides
    fun provideFetchPiecesUseCase(piecesRepository: PiecesRepository) = FetchPiecesUseCase(piecesRepository)

    @Provides
    fun provideResetUseCase(piecesRepository: PiecesRepository) = ResetUseCase(piecesRepository)

    @Provides
    @Singleton
    fun providePiecesRepository(
        piecesDatasource: PiecesDatasource,
        piecesDataToDomainMapper: PiecesDataToDomainMapper,
        piecesDomainToDataMapper: PiecesDomainToDataMapper,
    ): PiecesRepository = PiecesLiveRepository(
        piecesDatasource,
        piecesDataToDomainMapper,
        piecesDomainToDataMapper
    )

}
