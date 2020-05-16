package br.com.meiadois.decole

import android.app.Application
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.RequestInterceptor
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.data.repository.StepRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.auth.viewmodel.LoginViewModelFactory
import br.com.meiadois.decole.presentation.auth.viewmodel.RegisterViewModelFactory
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteDetailsViewModelFactory
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteListViewModelFactory
import br.com.meiadois.decole.presentation.user.education.viewmodel.StartInteractiveModeViewModelFactory
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeBottomViewModelFactory
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipPopUpViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class DecoleApplication() : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@DecoleApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { RequestInterceptor(instance()) }
        bind() from singleton { DecoleClient(instance(), instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton { RouteRepository(instance(), instance(), instance()) }
        bind() from singleton { LessonRepository(instance(), instance(), instance()) }
        bind() from singleton { CompanyRepository(instance()) }
        bind() from singleton { StepRepository(instance()) }
        bind() from singleton { LoginViewModelFactory(instance()) }
        bind() from singleton { RegisterViewModelFactory(instance()) }
        bind() from singleton { RouteListViewModelFactory(instance(), instance()) }
        bind() from singleton { RouteDetailsViewModelFactory(instance(), instance()) }
        bind() from singleton { StartInteractiveModeViewModelFactory(instance()) }
        bind() from singleton { RouteListViewModelFactory(instance()) }
        bind() from singleton { RouteDetailsViewModelFactory(instance()) }
        bind() from singleton { PartnershipHomeBottomViewModelFactory(instance()) }
        bind() from singleton { PartnershipPopUpViewModelFactory(instance()) }
    }
}