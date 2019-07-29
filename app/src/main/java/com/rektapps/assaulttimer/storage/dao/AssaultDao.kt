package com.rektapps.assaulttimer.storage.dao

import androidx.room.*
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.joda.time.DateTime

@Dao
abstract class AssaultDao {

    @Query("SELECT * FROM Assault WHERE region = :region AND type = :type ORDER BY start")
    abstract fun getAssaults(region: Region, type: AssaultType): Flowable<List<Assault>>

    @Query("SELECT * FROM Assault WHERE id = :id")
    abstract fun getAssaultById(id: Long): Single<Assault>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addAll(list: List<Assault>):Completable

    @Delete
    protected abstract fun deleteAll(list: List<Assault>):Completable

    @Query("DELETE FROM Assault where region = :region AND type = :type")
    abstract fun dropTable(region: Region, type: AssaultType): Completable

    @Query("SELECT * From Assault WHERE `end` <= :dateTime AND region = :region AND type = :type")
    abstract fun getAssaultsWithLessOrEqualEndDateTime(
        dateTime: DateTime,
        region: Region,
        type: AssaultType
    ): Single<List<Assault>>


    @Query("SELECT `end` FROM Assault WHERE start = (SELECT MAX(start) FROM Assault WHERE region = :region AND type = :type)")
    abstract fun getLastAssaultEndDateTime(region: Region, type: AssaultType): Single<DateTime>

    @Query("SELECT EXISTS (SELECT * FROM Assault WHERE region = :region AND type = :type )")
    abstract fun hasAssaults(region: Region, type: AssaultType):Single<Boolean>

    @Transaction
    open fun deleteAndAddTransaction(assaultsToRemove: List<Assault>, assaultsToAdd: List<Assault>) {
        deleteAll(assaultsToRemove).andThen(addAll(assaultsToAdd)).blockingAwait()
    }

}