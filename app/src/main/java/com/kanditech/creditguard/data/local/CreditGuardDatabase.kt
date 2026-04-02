package com.kanditech.creditguard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kanditech.creditguard.data.local.dao.CreditGuardDao
import com.kanditech.creditguard.data.local.entities.*

@Database(
    entities = [
        CustomerEntity::class,
        GroupEntity::class,
        CollectionEntity::class,
        PendingCollectionEntity::class,
        WalletTransactionEntity::class,
        CreditorEntity::class,
        CreditDetailsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CreditGuardDatabase : RoomDatabase() {
    abstract fun creditGuardDao(): CreditGuardDao
}
