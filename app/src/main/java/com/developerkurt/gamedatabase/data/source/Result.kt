package com.developerkurt.gamedatabase.data.source

/**
 * A generic class that holds a value with its loading status.
 */
sealed class Result<out R>
{
    object Loading : Result<Nothing>()
    object FailedToUpdate : Result<Nothing>()

    //TODO [Improvement]: If the app is going to be translated, create an enum class with the error codes to determine which string resource to display
    //instead of passing the string directly
    data class Error(
            val exception: Exception? = null,
            val errorMessage: String = "No additional message was included") : Result<Nothing>()

    data class Success<out T>(val data: T) : Result<T>()


    override fun toString(): String
    {
        return when (this)
        {
            is Success<*> -> "Success[data: $data]"
            is Error -> "Error: " +
                    "${exception?.let { "\n Exception: $it" }}" +
                    "\n Message: $errorMessage"
            is FailedToUpdate -> "FailedToUpdate"
            is Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null
