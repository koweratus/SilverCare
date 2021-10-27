package com.example.silvercare.di

import com.example.silvercare.view.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideMainActivity(): MainActivity {
        return MainActivity()
    }

}