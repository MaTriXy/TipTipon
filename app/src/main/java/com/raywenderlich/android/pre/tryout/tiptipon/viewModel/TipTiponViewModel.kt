package com.raywenderlich.android.pre.tryout.tiptipon.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.round

class TipTiponViewModel : ViewModel() {

    companion object {
        const val DEFAULT_MAX_TIP_PERCENTAGE: Int = 100
        const val DEFAULT_MIN_TIP_PERCENTAGE: Int = 0
        const val DEFAULT_TIP_PERCENTAGE: Int = 20//default for tip is 20%
        const val DEFAULT_NUM_PEOPLE: Int = 4//default number of people is 4
        const val DEFAULT_MIN_NUM_PEOPLE: Int = 1//we need to have at least 1 people to pay the tab :)
    }

        var currentNumOfPeople = DEFAULT_NUM_PEOPLE
    var currentTipPercentage = DEFAULT_TIP_PERCENTAGE
    var currentBill = 0.0
    set(value) {
        field = round(value * 100) / 100
    }

    var currentTipAmount = 0.0
        set(value) {
            field = round(value * 100) / 100
        }

    var currentTotalBill = 0.0
        set(value) {
            field = round(value * 100) / 100
        }
    var currentTotalPerPerson = 0.0
        set(value) {
            field = round(value * 100) / 100
        }


    val currentNumOfPeopleLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentTipPercentageLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentBillLiveData: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }


}