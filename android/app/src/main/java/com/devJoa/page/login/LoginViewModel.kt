package com.devJoa.page.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devJoa.dto.User
import com.devJoa.dto.UserLogin
import com.devJoa.util.RetrofitUtil
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun login(loginId: String, loginPass: String) {
        viewModelScope.launch {
            try {
                _user.value = RetrofitUtil.userService.login(UserLogin(loginId, loginPass))
            } catch(e:Exception) {
                _user.value = User()
            }
        }
    }
}