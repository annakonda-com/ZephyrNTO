package ru.myitschool.work.domain.main

import ru.myitschool.work.data.entity.Employee
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.repo.MainRepository

class GetUserDataUseCase(
    private val repository: MainRepository
) {
    suspend operator fun invoke(): Result<Employee> {
        return repository.getUserInfo()
    }
}