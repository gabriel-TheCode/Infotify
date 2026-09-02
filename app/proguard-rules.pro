# Retrofit / Gson: the NewsData DTOs are only instantiated reflectively.
-keep class com.thecode.infotify.data.remote.newsdata.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations

# Retrofit
-keepclasseswithmembers,includedescriptorclasses class * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# kotlinx.serialization: navigation routes are serializable objects
-keepclassmembers class com.thecode.infotify.presentation.navigation.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
