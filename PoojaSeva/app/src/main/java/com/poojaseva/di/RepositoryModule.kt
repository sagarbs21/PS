package com.poojaseva.di

import com.poojaseva.data.repository.AuthRepositoryImpl
import com.poojaseva.data.repository.BookingRepositoryImpl
import com.poojaseva.data.repository.CatalogRepositoryImpl
import com.poojaseva.data.repository.PaymentRepositoryImpl
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.domain.repository.CatalogRepository
import com.poojaseva.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(impl: CatalogRepositoryImpl): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository
}
