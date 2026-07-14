# This is a configuration file for R8
# U can use this to override the default ProGuard rules for this project

# Keep everything in Material3
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.ui.** { *; }

# Keep Room
-keepclasseswithmembernames class * {
    @androidx.room.* <methods>;
}

# Keep ExoPlayer
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }

# Keep AdMob
-keep class com.google.android.gms.ads.** { *; }

# Keep our app classes
-keep class com.musicplayer.pro.** { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
