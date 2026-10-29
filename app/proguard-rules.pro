# Manu Remote Control — reglas de ofuscación.
# La app no usa reflexión sobre modelos propios, pero Room/Hilt necesitan sus reglas por defecto.
-keep class com.manu.remotecontrol.data.local.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn org.jetbrains.annotations.**
