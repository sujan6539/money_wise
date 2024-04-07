package com.sp.moneywise.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.sp.moneywise.BaseApplication
import com.sp.moneywise.databinding.FragmentDashboardBinding
import com.sp.moneywise.datalayer.model.Category
import com.sp.moneywise.datalayer.model.TransactionType
import com.sp.moneywise.getEndOfWeekTimestamp
import com.sp.moneywise.getStartOfWeekTimestamp
import com.sp.moneywise.toFormattedString


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var chart: LineChart? = null


    private var baseObservable: DashboardBaseObservable? = null
    var dashboardViewModel: DashboardViewModel? = null
    var incomeBaseObservable: ItemDashboardBaseObservable? = null
    var expenseBaseObservable: ItemDashboardBaseObservable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val myAppDatabase = (activity?.applicationContext as? BaseApplication)?.myAppDatabase
        dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(myAppDatabase)
        )[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        baseObservable = DashboardBaseObservable()
        binding.data = baseObservable
        chart = binding.chartLinechart
        incomeBaseObservable =
            ItemDashboardBaseObservable("Income", android.R.color.holo_green_light, true)
        expenseBaseObservable =
            ItemDashboardBaseObservable("Expense", android.R.color.holo_red_light, false)
        binding.incomeData = incomeBaseObservable
        binding.expenseData = expenseBaseObservable

        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardViewModel?.transactionLiveData?.observe(viewLifecycleOwner) { newList ->
            if(newList.isNotEmpty()) {
                baseObservable?.adapter?.clear()
                val startOfWeekTimestamp = getStartOfWeekTimestamp()
                val endOfWeekTimestamp = getEndOfWeekTimestamp()
                val list = newList.filter { transaction ->
                    transaction.dateTimestamp.time in startOfWeekTimestamp..endOfWeekTimestamp
                }.map {
                    ItemTransactionBaseObservable(
                        title = it.title,
                        amount = it.amount,
                        description = it.description,
                        time = it.dateTimestamp.toFormattedString("MM-dd-yyyy"),
                        transactionType = it.type,
                        category = it.categoriesCategoryId
                    )
                }
                baseObservable?.addItems(list)
            }else{
                //TODO create a different observable and add it here
                 baseObservable?.adapter?.addItem(
                    ItemTransactionBaseObservable(
                        title = "No recent transaction",
                        amount = "",
                        description = "",
                        time = ",",
                        transactionType = TransactionType.EXPENSE,
                        category = Category.MISC
                    )
                )
            }

        }

        dashboardViewModel?.getMonthSummary(TransactionType.INCOME)?.observe(viewLifecycleOwner) {
            incomeBaseObservable?.totalValue = it?:"0"
        }

        dashboardViewModel?.getMonthSummary(TransactionType.EXPENSE)?.observe(viewLifecycleOwner) {
            expenseBaseObservable?.totalValue = it?:"0"
        }

        dashboardViewModel?.getAccountBalance()?.observe(viewLifecycleOwner) {
            var balance = 0.0
            it.forEach { summary ->
                when (summary.type) {
                    // Handle any other types here if needed
                    TransactionType.INCOME -> balance += summary.totalAmount
                    TransactionType.EXPENSE -> balance -= summary.totalAmount
                }
            }
            baseObservable?.accountBalance = balance.toString()
        }
        setupChart()

    }

    private fun setupChart() {

        // no description text
        chart?.description?.isEnabled = false
        chart?.setBackgroundColor(Color.WHITE)

        // enable touch gestures
        chart?.setTouchEnabled(true)
        chart?.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        chart?.isDragEnabled = false
        chart?.setScaleEnabled(false)

        chart?.axisLeft?.setDrawGridLines(false)
        chart?.xAxis?.setDrawGridLines(false)
        chart?.axisRight?.setDrawGridLines(false)

        chart?.setGridBackgroundColor(Color.WHITE)
        chart?.isHighlightPerDragEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        chart?.setPinchZoom(false)

        // set an alternative background color
//        chart?.setBackgroundColor(Color.LTGRAY)

        chart?.animateX(1500)

        setData(7, 100F)
    }


    private fun setData(count: Int, range: Float) {
        val values1 = ArrayList<Entry>()
        for (i in 0 until count) {
            val `val` = (Math.random() * (range / 2f)).toFloat() + 50
            values1.add(Entry(i.toFloat(), `val`))
        }

        val set1: LineDataSet?

        if (chart?.data != null &&
            chart?.data?.dataSetCount!! > 0
        ) {
            set1 = chart?.data?.getDataSetByIndex(0) as LineDataSet

            set1.values = values1
            chart?.data?.notifyDataChanged()
            chart?.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values1, "Spend Frequency this week")
            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.setDrawValues(false)
//            set1.color = ColorTemplate.getHoloBlue()
//            set1.setCircleColor(Color.WHITE)
            set1.lineWidth = 10f
//            set1.circleRadius = 3f
            set1.fillAlpha = 65
            set1.fillColor = ColorTemplate.getHoloBlue()
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a data object with the data sets
            val data = LineData(set1)
            //data.setValueTextColor(Color.WHITE)
            //data.setValueTextSize(9f)

            // set data
            chart!!.data = data

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}