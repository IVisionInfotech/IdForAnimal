# =====================
# Preserve Core Android Components
# =====================
-keep public class * extends android.app.Application { *; }
-keep public class * extends android.app.Activity { *; }
-keep public class * extends android.app.Service { *; }
-keep public class * extends android.content.BroadcastReceiver { *; }
-keep public class * extends android.content.ContentProvider { *; }
-keep public class * extends android.view.View { *; }

# Preserve all ViewBinding classes
-keep class * implements androidx.viewbinding.ViewBinding { *; }

# Preserve classes annotated with @Keep
-keep @androidx.annotation.Keep class * { *; }
-keep @android.annotation.Keep class * { *; }

# =====================
# AndroidX and Support Libraries
# =====================
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.constraintlayout.** { *; }
-keep class androidx.recyclerview.** { *; }
-keep class androidx.cardview.** { *; }
-keep class androidx.legacy.** { *; }
-keep class androidx.multidex.** { *; }

# =====================
# Third-Party Libraries
# =====================

# SDP (Scalable Dimension for Android)
-keep class com.intuit.sdp.** { *; }

# RoundedImageView
-keep class com.makeramen.** { *; }

# Play Core (In-app updates, Asset Delivery)
-keep class com.google.android.play.core.** { *; }

# OtpView (Custom OTP input view)
-keep class com.github.aabhasr1.** { *; }

# SmartMaterialSpinner (Custom Spinner)
-keep class com.github.chivorns.** { *; }

# =====================
# Retrofit (API Calls)
# =====================
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# =====================
# Gson (JSON serialization)
# =====================
-keep class com.google.gson.** { *; }

# =====================
# OkHttp Logging Interceptor
# =====================
-keep class okhttp3.logging.** { *; }

# =====================
# Google Play Services Location
# =====================
-keep class com.google.android.gms.location.** { *; }

# =====================
# Glide (Image Loading)
# =====================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# =====================
# Realm Database
# =====================
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-keep class io.realm.rx.Keep
-keep @io.realm.rx.Keep class *

# Keep custom model classes
-keep class com.idforanimal.model.** { *; }

# =====================
# Apache POI (Excel Generation)
# =====================
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.apache.commons.** { *; }
-keep class org.apache.commons.math3.** { *; }
-keep class org.apache.commons.compress.** { *; }

# Keep Microsoft Office document-related schemas
-keep class schemasMicrosoftComVml.** { *; }
-keep class com.microsoft.schemas.** { *; }
-keep class org.openxmlformats.schemas.** { *; }

# Preserve Java XML parsing classes
-keep class javax.xml.stream.** { *; }
-keep class javax.xml.parsers.** { *; }
-keep class org.xml.sax.** { *; }
-keep class org.w3c.dom.** { *; }

# Prevent obfuscation of classes used via reflection
-keepnames class org.apache.poi.** { *; }
-keepclassmembers class org.apache.poi.** { *; }
-keepclassmembers class * {
    @org.apache.xmlbeans.XmlObject *;
}

# Ignore warnings from missing optional dependencies
-dontwarn org.apache.xmlbeans.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.poi.**
-dontwarn com.microsoft.schemas.**
-dontwarn javax.xml.**

# =====================
# Other Common Keep Rules
# =====================

# Prevent obfuscation of enums (for Retrofit, Gson, etc.)
-keepclassmembers class * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable { *; }

# Keep Serializables
-keep class * implements java.io.Serializable { *; }

# =====================
# Debugging and Optimization
# =====================

# Reduce APK size by removing logs in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Enable R8 compatibility mode (if needed)
# Uncomment this if ProGuard causes issues with optimizations
# android.enableR8.fullMode=true
