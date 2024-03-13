package com.app.taocalligraphy.models


import com.google.gson.annotations.SerializedName

data class GoogleResponse(
    @SerializedName("displayName")
    val displayName: String, // Openxcell Teating
    @SerializedName("email")
    val email: String, // openxcell.testing1@gmail.com
    @SerializedName("expirationTime")
    val expirationTime: Int, // 1663333236
    @SerializedName("familyName")
    val familyName: String, // Teating
    @SerializedName("givenName")
    val givenName: String, // Openxcell
    @SerializedName("grantedScopes")
    val grantedScopes: List<String>,
    @SerializedName("id")
    val id: String, // 111398517170504553881
    @SerializedName("obfuscatedIdentifier")
    val obfuscatedIdentifier: String, // 317305FDBEB6AA945C118CE63D149065
    @SerializedName("photoUrl")
    val photoUrl: String, // https://lh3.googleusercontent.com/a/AItbvmnyaKzPe7FgUPTSkZ104QnXs_UE0JQyODsFl7-v=s96-c
    @SerializedName("tokenId")
    val tokenId: String // eyJhbGciOiJSUzI1NiIsImtpZCI6ImNhYWJmNjkwODE5MTYxNmE5MDhhMTM4OTIyMGE5NzViM2MwZmJjYTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMDMzOTIxMTYxODM2LWF1NGRzZjNpdDBqOTczYjAxYTBwYzc5dWIxcDZhOXJuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMTAzMzkyMTE2MTgzNi01amZsMW5oZHZrMmU0c2o5YjRzbXVucDUzbTk5djc5MC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExMTM5ODUxNzE3MDUwNDU1Mzg4MSIsImVtYWlsIjoib3BlbnhjZWxsLnRlc3RpbmcxQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiT3BlbnhjZWxsIFRlYXRpbmciLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUl0YnZtbnlhS3pQZTdGZ1VQVFNrWjEwNFFuWHNfVUUwSlF5T0RzRmw3LXY9czk2LWMiLCJnaXZlbl9uYW1lIjoiT3BlbnhjZWxsIiwiZmFtaWx5X25hbWUiOiJUZWF0aW5nIiwibG9jYWxlIjoiZW4tR0IiLCJpYXQiOjE2NjMzMjk2MzYsImV4cCI6MTY2MzMzMzIzNn0.MJfJHwEf5pqkkSmL954l5NkHGM0VkszlsohqaadNOW0ac7QzLieA1quFilGrGqG0d-O7qFfJKn8p93c9lw_k8KaB1MTbJZwuP8AUzcT-31ThrJcipz7gHSOtLBrsYyA08ridxBKbi4jr0yweTbLUbqK1XfCdD0RgCmTg2hzLg_SNpyS61f9TYjNdcVSvW7Fh8MWotTrJOUOVNs5ClzelWtl48VMFLVPTKGDhH7ay58Yqx9GkE8rKt6Cd_mReS2qu06NRBINhyzzZvhvc7joIPamQ7lXdLXyE7lm6i_wv51Y5xJ1QFTy8NKGvdT86erQZr25OoQ4MJxk2aNY3jwVsug
)