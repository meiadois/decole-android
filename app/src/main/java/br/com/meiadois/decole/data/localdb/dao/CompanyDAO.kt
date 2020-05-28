package br.com.meiadois.decole.data.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.MyCompany


@Dao
interface CompanyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(myCompany: MyCompany)

    @Query("SELECT * FROM MyCompany")
    suspend fun getUserCompanyLocal(): MyCompany
}