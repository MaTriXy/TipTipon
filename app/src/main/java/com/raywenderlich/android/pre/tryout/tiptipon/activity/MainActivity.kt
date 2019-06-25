/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.pre.tryout.tiptipon.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.raywenderlich.android.pre.tryout.tiptipon.R
import com.raywenderlich.android.pre.tryout.tiptipon.viewModel.TipTiponViewModel
import com.raywenderlich.android.pre.tryout.tiptipon.viewModel.TipTiponViewModel.Companion.DEFAULT_MAX_TIP_PERCENTAGE
import com.raywenderlich.android.pre.tryout.tiptipon.viewModel.TipTiponViewModel.Companion.DEFAULT_MIN_NUM_PEOPLE
import com.raywenderlich.android.pre.tryout.tiptipon.viewModel.TipTiponViewModel.Companion.DEFAULT_MIN_TIP_PERCENTAGE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var tipTiponViewModel: TipTiponViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a ViewModel the first time onCreate is called
        //Later if needed we get the same instance created by our first activity
        tipTiponViewModel = ViewModelProviders.of(this).get(TipTiponViewModel::class.java)

        setContentView(R.layout.activity_main)
        updateUI()

        increasePeopleButton.setOnClickListener {
            increaseNumOfPeople()
        }
        decreasePeopleButton.setOnClickListener {
            decreaseNumOfPeople()
        }

        increaseTipButton.setOnClickListener {
            increaseTipPercentage()
        }

        decreaseTipButton.setOnClickListener {
            decreaseTipPercentage()
        }
        input_bill.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT,
                EditorInfo.IME_ACTION_GO,
                EditorInfo.IME_ACTION_SEND,
                EditorInfo.IME_ACTION_DONE -> {
                    tipTiponViewModel.currentBillLiveData.value = v.text.toString().toDouble()
                    false
                }
                else -> false
            }
        }

        input_tip_percentage.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT,
                EditorInfo.IME_ACTION_GO,
                EditorInfo.IME_ACTION_SEND,
                EditorInfo.IME_ACTION_DONE -> {
                    try {
                        val num = v.text.toString().toInt()
                        if (num in DEFAULT_MIN_TIP_PERCENTAGE..DEFAULT_MAX_TIP_PERCENTAGE) {
                            tipTiponViewModel.currentTipPercentageLiveData.value = num
                            return@setOnEditorActionListener false
                        }
                    } catch (ignored: Exception) {
                    }//even though we ignored the exception we have a piece of code that will run in case
                    //there was an exception or in case the num is not in out Tip range.
                    v.text = tipTiponViewModel.currentTipPercentage.toString()
                    Toast.makeText(this@MainActivity, R.string.tip_out_of_bounds, Toast.LENGTH_LONG).show()
                    true

                }
                else -> false
            }
        }

        input_people_amount.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT,
                EditorInfo.IME_ACTION_GO,
                EditorInfo.IME_ACTION_SEND,
                EditorInfo.IME_ACTION_DONE -> {
                    tipTiponViewModel.currentNumOfPeopleLiveData.value = v.text.toString().toInt()
                    false
                }
                else -> false
            }
        }

        // Create the observer which updates the UI.
        val billObserver = Observer<Double> { newBill ->
            // Update the UI, in this case, a TextView.
            tipTiponViewModel.currentBill = newBill
            updateUI()
        }

        // Create the observer which updates the UI.
        val tipObserver = Observer<Int> { newTip ->
            // Update the UI, in this case, a TextView.
            tipTiponViewModel.currentTipPercentage = newTip
            updateUI()
        }

        // Create the observer which updates the UI.
        val peopleObserver = Observer<Int> { newAmount ->
            // Update the UI, in this case, a TextView.
            tipTiponViewModel.currentNumOfPeople = newAmount
            updateUI()
        }


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        tipTiponViewModel.currentBillLiveData.observe(this, billObserver)
        tipTiponViewModel.currentTipPercentageLiveData.observe(this, tipObserver)
        tipTiponViewModel.currentNumOfPeopleLiveData.observe(this, peopleObserver)


    }


    private fun updateBillText() {
        input_bill.setText(tipTiponViewModel.currentBill.toString())
    }

    private fun updateUI() {
        val billBeforeTip = if (input_bill.text.isNullOrEmpty()) {
            0.0
        } else {
            input_bill.text.toString().toDouble()
        }

        tipTiponViewModel.currentBill = billBeforeTip
        tipTiponViewModel.currentTipAmount =
            ((tipTiponViewModel.currentTipPercentage / 100.00) * billBeforeTip) / tipTiponViewModel.currentNumOfPeople

        tipTiponViewModel.currentTotalBill =
            ((tipTiponViewModel.currentTipPercentage + DEFAULT_MAX_TIP_PERCENTAGE) / 100.00) * tipTiponViewModel.currentBill

        tipTiponViewModel.currentTotalPerPerson =
            tipTiponViewModel.currentTotalBill / tipTiponViewModel.currentNumOfPeople


        total_per_person.text = tipTiponViewModel.currentTotalPerPerson.toString()
        tip_per_person.text = tipTiponViewModel.currentTipAmount.toString()
        updateBillText()
        updateTipText()
        updatePeopleText()
    }

    private fun increaseTipPercentage() {
        if (tipTiponViewModel.currentTipPercentage < DEFAULT_MAX_TIP_PERCENTAGE) {
            tipTiponViewModel.currentTipPercentage++
            updateUI()
        }
    }

    private fun decreaseTipPercentage() {
        if (tipTiponViewModel.currentTipPercentage > DEFAULT_MIN_TIP_PERCENTAGE) {
            tipTiponViewModel.currentTipPercentage--
            updateUI()
        }
    }

    private fun updateTipText() {
        input_tip_percentage.setText(tipTiponViewModel.currentTipPercentage.toString())
    }

    private fun increaseNumOfPeople() {
        tipTiponViewModel.currentNumOfPeople++
        updateUI()
    }

    private fun decreaseNumOfPeople() {
        if (tipTiponViewModel.currentNumOfPeople > DEFAULT_MIN_NUM_PEOPLE) {
            tipTiponViewModel.currentNumOfPeople--
            updateUI()
        }
    }

    private fun updatePeopleText() {
        input_people_amount.setText(getString(R.string.num_people_text, tipTiponViewModel.currentNumOfPeople))
    }

}
