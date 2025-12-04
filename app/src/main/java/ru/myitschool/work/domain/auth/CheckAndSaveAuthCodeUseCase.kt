package ru.myitschool.work.domain.auth

import ru.myitschool.work.data.repo.AuthRepository

    class CheckAndSaveAuthCodeUseCase(
        private val repository: AuthRepository
    ) {
        suspend operator fun invoke(
            text: String
        ): Result<Boolean> {
            return repository.checkAndSave(text)
        }
    }