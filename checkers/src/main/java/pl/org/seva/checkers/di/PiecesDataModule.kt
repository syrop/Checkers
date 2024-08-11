package pl.org.seva.checkers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.org.seva.checkers.data.mapper.PiecesDataToDomainMapper

@Module
@InstallIn(SingletonComponent::class)
class PiecesDataModule {

    @Provides
    fun providePiecesDataToDomainMapper() = PiecesDataToDomainMapper()

}