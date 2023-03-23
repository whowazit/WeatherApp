package com.jayjohn.app.feature.domain.usecase

import com.jayjohn.app.feature.domain.model.LocationData
import com.jayjohn.app.feature.domain.repository.WeatherRepository
import com.jayjohn.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * A UseCase class for getting the Location response
 * Can also be used for changing, sorting or filtering the response before passing to the viewModel
 * @param weatherRepository
 */
class GetLocationUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(
        keyword: String
    ): Flow<Resource<List<LocationData>>> = flow {
        try {
            emit(Resource.Loading())

            val response = weatherRepository.getLocation(
                keyword = keyword
            )

            emit(Resource.Success(response.distinct()))
        } catch (e: HttpException) {
            emit(Resource.Error(message = e.cause?.message ?: "Something went wrong"))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.cause?.message ?: "Something went wrong"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message ?: "Something went wrong"))
        }
    }
}