package br.com.meiadois.decole.presentation.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityWelcomeSlideBinding
import br.com.meiadois.decole.presentation.user.HomeActivity
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeSlideViewModel
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeSlideViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.activity_welcome_slide.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class WelcomeSlideActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: WelcomeSlideViewModelFactory by instance()

    private lateinit var mViewModel: WelcomeSlideViewModel

    private var pageSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityWelcomeSlideBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_welcome_slide)

        mViewModel = ViewModelProvider(this, factory).get(WelcomeSlideViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
        }
    }

    override fun onResume() {
        super.onResume()
        view_pager.adapter = WelcomeSlideAdapter(supportFragmentManager)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                selectPage(position)
            }

        })

        btn_next.setOnClickListener {
            if (btn_next.text == getString(R.string.next)) {
                view_pager.currentItem = pageSelected + 1
            } else callNextActivity()

        }
    }

    private fun callNextActivity() {
        Coroutines.io {
            try {
                mViewModel.introduce()
                Intent(this, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            } catch (ex: NoInternetException) {
                scroll_slide.longSnackbar(getString(R.string.no_internet_connection_error_message)) { snackbar ->
                    snackbar.setAction(getString(R.string.reload)) {
                        snackbar.dismiss()
                    }
                }
            } catch (ex: Exception) {
                scroll_slide.longSnackbar(getString(R.string.sendding_data_failed_error_message)) { snackbar ->
                    snackbar.setAction(getString(R.string.reload)) {
                        snackbar.dismiss()
                    }
                }
            }
        }
    }

    private fun selectPage(position: Int) {
        pageSelected = position
        when (position) {
            2 -> btn_next.text = getString(R.string.start)
            else -> btn_next.text = getString(R.string.next)
        }
    }

    inner class WelcomeSlideAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                1 -> MarketplaceSlideFragment()
                2 -> PartnershipSlideFragment()
                else -> EducationSlideFragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
