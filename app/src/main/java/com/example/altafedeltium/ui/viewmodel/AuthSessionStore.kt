package com.example.altafedeltium.ui.viewmodel

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class AuthUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String = ""
) {
    val fullName: String get() = "$firstName $lastName"
}

object AuthSessionStore {
    private const val PREFS_NAME = "auth_session_store"
    private const val KEY_USERS = "users_json"
    private const val KEY_CURRENT_USER_EMAIL = "current_user_email"

    private val defaultUser = AuthUser(
        firstName = "Paolo",
        lastName = "Cortellesi",
        email = "paolo.cortellesi@email.it",
        password = "password123",
        phone = ""
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

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String = ""
    ): Result<AuthUser> {
        val normalizedEmail = email.trim().lowercase()
        if (users.any { it.email.equals(normalizedEmail, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Esiste gia un account con questa email"))
        }

        val user = AuthUser(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = normalizedEmail,
            password = password,
            phone = phone.trim()
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
                val firstName = item.optString("firstName").trim()
                val lastName = item.optString("lastName").trim()
                val email = item.optString("email").trim().lowercase()
                val password = item.optString("password")
                val phone = item.optString("phone", "").trim()
                if (firstName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    users.add(
                        AuthUser(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,
                            phone = phone
                        )
                    )
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
                    .put("firstName", user.firstName)
                    .put("lastName", user.lastName)
                    .put("email", user.email)
                    .put("password", user.password)
                    .put("phone", user.phone)
            )
        }

        prefs.edit()
            .putString(KEY_USERS, usersArray.toString())
            .putString(KEY_CURRENT_USER_EMAIL, currentUser?.email)
            .apply()
    }

    /**
     * Aggiorna i dati dell'utente corrente (se presente) e persiste lo stato.
     */
    fun updateCurrentUser(firstName: String, lastName: String, email: String, phone: String = "") {
        val existing = currentUser ?: return
        val updated = AuthUser(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = email.trim().lowercase(),
            password = existing.password,
            phone = phone.trim()
        )

        // Replace in users list
        val index = users.indexOfFirst { it.email.equals(existing.email, ignoreCase = true) }
        if (index >= 0) {
            users[index] = updated
        } else {
            users.add(updated)
        }
        currentUser = updated
        persistState()
    }

    fun updateCurrentUserPhone(phone: String) {
        val existing = currentUser ?: return
        updateCurrentUser(existing.firstName, existing.lastName, existing.email, phone)
    }
}
