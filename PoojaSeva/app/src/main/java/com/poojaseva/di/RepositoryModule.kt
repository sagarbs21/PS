package com.poojaseva.di

import com.poojaseva.data.repository.RemoteAuthRepository
import com.poojaseva.data.repository.RemoteBookingRepository
import com.poojaseva.data.repository.RemotePanditRepository
import com.poojaseva.data.repository.RemotePaymentGateway
import com.poojaseva.data.repository.RemoteReviewRepository
import com.poojaseva.data.repository.RemoteServiceRepository
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.domain.repository.PanditRepository
import com.poojaseva.domain.repository.PaymentGateway
import com.poojaseva.domain.repository.ReviewRepository
import com.poojaseva.domain.repository.ServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindServiceRepo(impl: RemoteServiceRepository): ServiceRepository
    @Binds @Singleton abstract fun bindPanditRepo(impl: RemotePanditRepository): PanditRepository
    @Binds @Singleton abstract fun bindBookingRepo(impl: RemoteBookingRepository): BookingRepository
    @Binds @Singleton abstract fun bindAuthRepo(impl: RemoteAuthRepository): AuthRepository
    @Binds @Singleton abstract fun bindReviewRepo(impl: RemoteReviewRepository): ReviewRepository
    @Binds @Singleton abstract fun bindPayment(impl: RemotePaymentGateway): PaymentGateway
}
