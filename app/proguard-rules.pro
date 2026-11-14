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

-dontwarn org.slf4j.impl.StaticLoggerBinder

# Room 2.2.5 (via Glance/WorkManager) creates this database through reflection.
# Its bundled keep rule does not preserve the constructor with current R8.
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
}

# WorkManager 2.7.1 creates every work request's InputMerger (OverwritingInputMerger
# by default) through Class.newInstance(). Its bundled `-keep class * extends InputMerger`
# keeps only the class, so R8 full mode strips the constructor, every Glance
# SessionWorker then fails before running and widgets stay on "Loading...".
-keep class * extends androidx.work.InputMerger {
    public <init>();
}

# Glance records each GlanceAppWidget's class name to map widgets to their receiver.
# Keep the name stable across releases so lookups like getGlanceIds(HijriWidget::class.java)
# keep matching what an earlier version stored.
-keepnames class * extends androidx.glance.appwidget.GlanceAppWidget
