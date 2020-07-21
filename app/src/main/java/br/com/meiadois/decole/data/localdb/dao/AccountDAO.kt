package br.com.meiadois.decole.data.localdb.dao

import androidx.room.*
import br.com.meiadois.decole.data.localdb.entity.Account

@Dao
interface AccountDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(accounts: List<Account>)

    @Query("SELECT * FROM Account")
    suspend fun getUserAccounts(): List<Account>

    @Query("SELECT * FROM Account WHERE channelName = :channelName LIMIT 1")
    suspend fun getUserAccountByChannel(channelName: String): Account

    @Delete
    suspend fun deleteAccount(account: Account)
}