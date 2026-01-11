# Firebase Realtime Database
# This is necessary because the Realtime Database uses reflection to work with your model objects.
-keepclassmembers class com.google.firebase.database.** { *; }
-keepclassmembers class com.google.firebase.database.GenericTypeIndicator { *; }
-keep class * extends com.google.firebase.database.GenericTypeIndicator { *; }
# Cloud Firestore & Realtime Database Model Classes
# If you use custom model classes with Firestore or Realtime Database, you need to
# prevent them from being obfuscated or removed. You can do this by adding the
# @Keep annotation to your model classes, or by adding keep rules here.
#
# For example:
# -keep class com.yourpackage.YourModelClass { *; }
-keep class com.marcusrunge.mydefcon.firebase.documents.DefconGroup { *; }
-keep class com.marcusrunge.mydefcon.firebase.documents.Follower { *; }