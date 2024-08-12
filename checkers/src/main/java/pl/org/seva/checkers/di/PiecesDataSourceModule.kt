package pl.org.seva.checkers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.org.seva.checkers.data.datasource.PiecesDatasource
import pl.org.seva.checkers.datasource.PiecesLiveDatasource
import pl.org.seva.checkers.datasource.mapper.PiecesMemoryToDataMapper
import pl.org.seva.checkers.datasource.model.PiecesMemoryModel
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PiecesDataSourceModule {

    @Provides
    fun providesPiecesMemoryToDataMapper() = PiecesMemoryToDataMapper()

    @Provides
    @Singleton
    fun providesPiecesLiveDataSource(
        piecesMemoryToDataMapper: PiecesMemoryToDataMapper
    ): PiecesDatasource = PiecesLiveDatasource(
        setOf(
            PiecesMemoryModel(
                UUID.randomUUID().toString(),
                "",
                WHITE_START_POSITION,
                BLACK_START_POSITION,
                emptyList(),
                emptyList(),
            )
        ),
        piecesMemoryToDataMapper,
    )

    companion object {
        private val WHITE_START_POSITION = listOf(0 to 7, 1 to 6, 2 to 7, 3 to 6, 4 to 7, 5 to 6, 6 to 7, 7 to 6, 0 to 5, 2 to 5, 4 to 5, 6 to 5)
        private val BLACK_START_POSITION = listOf(0 to 1, 1 to 0, 2 to 1, 3 to 0, 4 to 1, 5 to 0, 6 to 1, 7 to 0, 1 to 2, 3 to 2, 5 to 2, 7 to 2)
    }
}
