# R8 rules.
#
# Most libraries here ship their own consumer rules (Hilt, Room, OkHttp, Coil,
# kotlinx.serialization), so this file only covers what R8 cannot infer: types the app
# touches reflectively.

# ---------------------------------------------------------------------------
# Gson DTOs
# ---------------------------------------------------------------------------
# The proxy payload is deserialised by field name. R8 would otherwise rename those fields
# and every article would arrive null — silently, with no crash to point at it.
#
# Note this rule previously named `data.remote.newsdata`, a package deleted when the app
# moved to its own proxy. A keep rule for a package that no longer exists protects nothing.
-keep class com.thecode.infotify.data.remote.infotify.** { *; }

# Gson needs generic signatures to resolve List<ArticleDto> and friends.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-dontwarn sun.misc.**
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ---------------------------------------------------------------------------
# Room entities
# ---------------------------------------------------------------------------
# Column names are resolved at runtime against the generated schema.
-keep class com.thecode.infotify.data.local.bookmark.BookmarkEntity { *; }

# ---------------------------------------------------------------------------
# Retrofit
# ---------------------------------------------------------------------------
-keepclasseswithmembers,includedescriptorclasses class * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# Retrofit builds the API implementation from the interface's own generic signature.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# ---------------------------------------------------------------------------
# kotlinx.serialization — navigation routes
# ---------------------------------------------------------------------------
# Typed Navigation routes are @Serializable objects; their generated serializers are
# looked up reflectively.
-keepclassmembers class com.thecode.infotify.presentation.navigation.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.thecode.infotify.presentation.navigation.**$$serializer { *; }

# ---------------------------------------------------------------------------
# WorkManager
# ---------------------------------------------------------------------------
# The worker is instantiated by class name from the persisted work request.
-keep class com.thecode.infotify.notification.DailyBriefingWorker { *; }

# ---------------------------------------------------------------------------
# Noise
# ---------------------------------------------------------------------------
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
