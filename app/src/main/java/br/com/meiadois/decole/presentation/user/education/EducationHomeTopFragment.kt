package br.com.meiadois.decole.presentation.user.education

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.education.viewmodel.EducationHomeTopViewModel
import br.com.meiadois.decole.presentation.user.education.viewmodel.factory.EducationHomeTopViewModelFactory
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
        barra2.add(BarEntry(1f,seguindos.toFloat()))


        val barDataSetbarra1 = BarDataSet(barra1,"seguidores")
        val barDataSetbarra2 = BarDataSet(barra2,"seguidores")

        barDataSetbarra1.color=Color.RED
        barDataSetbarra1.valueTextColor=Color.BLACK
        barDataSetbarra1.valueTextSize=10f

        barDataSetbarra2.color=Color.BLUE
        barDataSetbarra2.valueTextColor=Color.BLACK
        barDataSetbarra2.valueTextSize=10f

        barChartView.data = BarData(barDataSetbarra1,barDataSetbarra2)
        barChartView.setFitBars(true)
        /*val description2 =
            Description()
        description2.text = ""
        //barChartView.setDescription(description2)*/

        barChartView.getDescription().setEnabled(false);

        barChartView.animateY(2000)
        barChartView.axisLeft.isEnabled=false
        barChartView.axisRight.isEnabled=false
        barChartView.xAxis.isEnabled=false
        barChartView.axisLeft.labelCount=5
        barChartView.axisLeft.axisMaximum=axValue.toFloat()
        barChartView.axisLeft.axisMinimum=0f





    }
}
