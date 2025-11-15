package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.repository.UserRepository

class LogoutUseCase(
    private val userRepository: UserRepository
) : UseCase<Unit, Unit>() {

    override suspend fun invoke(parameters: Unit): Result<Unit> {
        return userRepository.logout()
    }
}
