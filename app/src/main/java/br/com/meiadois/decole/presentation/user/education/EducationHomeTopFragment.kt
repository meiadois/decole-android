package br.com.meiadois.decole.presentation.user.education

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.education.viewmodel.EducationHomeTopViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.EducationHomeTopViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.fragment_education_home_top.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class EducationHomeTopFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: EducationHomeTopViewModelFactory by instance<EducationHomeTopViewModelFactory>()
    private lateinit var viewModel: EducationHomeTopViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_education_home_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(EducationHomeTopViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        init()
    }
    private fun init(){
        scrollview_education?.visibility = View.GONE
        container_education_metrics?.visibility = View.GONE
        setProgressBarVisibility(true)
        setMetricsData()
    }

    private fun teste() {
        setBarChart(50f, 40f)
        setPieChart(200, 50)
        setAvarageChart(80.6f)
        scrollview_education.visibility = View.VISIBLE
    }

    private fun setAvarageChart(avarage: Float) {
        text_metrics_avarage.text = avarage.toString()
    }

    private fun setBarChart(followers: Float, following: Float) {

        val axValue = if (followers >= following) {
            followers + 10
        } else {
            following + 10
        }
        val firstBar: ArrayList<BarEntry> = ArrayList()
        firstBar.add(BarEntry(0f, followers))
        val secondBar: ArrayList<BarEntry> = ArrayList()
        secondBar.add(BarEntry(2f, following))

        val dataSetFirstBar = BarDataSet(firstBar, "")
        val dataSetSecondBar = BarDataSet(secondBar, "")

        //subtitle config view
        val legend = barChartView.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val footerEntries = arrayListOf<LegendEntry>()

        footerEntries.add(
            LegendEntry(
                context?.getString(R.string.followers_label),
                Legend.LegendForm.SQUARE,
                8f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
        )
        footerEntries.add(
            LegendEntry(
                context?.getString(R.string.following_label),
                Legend.LegendForm.SQUARE,
                8f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)
            )
        )

        legend.setCustom(footerEntries)
        legend.xOffset = -2f
        legend.yEntrySpace = 0f
        legend.textSize = 9f

        dataSetFirstBar.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        dataSetFirstBar.valueTextColor = Color.BLACK
        dataSetFirstBar.valueTextSize = 10f

        dataSetSecondBar.color = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)
        dataSetSecondBar.valueTextColor = Color.BLACK
        dataSetSecondBar.valueTextSize = 10f
        //end subtitle
        //chart config view
        barChartView.data = BarData(dataSetFirstBar, dataSetSecondBar)
        barChartView.setFitBars(true)
        barChartView.description.isEnabled = false

        barChartView.animateY(1000)
        barChartView.axisLeft.isEnabled = false
        barChartView.axisRight.isEnabled = false
        barChartView.axisLeft.labelCount = 5
        barChartView.axisLeft.axisMaximum = axValue
        barChartView.axisLeft.axisMinimum = 0f
        barChartView.xAxis.isEnabled = false
        barChartView.xAxis.isGranularityEnabled = true
        barChartView.xAxis.granularity = 1f
        barChartView.xAxis.setCenterAxisLabels(true)
        barChartView.xAxis.setDrawGridLines(false)
        barChartView.xAxis.textSize = 9f

        barChartView.isDoubleTapToZoomEnabled = false
        //end config
    }

    private fun setPieChart(with: Int, without: Int) {

        val visitors: ArrayList<PieEntry> = ArrayList()
        visitors.add(PieEntry(with.toFloat(), ""))
        visitors.add(PieEntry(without.toFloat(), ""))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker))

        val barPie = PieDataSet(visitors, "")
        barPie.colors = colors
        barPie.valueTextColor = Color.WHITE
        barPie.valueTextSize = 10f
        barPie.isDrawIconsEnabled
        barPie.setDrawIcons(false)

        val legend = pieChartView.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val footerEntries = arrayListOf<LegendEntry>()

        footerEntries.add(
            LegendEntry(
                context?.getString(R.string.has_hashtag_label),
                Legend.LegendForm.SQUARE,
                10f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
        )
        footerEntries.add(
            LegendEntry(
                context?.getString(R.string.no_has_hashtag_label),
                Legend.LegendForm.SQUARE,
                10f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)
            )
        )

        legend.setCustom(footerEntries)
        legend.textColor = Color.BLACK
        legend.textSize = 10f
        legend.yOffset = 4f
        legend.xOffset = 5f

        pieChartView.data = PieData(barPie)
        pieChartView.description.text = context?.getString(R.string.post_label)
        pieChartView.description.textSize = 10f
        pieChartView.description.xOffset = 30f
        pieChartView.animate()
        pieChartView.isRotationEnabled = false
    }

    private fun setMetricsData() {
        Coroutines.main {
            if (viewModel.userHasInstagram()) {
                try {
                    val metric = viewModel.getUserMetrics()
                    setBarChart(
                        metric.followers.value,
                        metric.following.value
                    )
                    setPieChart(
                        metric.posts_with_hashtags.value.toInt(),
                        metric.publications.value.toInt() - metric.posts_with_hashtags.value.toInt()
                    )
                    setAvarageChart(
                        metric.mean_of_comments.value
                    )
                    scrollview_education.visibility = View.VISIBLE
                } catch (ex: ClientException) {
                    setMetricsNotFound(ex)
                } catch (ex: Exception) {
                    Log.i("Fragment_top_exception", ex.message ?: "")
                } finally {
                    setProgressBarVisibility(false)
                }
            } else {
                setProgressBarVisibility(false)
                container_education_metrics?.visibility = View.VISIBLE
                text_no_registered?.visibility = View.VISIBLE
            }
        }
    }

    private fun setProgressBarVisibility(visible: Boolean) {
        progress_bar?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setMetricsNotFound(ex: ClientException) {
        setProgressBarVisibility(false)
        scrollview_education?.visibility = View.GONE
        container_education_metrics?.visibility = View.VISIBLE
        when (ex.code) {
            500 -> text_no_found?.visibility = View.VISIBLE
        }
    }
}
