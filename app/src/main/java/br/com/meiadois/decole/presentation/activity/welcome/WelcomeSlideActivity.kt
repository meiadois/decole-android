package br.com.meiadois.decole.presentation.activity.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.activity.instagram.InstagramIntroActivity
import br.com.meiadois.decole.presentation.fragment.welcome.EducationSlideFragment
import br.com.meiadois.decole.presentation.fragment.welcome.MarketplaceSlideFragment
import br.com.meiadois.decole.presentation.fragment.welcome.PartnershipSlideFragment
import kotlinx.android.synthetic.main.activity_welcome_slide.*

class WelcomeSlideActivity : AppCompatActivity() {

    var pageSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_slide)
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
            confirmSelection()
        }
    }

    private fun confirmSelection() {
        if (pageSelected == 0) {
            val intent = Intent(this, InstagramIntroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun selectPage(position: Int) {

        btn_next.isEnabled = position == 0

        pageSelected = position
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
