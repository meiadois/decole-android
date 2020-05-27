package br.com.meiadois.decole.data.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.Company


@Dao
interface CompanyDAO {
    /*
    @Query("SELECT * FROM Company WHERE ")
    fun findCompanyByFilter(): LiveData<List<Company>>
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(company: Company)

    @Query("SELECT * FROM Company WHERE userId = :userId")
    suspend fun getUserCompanyLocal(userId: Int): Company
}