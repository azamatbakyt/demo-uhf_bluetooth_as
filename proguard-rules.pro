# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\soft\android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn com.imagealgorithmlab.**
-dontwarn com.hsm.**
-dontwarn com.rscja.deviceapi.**
-keep class com.hsm.** {*; }
-keep class com.rscja.deviceapi.** {*; }
-keep interface com.rscja.ht.ui.JavascriptInterface {*;}
-keep class * implements com.rscja.ht.ui.JavascriptInterface {*;}

# 蓝牙固件升级
-keep class no.nordicsemi.android.dfu.** { *; }

# Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models.
# Modify this rule to fit the structure of your app.
-keepclassmembers class com.yourcompany.models.** {
  *;