package br.com.meiadois.decole.data.localdb.dao

import androidx.room.*
import br.com.meiadois.decole.data.localdb.entity.Company
import br.com.meiadois.decole.data.localdb.entity.MyCompany

@Dao
interface CompanyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(myCompany: Company)

    @Query("SELECT * FROM Company")
    suspend fun getUserCompany(): Company

    @Transaction
    @Query("SELECT * FROM Company")
    suspend fun getUserCompanyWithSegment(): MyCompany
}