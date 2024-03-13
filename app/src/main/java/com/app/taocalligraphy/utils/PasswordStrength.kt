package com.app.taocalligraphy.utils

import android.graphics.Color
import android.text.*
import com.app.taocalligraphy.R

enum class PasswordStrength(internal var resId: Int, color: Int) {

    WEAK(R.string.password_strength_weak, Color.RED),
    MEDIUM(R.string.password_strength_medium, Color.YELLOW),
    STRONG(R.string.password_strength_strong, Color.GREEN),
    DEFAULT(R.string.password_strength, Color.WHITE);

    var color: Int = 0
        internal set

    init {
        this.color = color
    }

    fun getText(ctx: android.content.Context): CharSequence {
        return ctx.getText(resId)
    }

    companion object {

        //This value defines the minimum length for the password
        internal var REQUIRED_LENGTH = 10

        //This value determines the maximum length of the password
        internal var MAXIMUM_LENGTH = 30

        //This value determines if the password should contain special characters. set it as "false" if you
        //do not require special characters for your password field.
        internal var REQUIRE_SPECIAL_CHARACTERS = true

        //This value determines if the password should contain digits. set it as "false" if you
        //do not require digits for your password field.
        internal var REQUIRE_DIGITS = true

        //This value determines if the password should require low case. Set it as "false" if you
        //do not require lower cases for your password field.
        internal var REQUIRE_LOWER_CASE = true

        //This value determines if the password should require upper case. Set it as "false" if you
        //do not require upper cases for your password field.
        internal var REQUIRE_UPPER_CASE = true

        fun calculateStrength(password: String): PasswordStrength {
            var currentScore = 0
            var sawUpper = false
            var sawLower = false
            var sawDigit = false
            var sawSpecial = false


            for (element in password) {
                if (!sawSpecial && !Character.isLetterOrDigit(element)) {
//                    currentScore += 1
                    sawSpecial = true
                } else {
                    if (!sawDigit && Character.isDigit(element)) {
//                        currentScore += 1
                        sawDigit = true
                    } else {
                        if (!sawUpper || !sawLower) {
                            if (Character.isUpperCase(element))
                                sawUpper = true
                            else
                                sawLower = true
//                            if (sawUpper && sawLower)
//                                currentScore += 1
                        }
                    }
                }
            }

            if (password.length >= 10 && sawSpecial && sawUpper && sawDigit && sawLower) {
                currentScore = 2
            }else if (password.length >= 5 && (sawSpecial || sawUpper || sawDigit || sawLower)) {
                currentScore = 1
            } else if (password.isEmpty()) {
                currentScore = 3
            } else {
                currentScore = 0
            }

           /* if (password.length > REQUIRED_LENGTH) {
                if (REQUIRE_SPECIAL_CHARACTERS && !sawSpecial
                    || REQUIRE_UPPER_CASE && !sawUpper
                    || REQUIRE_LOWER_CASE && !sawLower
                    || REQUIRE_DIGITS && !sawDigit
                ) {
                    currentScore = 1
                } else {
                    currentScore = 2
                    if (password.length > MAXIMUM_LENGTH) {
                        currentScore = 3
                    }
                }
            } else {
                currentScore = 0
            }*/

            when (currentScore) {
                0 -> return WEAK
                1 -> return MEDIUM
                2 -> return STRONG
                3 -> return DEFAULT
            }

            return DEFAULT
        }
    }

}