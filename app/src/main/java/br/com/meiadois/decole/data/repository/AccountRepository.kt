package br.com.meiadois.decole.data.repository

import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.model.Account
import br.com.meiadois.decole.data.localdb.entity.Account as AccountEntity
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.AccountRequest
import br.com.meiadois.decole.data.network.response.AccountResponse
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AccountRepository(
    private val prefs: PreferenceProvider,
    private val client: DecoleClient,
    private val db: AppDatabase
) : RequestHandler() {

    private val userAccounts = MutableLiveData<List<Account>>()

    init {
        userAccounts.observeForever {
            saveAccounts(it)
        }
    }

    private suspend fun fetchAccounts(): List<AccountResponse> {
        val response =  callClient { client.getUserAccounts() }
        userAccounts.postValue(response.parseToAccountModelList())
        return response
    }

    private fun saveAccounts(accounts: List<Account>) {
        prefs.saveLastAccountFetch(System.currentTimeMillis())
        Coroutines.io {
            db.getAccountDao().upsert(accounts.toAccountEntityList())
        }
    }

    private fun upsertAccount(account: AccountEntity){
        Coroutines.io {
            db.getAccountDao().upsert(account)
        }
    }

    suspend fun getUserAccounts(): List<AccountEntity> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastAccountFetch()
            if (lastFetch == 0L || Date(lastFetch).isFetchNeeded())
                return@withContext fetchAccounts().parseToAccountEntityList()
            db.getAccountDao().getUserAccounts()
        }
    }

    suspend fun insertUserAccount(account: Account): AccountResponse {
        val response =  callClient {
            client.insertUserAccount(AccountRequest(account.userName, account.channelName))
        }
        account.id = response.id
        upsertAccount(account.toAccountEntity())
        return response
    }

    suspend fun updateUserAccount(account: Account): AccountResponse {
        val response = callClient {
            client.updateUserAccount(account.channelName, AccountRequest(account.userName))
        }
        upsertAccount(account.toAccountEntity())
        return response
    }

    suspend fun deleteUserAccount(account: Account) {
        client.deleteUserAccount(account.channelName)
        db.getAccountDao().deleteAccount(account.toAccountEntity())
    }
}