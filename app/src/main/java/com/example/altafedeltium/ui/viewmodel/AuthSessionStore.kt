package com.example.altafedeltium.ui.viewmodel

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class AuthUser(
    val fullName: String,
    val email: String,
    val password: String
)

object AuthSessionStore {
    private const val PREFS_NAME = "auth_session_store"
    private const val KEY_USERS = "users_json"
    private const val KEY_CURRENT_USER_EMAIL = "current_user_email"

    private val defaultUser = AuthUser(
        fullName = "Paolo Cortellesi",
        email = "paolo.cortellesi@email.it",
        password = "password123"
    )

    private val users = mutableListOf(
        defaultUser
    )

    private var appContext: Context? = null
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        appContext = context.applicationContext
        loadState()
        isInitialized = true
    }

    var currentUser: AuthUser? = null
        private set

    fun register(fullName: String, email: String, password: String): Result<AuthUser> {
        val normalizedEmail = email.trim().lowercase()
        if (users.any { it.email.equals(normalizedEmail, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Esiste gia un account con questa email"))
        }

        val user = AuthUser(
            fullName = fullName.trim(),
            email = normalizedEmail,
            password = password
        )
        users.add(user)
        currentUser = user
        persistState()
        return Result.success(user)
    }

    fun login(email: String, password: String): AuthUser? {
        val normalizedEmail = email.trim().lowercase()
        val user = users.firstOrNull {
            it.email.equals(normalizedEmail, ignoreCase = true) && it.password == password
        }
        currentUser = user
        persistState()
        return user
    }

    fun logout() {
        currentUser = null
        persistState()
    }

    private fun loadState() {
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        val savedUsersJson = prefs.getString(KEY_USERS, null)
        val savedCurrentEmail = prefs.getString(KEY_CURRENT_USER_EMAIL, null)

        users.clear()
        if (!savedUsersJson.isNullOrBlank()) {
            val jsonArray = JSONArray(savedUsersJson)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val fullName = item.optString("fullName").trim()
                val email = item.optString("email").trim().lowercase()
                val password = item.optString("password")
                if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    users.add(AuthUser(fullName = fullName, email = email, password = password))
                }
            }
        }

        if (users.isEmpty()) {
            users.add(defaultUser)
        }

        currentUser = savedCurrentEmail
            ?.trim()
            ?.lowercase()
            ?.let { email -> users.firstOrNull { it.email.equals(email, ignoreCase = true) } }
    }

    private fun persistState() {
        val prefs = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        val usersArray = JSONArray()
        users.forEach { user ->
            usersArray.put(
                JSONObject()
                    .put("fullName", user.fullName)
                    .put("email", user.email)
                    .put("password", user.password)
            )
        }

        prefs.edit()
            .putString(KEY_USERS, usersArray.toString())
            .putString(KEY_CURRENT_USER_EMAIL, currentUser?.email)
            .apply()
    }
}

