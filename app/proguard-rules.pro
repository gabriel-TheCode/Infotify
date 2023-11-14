# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Models
-dontwarn com.thecode.infotify.framework.datasource.network.model.**
-dontwarn com.thecode.infotify.database.article.**
-dontwarn com.thecode.infotify.database.source.**
-dontwarn com.thecode.infotify.core.domain.**
-keep class com.thecode.infotify.framework.datasource.network.model.** { *; }
-keep class com.thecode.infotify.database.article.** { *; }
-keep class com.thecode.infotify.database.source.** { *; }
-keep class com.thecode.infotify.core.domain.** { *; }
