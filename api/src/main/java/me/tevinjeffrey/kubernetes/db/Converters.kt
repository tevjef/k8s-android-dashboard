package me.tevinjeffrey.kubernetes.db

import androidx.annotation.Nullable
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
  val moshi: Moshi = Moshi.Builder().build()

  @TypeConverter
  @Nullable
  fun stringSetToString(json: String): Set<String>? {
    val type = Types.newParameterizedType(Set::class.java, String::class.java)
    val adapter = moshi.adapter<Set<String>>(type)
    return adapter.fromJson(json)
  }

  @TypeConverter
  fun stringFromStringSet(value: Set<String>?): String {
    val type = Types.newParameterizedType(Set::class.java, String::class.java)
    val adapter = moshi.adapter<Set<String>>(type)
    return adapter.toJson(value)
  }

  @TypeConverter
  @Nullable
  fun workloadSortKeyToInt(key: WorkloadSortKey): Int? = key.ordinal


  @TypeConverter
  fun intToWorkloadSortKey(value: Int?): WorkloadSortKey =  WorkloadSortKey.values()[value!!]

  @TypeConverter
  @Nullable
  fun workloadSortKeyListToString(keys: Set<WorkloadType>?): String  {
    return keys
        .orEmpty()
        .map { it.ordinal }
        .joinToString(separator = "|")
  }


  @TypeConverter
  fun stringToWorkloadSortKeySet(value: String?): Set<WorkloadType> {
    return if (value.isNullOrEmpty()) emptySet() else value
        .orEmpty()
        .split("|")
        .map { it.toInt() }
        .map { WorkloadType.values()[it] }
        .toSet()
  }
}