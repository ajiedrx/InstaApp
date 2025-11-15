package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.NoParametersUseCase
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) : NoParametersUseCase<User?>() {

    override suspend operator fun invoke(): User? {
        return userRepository.getCurrentUser()
    }
}
