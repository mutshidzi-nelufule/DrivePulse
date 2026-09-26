package com.example.drivepulse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivepulse.data.model.User
import com.example.drivepulse.data.repository.AuthRepository
import com.example.drivepulse.data.repository.AuthResult
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    var loginState by mutableStateOf(LoginUiState())
        private set

    var registerState by mutableStateOf(RegisterUiState())
        private set

    var authState by mutableStateOf(
        AuthState(
            isAuthenticated = authRepository.isLoggedIn(),
            currentUser = authRepository.getCurrentUser()
        )
    )
        private set

    var currentUser: User?
        get() = authState.currentUser
        private set(value) {
            authState = authState.copy(
                isAuthenticated = value != null,
                currentUser = value
            )
        }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()


    // LOGIN


    fun onLoginEmailChange(value: String) {
        loginState = loginState.copy(
            email = value,
            errorMessage = null
        )
    }

    fun onLoginPasswordChange(value: String) {
        loginState = loginState.copy(
            password = value,
            errorMessage = null
        )
    }

    fun toggleLoginPasswordVisibility() {
        loginState = loginState.copy(
            isPasswordVisible = !loginState.isPasswordVisible
        )
    }

    fun login(onSuccess: () -> Unit) {

        if (loginState.isLoading) return

        loginState = loginState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {

            val result = authRepository.login(
                email = loginState.email,
                password = loginState.password
            )

            when (result) {

                is AuthResult.Success -> {

                    currentUser = result.user

                    loginState = LoginUiState()

                    onSuccess()
                }

                is AuthResult.Error -> {

                    loginState = loginState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }


    // REGISTRATION


    fun onRegisterNameChange(value: String) {
        registerState = registerState.copy(
            fullName = value,
            errorMessage = null
        )
    }

    fun onRegisterEmailChange(value: String) {
        registerState = registerState.copy(
            email = value,
            errorMessage = null
        )
    }

    fun onRegisterPasswordChange(value: String) {
        registerState = registerState.copy(
            password = value,
            errorMessage = null
        )
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        registerState = registerState.copy(
            confirmPassword = value,
            errorMessage = null
        )
    }

    fun toggleRegisterPasswordVisibility() {
        registerState = registerState.copy(
            isPasswordVisible = !registerState.isPasswordVisible
        )
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        registerState = registerState.copy(
            isConfirmPasswordVisible =
                !registerState.isConfirmPasswordVisible
        )
    }

    fun register(onSuccess: () -> Unit) {

        if (registerState.isLoading) return

        registerState = registerState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {

            val result = authRepository.register(
                fullName = registerState.fullName,
                email = registerState.email,
                password = registerState.password,
                confirmPassword = registerState.confirmPassword
            )

            when (result) {

                is AuthResult.Success -> {

                    currentUser = result.user

                    registerState = RegisterUiState()

                    onSuccess()
                }

                is AuthResult.Error -> {

                    registerState = registerState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }



    fun logout(onLoggedOut: () -> Unit) {

        authRepository.logout()

        currentUser = null

        loginState = LoginUiState()

        registerState = RegisterUiState()

        onLoggedOut()
    }



    fun clearErrors() {

        loginState = loginState.copy(
            errorMessage = null
        )

        registerState = registerState.copy(
            errorMessage = null
        )
    }
}