package br.com.meiadois.decole

import android.app.Application
import android.content.Intent
import android.util.Log
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.RequestInterceptor
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.data.repository.*
import br.com.meiadois.decole.presentation.auth.viewmodel.LoginViewModelFactory
import br.com.meiadois.decole.presentation.auth.viewmodel.RegisterViewModelFactory
import br.com.meiadois.decole.presentation.errorhandler.ErrorHandlerActivity
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModelFactory
import br.com.meiadois.decole.presentation.splash.viewmodel.SplashViewModelFactory
import br.com.meiadois.decole.presentation.user.account.viewmodel.factory.*
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.*
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.factory.PartnerBottomSheetViewModelFactory
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.factory.PartnershipCompanyProfileViewModelFactory
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.factory.PartnershipHomeBottomViewModelFactory
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.factory.PartnershipHomeTopViewModelFactory
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeInfoViewModelFactory
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeSlideViewModelFactory
import br.com.meiadois.decole.service.LogOutService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class DecoleApplication() : Application(), KodeinAware {

    private lateinit var crashlytics: FirebaseCrashlytics

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
        bind() from singleton { AccountMenuViewModelFactory(instance()) }
        bind() from singleton { AccountCompanyViewModelFactory(instance(), instance(), instance(), instance()) }
        bind() from singleton { AccountProfileViewModelFactory(instance()) }
        bind() from singleton { PartnershipHomeTopViewModelFactory(instance()) }
        bind() from singleton { EducationHomeTopViewModelFactory(instance(), instance()) }
        bind() from singleton { FinishedRouteViewModelFactory(instance(), instance()) }
        bind() from singleton { WelcomeSlideViewModelFactory(instance()) }
        bind() from singleton { WelcomeInfoViewModelFactory(instance()) }
        bind() from singleton { PartnershipCompanyProfileViewModelFactory(instance(), instance()) }
        bind() from singleton { AccountChangePasswordViewModelFactory(instance()) }
        bind() from singleton { PwRecoveryViewModelFactory(instance()) }
        bind() from singleton { PartnerBottomSheetViewModelFactory(instance()) }
        bind() from singleton { SplashViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        crashlytics = Firebase.crashlytics

        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            logException(ex)
            handleUncaughtException()
        }
    }

    private fun logException(ex: Throwable) {
        var stackTrace = ""
        for (item in ex.stackTrace)
            stackTrace += "\n\t${item.className} | ${item.methodName} | ${item.lineNumber}"
        Log.i("GlobalErrorH", "Exception:\n" +
                "message: ${ex.message ?: "no message"}\n" +
                "cause: ${ex.cause ?: "no cause"}\n" +
                "stackTrace: $stackTrace")

        crashlytics.recordException(ex)
    }

    private fun handleUncaughtException() {
        Intent(this, ErrorHandlerActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }
}