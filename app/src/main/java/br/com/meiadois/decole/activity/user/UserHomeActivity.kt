package br.com.meiadois.decole.activity.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import br.com.meiadois.decole.R
import br.com.meiadois.decole.fragments.user.EducationHomeBottomFragment
import br.com.meiadois.decole.fragments.user.EducationHomeTopFragment
import br.com.meiadois.decole.fragments.user.PartnershipHomeBottomFragment
import br.com.meiadois.decole.fragments.user.PartnershipHomeTopFragment
import kotlinx.android.synthetic.main.activity_user_home.*

class UserHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val newFragment: Fragment = EducationHomeTopFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.top_container, newFragment)
            .addToBackStack(null)
            .commit()

        bottom_view_pager.adapter = UserHomeBottomSlideAdapter(supportFragmentManager)
        bottom_view_pager.addOnPageChangeListener(CustomOnPageChangeListener(supportFragmentManager))

        tab_layout.setupWithViewPager(bottom_view_pager)
    }

    override fun onBackPressed() {
        finish()
    }

    inner class UserHomeBottomSlideAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                1 -> PartnershipHomeBottomFragment()
                else -> EducationHomeBottomFragment()
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) "Educação" else "Parcerias"
        }
    }

    class CustomOnPageChangeListener(private val fm: FragmentManager) :
        ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            val newFragment: Fragment =
                if (position == 0) EducationHomeTopFragment() else PartnershipHomeTopFragment()
            val transaction: FragmentTransaction = fm.beginTransaction()

            transaction.replace(R.id.top_container, newFragment)
            transaction.addToBackStack(null)

            transaction.commit()
        }

    }

}
