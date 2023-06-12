package com.devJoa.util

import android.content.Context
import android.content.SharedPreferences
import com.devJoa.dto.User
import com.google.gson.Gson

private const val TAG = "SharedPreferencesUtil_싸피"
class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "devJoa_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun addUser(user: User){
        val editor = preferences.edit()
        val jsonString = Gson().toJson(user)
        editor.putString("userId", user.userId)
        editor.putString("nickname", user.nickname)
        editor.putString("user", jsonString)
        editor.apply()
    }

    fun getUser(): User{
        val userId = preferences.getString("userId", "")
        if (userId != ""){
            val nickname = preferences.getString("nickname", "")
            val user = Gson().fromJson(preferences.getString("user", ""), User::class.java)
//            return User(userId!!, nickname!!)
            return user
        }else{
            return User()
        }
    }
    fun deleteUser(){
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }
}