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

        setBarChart(50,40)
        setPieChart(400,50)
        publicar.text="80.6";


    }
        private fun test(){
            publicar.text="80.6";
        }
    private  fun setBarChart(seguidores:Int,seguindos:Int){

        var axValue =0;
        if(seguidores>=seguindos){
            axValue=seguidores+10
        }else{
            axValue=seguindos+10
        }
        val barra1 :ArrayList<BarEntry> = ArrayList();
        barra1.add(BarEntry(0f,seguidores.toFloat()))
        val barra2 :ArrayList<BarEntry> = ArrayList();
        barra2.add(BarEntry(2f,seguindos.toFloat()))


        val barDataSetbarra1 = BarDataSet(barra1,"")
        val barDataSetbarra2 = BarDataSet(barra2,"")

        // set bar label
        var legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()

        legenedEntries.add(LegendEntry("Seguidores", Legend.LegendForm.SQUARE, 8f, 8f, null,ContextCompat.getColor(requireContext(),R.color.colorPrimary)))
        legenedEntries.add(LegendEntry("Seguindo", Legend.LegendForm.SQUARE, 8f, 8f, null,ContextCompat.getColor(requireContext(),R.color.colorPrimaryDarker)))

        legend.setCustom(legenedEntries)
        //legend.setYOffset(4f)
        legend.setXOffset(-2f)
        legend.setYEntrySpace(0f)
        legend.setTextSize(9f)

        barDataSetbarra1.color=ContextCompat.getColor(requireContext(),R.color.colorPrimary)

        barDataSetbarra1.valueTextColor=Color.BLACK
        barDataSetbarra1.valueTextSize=10f
        /*barDataSetbarra1.setDrawIcons(false)
        barDataSetbarra1.setDrawValues(false)*/



        barDataSetbarra2.color=ContextCompat.getColor(requireContext(),R.color.colorPrimaryDarker)
        barDataSetbarra2.valueTextColor=Color.BLACK
        barDataSetbarra2.valueTextSize=10f

        barChartView.data = BarData(barDataSetbarra1,barDataSetbarra2)

        barChartView.setFitBars(true)
        /*val description2 =
            Description()
        description2.text = ""
        //barChartView.setDescription(description2)*/

        barChartView.getDescription().setEnabled(false);
        /*barChartView.description.text="seguidoresxSeguidos"
        barChartView.description.textSize=10f*/

        barChartView.animateY(1000)
        //barChartView.xAxis.setGranularity(0f)
        barChartView.axisLeft.isEnabled=false
        barChartView.axisRight.isEnabled=false
        barChartView.xAxis.isEnabled=false
        barChartView.axisLeft.labelCount=5
        barChartView.axisLeft.axisMaximum=axValue.toFloat()
        barChartView.axisLeft.axisMinimum=0f
        barChartView.xAxis.setGranularity(1f)
        barChartView.xAxis.setGranularityEnabled(true)
        barChartView.xAxis.setCenterAxisLabels(true)
        barChartView.xAxis.setDrawGridLines(false)
        barChartView.xAxis.textSize = 9f

        barChartView.setDoubleTapToZoomEnabled(false)

        //barChartView.xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
//        var xAxisValues = ArrayList<String>()
//        xAxisValues.add("Seguidores")
//        xAxisValues.add("Segundo")
//        barChartView.xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))

//        barChartView.xAxis.setLabelCount(12)
//        barChartView.xAxis.mAxisMaximum = 12f
//        barChartView.xAxis.setCenterAxisLabels(true)
//        barChartView.xAxis.setAvoidFirstLastClipping(true)
//        barChartView.xAxis.spaceMin = 4f
//        barChartView.xAxis.spaceMax = 4f





    }
            // grafico pizza
    private fun setPieChart(postCom:Int,postSem:Int){

        val visitors :ArrayList<PieEntry> = ArrayList();
        visitors.add(PieEntry(postCom.toFloat(),""))
        visitors.add(PieEntry(postSem.toFloat(),""))


        val cor :ArrayList<Int> = ArrayList();
        cor.add(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
        cor.add(ContextCompat.getColor(requireContext(),R.color.colorPrimaryDarker))


        val barPie= PieDataSet(visitors,"")
        barPie.colors=cor
        barPie.valueTextColor=Color.BLACK
        barPie.valueTextSize=10f
        barPie.isDrawIconsEnabled
        barPie.setDrawIcons(false)
        var legend = pieChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()

        legenedEntries.add(LegendEntry("Com #", Legend.LegendForm.SQUARE, 10f, 8f, null,ContextCompat.getColor(requireContext(),R.color.colorPrimary)))
        legenedEntries.add(LegendEntry("Sem #", Legend.LegendForm.SQUARE, 10f, 8f, null,ContextCompat.getColor(requireContext(),R.color.colorPrimaryDarker)))

        legend.setCustom(legenedEntries)
        legend.setTextSize(10f)
                //legenda
        legend.setYOffset(4f)
        legend.setXOffset(5f)


        pieChartView.data = PieData(barPie)

       // pieChartView.description.isEnabled=false
        pieChartView.description.text="Post"
        pieChartView.description.textSize=10f
        pieChartView.description.xOffset=30f
        //pieChartView.description.yOffset=5f


        pieChartView.animate()



    }
}
