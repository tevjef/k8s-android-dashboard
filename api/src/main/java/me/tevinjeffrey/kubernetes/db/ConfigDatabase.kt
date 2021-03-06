package me.tevinjeffrey.kubernetes.db

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.tevinjeffrey.kubernetes.api.R
import timber.log.Timber

@Database(entities = [Config::class, Cluster::class], version = 1)
abstract class ConfigDatabase : RoomDatabase() {
  abstract fun configDao(): ConfigDao

  companion object {

    @Volatile
    private var INSTANCE: ConfigDatabase? = null

    fun getInstance(context: Context): ConfigDatabase =
        INSTANCE ?: synchronized(this) {
          INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

    private fun buildDatabase(context: Context) =
        Room.databaseBuilder(context.applicationContext,
            ConfigDatabase::class.java, "k8s-config")
            .addCallback(object : Callback() {
              override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                ioThread {
                  getInstance(context).configDao().apply {
                    prepopulate(
                        Config(1, 1),
                        Cluster(
                            clusterId = 1,
                            name = context.getString(R.string.default_cluster_name))
                    )
                  }
                }
              }
            })
            .fallbackToDestructiveMigration()
            .build()
  }
}