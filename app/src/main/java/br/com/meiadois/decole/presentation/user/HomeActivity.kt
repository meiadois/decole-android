package br.com.meiadois.decole.presentation.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.account.AccountActivity
import br.com.meiadois.decole.presentation.user.education.EducationHomeTopFragment
import br.com.meiadois.decole.presentation.user.education.RouteListFragment
import br.com.meiadois.decole.presentation.user.partnership.PartnershipHomeBottomFragment
import br.com.meiadois.decole.presentation.user.partnership.PartnershipHomeTopFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        Top fragment structure, temporarily removed
//        val newFragment: Fragment =
//            EducationHomeTopFragment()
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.top_container, newFragment)
//            .addToBackStack(null)
//            .commit()

        bottom_view_pager.adapter = UserHomeBottomSlideAdapter(supportFragmentManager)
//        Top fragment structure, temporarily removed
//        bottom_view_pager.addOnPageChangeListener(CustomOnPageChangeListener(supportFragmentManager))

        tab_layout.setupWithViewPager(bottom_view_pager)

        account_configuration_button.setOnClickListener {
            Intent(this, AccountActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    inner class UserHomeBottomSlideAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> RouteListFragment()
                else -> PartnershipHomeBottomFragment()
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.welcome_education_title) else getString(R.string.welcome_partnership_title)
        }
    }
//    Top fragment structure, temporarily removed
//    class CustomOnPageChangeListener(private val fm: FragmentManager) :
//        ViewPager.OnPageChangeListener {
//        override fun onPageScrollStateChanged(state: Int) {
//        }
//
//        override fun onPageScrolled(
//            position: Int,
//            positionOffset: Float,
//            positionOffsetPixels: Int
//        ) {
//        }
//
//        override fun onPageSelected(position: Int) {
//            val newFragment: Fragment =
//                if (position == 0) EducationHomeTopFragment() else PartnershipHomeTopFragment()
//            val transaction: FragmentTransaction = fm.beginTransaction()
//
//            transaction.replace(R.id.top_container, newFragment)
//            transaction.addToBackStack(null)
//
//            transaction.commit()
//        }
//
//    }

}