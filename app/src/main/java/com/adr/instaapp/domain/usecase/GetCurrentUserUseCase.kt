package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.NoParametersFlowUseCase
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) : NoParametersFlowUseCase<User?>() {

    override operator fun invoke(): Flow<User?> {
        return userRepository.getCurrentUser()
    }
}
