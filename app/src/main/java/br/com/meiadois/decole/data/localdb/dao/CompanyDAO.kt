package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.meiadois.decole.data.model.Company

@Dao
interface CompanyDAO {
    @Query("Select * FROM Company Where ")
    fun findCompanyByFilter(): LiveData<List<Company>>
}

