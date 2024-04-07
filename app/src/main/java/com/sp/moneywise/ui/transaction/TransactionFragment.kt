package com.sp.moneywise.ui.transaction

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sp.moneywise.BaseApplication
import com.sp.moneywise.databinding.BottomSheetLayoutBinding
import com.sp.moneywise.databinding.FragmentTransactionBinding
import com.sp.moneywise.datalayer.model.Category
import com.sp.moneywise.datalayer.model.Converters
import com.sp.moneywise.datalayer.model.TransactionEntity
import com.sp.moneywise.datalayer.model.TransactionType
import com.sp.moneywise.toFormattedString
import com.sp.moneywise.ui.dashboard.ItemTransactionBaseObservable
import java.util.Calendar
import kotlin.random.Random


class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var transactionViewModel: TransactionViewModel? = null
    var transactionBaseObservable: TransactionBaseObservable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        transactionBaseObservable = TransactionBaseObservable()
        _binding?.data = transactionBaseObservable
        val root: View = binding.root
        val myAppDatabase = (activity?.applicationContext as? BaseApplication)?.myAppDatabase
        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(myAppDatabase)
        )[TransactionViewModel::class.java]

        _binding?.floatingCallback = View.OnClickListener {
            val bottomSheetDialog = BottomSheetDialog(it.context)
            val bottomSheetLayoutBinding: BottomSheetLayoutBinding =
                DataBindingUtil.inflate(
                    inflater,
                    com.sp.moneywise.R.layout.bottom_sheet_layout,
                    container,
                    false
                )

            val adapter: ArrayAdapter<Category> =
                ArrayAdapter<Category>(
                    it.context,
                    R.layout.simple_spinner_dropdown_item,
                    Category.entries.toList()
                )

            bottomSheetLayoutBinding.spinnerCategory.adapter = adapter

            val bottomSheetLayoutBaseObservable = BottomSheetLayoutBaseObservable()
            bottomSheetLayoutBinding.data = bottomSheetLayoutBaseObservable
            bottomSheetLayoutBinding.clickCallback = View.OnClickListener {
                transactionViewModel?.addTransaction(
                    TransactionEntity(
                        transactionId = Random.nextInt(0, 1000000000),
                        title = bottomSheetLayoutBinding.etCategory.text.toString(),
                        description = bottomSheetLayoutBinding.etDescription.text.toString(),
                        type = if (bottomSheetLayoutBinding.incomeRadioButton.isChecked) TransactionType.INCOME else TransactionType.EXPENSE,
                        amount = bottomSheetLayoutBinding.editTextAmount.text.toString(),
                        categoriesCategoryId = Converters.fromCategoryType(bottomSheetLayoutBinding.spinnerCategory.selectedItem.toString()),
                        categoryName = bottomSheetLayoutBinding.etCategory.text.toString(),
                        dateTimestamp = Calendar.getInstance().time
                    )

                )
                bottomSheetDialog.dismiss()
            }

            bottomSheetLayoutBinding.radioGroup.setOnCheckedChangeListener { _: RadioGroup, i: Int ->

                when (i) {
                    com.sp.moneywise.R.id.expenseRadioButton -> {
                        bottomSheetLayoutBaseObservable.background = android.R.color.holo_red_light
                    }

                    com.sp.moneywise.R.id.incomeRadioButton -> {
                        bottomSheetLayoutBaseObservable.background =
                            android.R.color.holo_green_light

                    }
                }

            }

            bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)
            bottomSheetDialog.show()

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionViewModel?.text?.observe(viewLifecycleOwner) {
            _binding?.textHome?.text = it
        }

        transactionViewModel?.transactionLiveData?.observe(viewLifecycleOwner) { newList ->
            if (newList.isEmpty()) {
                transactionBaseObservable?.adapter?.addItem(
                    ItemTransactionBaseObservable(
                        title = "Add your transaction",
                        amount = "",
                        description = "",
                        time = ",",
                        transactionType = TransactionType.EXPENSE,
                        category = Category.MISC
                    )
                )
            } else {
                transactionBaseObservable?.adapter?.clear()
                val list = newList.map {
                    ItemTransactionBaseObservable(
                        title = it.title,
                        amount = it.amount,
                        description = it.description,
                        time = it.dateTimestamp.toFormattedString("MM-dd-yyyy"),
                        transactionType = it.type,
                        category = it.categoriesCategoryId
                    )
                }
                transactionBaseObservable?.addItems(list)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}