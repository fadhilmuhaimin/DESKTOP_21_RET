package com.lksprovinsi.desktop_21.network

import android.os.Parcelable



data class ResponseLogin(
	val birthday: String? = null,
	val phoneNumber: String? = null,
	val name: String? = null,
	val id: Int? = null,
	val email: String? = null
)

data class LoginRequest(
	val email: String,
	val password: String
)

