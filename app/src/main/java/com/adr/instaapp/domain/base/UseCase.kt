package com.adr.instaapp.domain.base

import kotlinx.coroutines.flow.Flow

abstract class UseCase<in P, R> {
    abstract suspend operator fun invoke(parameters: P): Result<R>
}

abstract class FlowUseCase<in P, R> {
    abstract operator fun invoke(parameters: P): Flow<R>
}

abstract class NoParametersUseCase<R> {
    abstract suspend operator fun invoke(): Result<R>
}

abstract class NoParametersFlowUseCase<R> {
    abstract operator fun invoke(): Flow<R>
}
