package com.poojaseva.di

import com.poojaseva.data.repository.InMemoryBookingRepository
import com.poojaseva.data.repository.InMemoryReviewRepository
import com.poojaseva.data.repository.NativePaymentGateway
import com.poojaseva.data.repository.StaticPanditRepository
import com.poojaseva.data.repository.StaticServiceRepository
import com.poojaseva.data.repository.StubAuthRepository
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
    @Binds @Singleton abstract fun bindServiceRepo(impl: StaticServiceRepository): ServiceRepository
    @Binds @Singleton abstract fun bindPanditRepo(impl: StaticPanditRepository): PanditRepository
    @Binds @Singleton abstract fun bindBookingRepo(impl: InMemoryBookingRepository): BookingRepository
    @Binds @Singleton abstract fun bindAuthRepo(impl: StubAuthRepository): AuthRepository
    @Binds @Singleton abstract fun bindReviewRepo(impl: InMemoryReviewRepository): ReviewRepository
    @Binds @Singleton abstract fun bindPayment(impl: NativePaymentGateway): PaymentGateway
}
