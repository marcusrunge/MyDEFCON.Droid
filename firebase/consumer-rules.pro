# Generic rules for Firebase Realtime Database
-keepclassmembers class com.google.firebase.database.** { *; }

# Keep custom model classes used with Firebase
-keep class com.marcusrunge.mydefcon.data.models.** { *; }
-keep class com.marcusrunge.mydefcon.firebase.documents.** { *; }

# Keep annotation classes
-keep class com.google.firebase.database.IgnoreExtraProperties
-keep class com.google.firebase.database.PropertyName
-keep class com.google.firebase.database.Exclude

# Keep GenericTypeIndicator and its subclasses
-keep class com.google.firebase.database.GenericTypeIndicator { *; }
-keep class * extends com.google.firebase.database.GenericTypeIndicator { *; }
