# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting clinicplus build.gradle.
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
-ignorewarnings
-keep class * {
   public private *;
}
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe.** { *; }
#-keep class com.google.gson.stream.** { *; }
# Volley
-dontwarn com.android.volley.**
-dontwarn com.android.volley.error.**
-keep class com.android.volley.** { *; }
-keep class com.android.volley.toolbox.** { *; }
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }
-keep interface com.android.volley.** { *; }
-keep class org.apache.commons.logging.*
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keeppackagenames
-keep class com.opentok.** { *; }
-keep class com.vonage.** { *; }
-keep class org.webrtc.** { *; }
-keep class org.otwebrtc.** { *; }

-keep class com.opentok.android.* { *; }
-keep class com.opentok.android.Session.** { *; }
-keep class com.opentok.client.* { *; }
-keep class com.opentok.impl.* { *; }
-keep class com.opentok.otc.* { *; }
-keep class org.webrtc.* { *; }
-keep class org.otwebrtc.* { *; }
-keep class org.otwebrtc.voiceengine.* { *; }
-keep class org.otwebrtc.voiceengine.*
-keep class org.otwebrtc.WebRtcClassLoader.**{ *; }
-keep class org.otwebrtc.voiceengine61.* { *; }
-keep class org.otwebrtc.voiceengine.BuildInfo.** { *; }

-dontwarn com.opentok.*
-keepclassmembers class com.opentok.* { *; }

-keep class ai.api.** { *; }

-keep class com.onesignal.** { *; }


-keep class com.squareup.duktape.** { *; }

 -keep class com.squareup.retrofit2.** { *; }
 -keep class com.squareup.okhttp3.** { *; }
 -keep class com.github.bumptech.glide.** { *; }
 -keep class android.arch.lifecycle.** { *; }
 -keep class com.github.vihtarb.** { *; }
 -keep class com.google.android.exoplayer.** { *; }
 -keep class org.jsoup.** { *; }
 -keep class com.github.clans.** { *; }
 -keep class com.yamlee.** { *; }
 -keep class com.github.mreram.** { *; }
-keep class com.github.kizitonwose.** { *; }
 -keep class com.github.prolificinteractive.** { *; }
 -keep class androidx.appcompat.** { *; }
 -keep class com.jakewharton.threetenabp.** { *; }
 -keep class com.whitecoats.clinicplus.** { *; }

