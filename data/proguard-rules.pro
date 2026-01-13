# Proguard rules for Room and Kotlinx Serialization

# Keep all classes annotated with @Entity
-keep @androidx.room.Entity class * { *; }

# Keep all classes annotated with @Serializable
-keep @kotlinx.serialization.Serializable class * { *; }
-keep class com.marcusrunge.mydefcon.data.bases.RepositoryBase { *; }
-keep class com.marcusrunge.mydefcon.data.bases.MyDefconDatabase { *; }
-keep class com.marcusrunge.mydefcon.data.entities.CheckItem { *; }