package br.com.meiadois.decole.presentation.user.education

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.education.viewmodel.EducationHomeTopViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.EducationHomeTopViewModelFactory
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

        setBarChart(50, 40)
        setPieChart(200, 50)
        setAvarageChart(80.6f)

    }

    private fun setAvarageChart(avarage: Float) {
        text_metrics_avarage.text = avarage.toString()
    }

    private fun setBarChart(followers: Int, following: Int) {

        var axValue = 0
        if (followers >= following) {
            axValue = followers + 10
        } else {
            axValue = following + 10
        }
        val first_bar: ArrayList<BarEntry> = ArrayList();
        first_bar.add(BarEntry(0f, followers.toFloat()))
        val second_bar: ArrayList<BarEntry> = ArrayList();
        second_bar.add(BarEntry(2f, following.toFloat()))


        val dataSetFirstBar = BarDataSet(first_bar, "")
        val dataSetSecondBar = BarDataSet(second_bar, "")

        // set bar label
        val legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        val footerEntries = arrayListOf<LegendEntry>()

        footerEntries.add(
            LegendEntry(
                "Seguidores",
                Legend.LegendForm.SQUARE,
                8f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
        )
        footerEntries.add(
            LegendEntry(
                "Seguindo",
                Legend.LegendForm.SQUARE,
                8f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)
            )
        )

        legend.setCustom(footerEntries)
        legend.setXOffset(-2f)
        legend.setYEntrySpace(0f)
        legend.setTextSize(9f)

        dataSetFirstBar.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        dataSetFirstBar.valueTextColor = Color.BLACK
        dataSetFirstBar.valueTextSize = 10f

        dataSetSecondBar.color =
            ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)
        dataSetSecondBar.valueTextColor = Color.BLACK
        dataSetSecondBar.valueTextSize = 10f

        barChartView.data = BarData(dataSetFirstBar, dataSetSecondBar)
        barChartView.setFitBars(true)
        barChartView.getDescription().setEnabled(false);

        barChartView.animateY(1000)
        barChartView.axisLeft.isEnabled = false
        barChartView.axisRight.isEnabled = false
        barChartView.xAxis.isEnabled = false
        barChartView.axisLeft.labelCount = 5
        barChartView.axisLeft.axisMaximum = axValue.toFloat()
        barChartView.axisLeft.axisMinimum = 0f
        barChartView.xAxis.setGranularity(1f)
        barChartView.xAxis.setGranularityEnabled(true)
        barChartView.xAxis.setCenterAxisLabels(true)
        barChartView.xAxis.setDrawGridLines(false)
        barChartView.xAxis.textSize = 9f

        barChartView.setDoubleTapToZoomEnabled(false)
    }

    //sector chart
    private fun setPieChart(with: Int, without: Int) {

        val visitors: ArrayList<PieEntry> = ArrayList();
        visitors.add(PieEntry(with.toFloat(), ""))
        visitors.add(PieEntry(without.toFloat(), ""))

        val colors: ArrayList<Int> = ArrayList();
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker))

        val barPie = PieDataSet(visitors, "")
        barPie.colors = colors
        barPie.valueTextColor = Color.WHITE
        barPie.valueTextSize = 10f
        barPie.isDrawIconsEnabled
        barPie.setDrawIcons(false)

        val legend = pieChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        val footerEntries = arrayListOf<LegendEntry>()

        footerEntries.add(
            LegendEntry(
                "Com #",
                Legend.LegendForm.SQUARE,
                10f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
        )
        footerEntries.add(
            LegendEntry(
                "Sem #",
                Legend.LegendForm.SQUARE,
                10f,
                8f,
                null,
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarker)
            )
        )

        legend.setCustom(footerEntries)
        legend.textColor = Color.BLACK
        legend.setTextSize(10f)
        legend.setYOffset(4f)
        legend.setXOffset(5f)

        pieChartView.data = PieData(barPie)
        pieChartView.description.text = "Post"
        pieChartView.description.textSize = 10f
        pieChartView.description.xOffset = 30f

//      pieChartView.animate()
        pieChartView.setRotationEnabled(false)
    }
}
