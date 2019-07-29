package com.rektapps.assaulttimer.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rektapps.assaulttimer.model.entity.Assault
import com.rektapps.assaulttimer.model.entity.ManualNotification
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.model.enums.Region
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface ManualNotificationDao {
    @Insert
    fun create(manualNotification: ManualNotification): Completable

    @Query("DELETE FROM ManualNotification WHERE assaultId = :assaultId")
    fun deleteByAssaultId(assaultId: Long):Completable

    @Query("SELECT * FROM ManualNotification WHERE assaultId = :assaultId")
    fun getByAssaultId(assaultId: Long): Single<ManualNotification>

    @Query("SELECT * FROM Assault WHERE region= :region AND type = :type AND id in (SELECT assaultId FROM ManualNotification) ORDER BY start")
    fun getSelectedAssault(region: Region, type: AssaultType): Single<List<Assault>>
}