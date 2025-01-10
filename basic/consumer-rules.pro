# DrawableKt.installApi24InflateDelegates
-keep class androidx.appcompat.widget.ResourceManagerInternal { *; }
-keep class androidx.appcompat.widget.ResourceManagerInternal$InflateDelegate { *; }
-keep class * extends androidx.appcompat.widget.ResourceManagerInternal$InflateDelegate { *; }

# EdgeToEdgeCompat
-keep class androidx.activity.EdgeToEdgeImpl { *; }
-keep class * extends androidx.activity.EdgeToEdgeImpl { *; }
-keep class com.google.android.material.internal.EdgeToEdgeUtils { *; }
