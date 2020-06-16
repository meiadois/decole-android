package br.com.meiadois.decole

import android.app.Application
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.RequestInterceptor
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.data.repository.*
import br.com.meiadois.decole.presentation.auth.viewmodel.LoginViewModelFactory
import br.com.meiadois.decole.presentation.auth.viewmodel.RegisterViewModelFactory
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModelFactory
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModelFactory
import br.com.meiadois.decole.presentation.user.account.viewmodel.ChangePasswordViewModelFactory
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.*
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.*
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeInfoViewModelFactory
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeSlideViewModelFactory
import br.com.meiadois.decole.service.LogOutService
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
        bind() from singleton { LogOutService(instance(), instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton { AccountRepository(instance(), instance(), instance()) }
        bind() from singleton { RouteRepository(instance(), instance(), instance()) }
        bind() from singleton { LessonRepository(instance(), instance(), instance()) }
        bind() from singleton { CompanyRepository(instance(), instance(),instance()) }
        bind() from singleton { CepRepository(instance()) }
        bind() from singleton { SegmentRepository(instance(), instance(),instance()) }
        bind() from singleton { StepRepository(instance()) }
        bind() from singleton { MetricsRepository(instance()) }
        bind() from singleton { LoginViewModelFactory(instance()) }
        bind() from singleton { RegisterViewModelFactory(instance()) }
        bind() from singleton { RouteListViewModelFactory(instance(), instance()) }
        bind() from singleton { RouteDetailsViewModelFactory(instance(), instance()) }
        bind() from singleton { StartInteractiveModeViewModelFactory(instance()) }
        bind() from singleton { PartnershipHomeBottomViewModelFactory(instance()) }
        bind() from singleton { AccountViewModelFactory(instance(), instance(), instance(), instance(), instance(), instance()) }
        bind() from singleton { PartnershipHomeTopViewModelFactory(instance()) }
        bind() from singleton { EducationHomeTopViewModelFactory(instance(), instance()) }
        bind() from singleton { FinishedRouteViewModelFactory(instance(), instance()) }
        bind() from singleton { WelcomeSlideViewModelFactory(instance()) }
        bind() from singleton { WelcomeInfoViewModelFactory(instance()) }
        bind() from singleton { PartnershipCompanyProfileViewModelFactory(instance(), instance())}
        bind() from singleton { ChangePasswordViewModelFactory(instance())}
        bind() from singleton { PwRecoveryViewModelFactory(instance()) }
        bind() from singleton { PartnerBottomSheetViewModelFactory(instance()) }
    }
}