package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository
import javax.inject.Inject

data class LoginParams(val username: String, val password: String)

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<LoginParams, User>() {

    override suspend fun invoke(parameters: LoginParams): Result<User> {
        return userRepository.login(parameters.username, parameters.password)
    }
}
