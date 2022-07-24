package com.app.payseracurrencyexchange.ui

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.app.payseracurrencyexchange.data.repository.ExchangeRatesRepository
import com.app.payseracurrencyexchange.ui.base.ViewModelFactory
import com.app.payseracurrencyexchange.ui.exchangeRate.ExchangeRateViewModel
import android.view.View
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.payseracurrencyexchange.R
import com.app.payseracurrencyexchange.data.room.AccountsEntity
import com.app.payseracurrencyexchange.databinding.ActivityMainBinding
import com.app.payseracurrencyexchange.ui.adapter.AccountsAdapter
import com.app.payseracurrencyexchange.ui.customViews.CurrencyInput
import com.app.payseracurrencyexchange.ui.customViews.CustomDialog.Companion.showSuccessDialog
import com.app.payseracurrencyexchange.utils.Utils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var accountList = ArrayList<String>()

    /**
     * variable for recyclerview [AccountsAdapter] for setting accounts.
     */
    private var accountsAdapter = AccountsAdapter()

    var adapter: ArrayAdapter<String>? = null
    var userAccountsAdapter: ArrayAdapter<String>? = null

    /**
     * Lazily initialize for [ExchangeRateViewModel].
     */
    private val viewModel: ExchangeRateViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(ExchangeRatesRepository(), application)
        ).get(ExchangeRateViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)

          with(binding) {

            /**
             * Setting the adapter for RecyclerView
             */
            rvCurrency.setHasFixedSize(true)
            rvCurrency.adapter = accountsAdapter
            rvCurrency.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

            lifecycleOwner = this@MainActivity
            vm = viewModel
            executePendingBindings()


            /**
             * Change [EditText] format for input Amount
             */

            val textWatcher = CurrencyInput(edAmount).apply {
                moneyPrefix = ""
                separator = ','
                decimal = '.'
            }

            edAmount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().isNotEmpty()) {
                        val format: NumberFormat = NumberFormat.getInstance()
                        val amount: Double = format.parse(p0.toString()).toDouble()
                        //set input Amount in viewModel
                        viewModel.amount.set(amount)
                        binding.submitButton.isEnabled = viewModel.validate()
                    } else {
                        viewModel.amount.set(0.0)
                        binding.submitButton.isEnabled = viewModel.validate()
                    }
                }
            })
            edAmount.addTextChangedListener(textWatcher)

            /**
             * Listener for selecting sellCurrency [AutoCompleteTextView] drop down items
             */
            sellAutoCompleteTextView.onItemClickListener =
                OnItemClickListener { parent, _, position, _ ->
                    viewModel.fromCurrency.set(parent.getItemAtPosition(position) as String)


                    // Database call, check and assign balance from selected currency account using coroutines

                    lifecycleScope.launch {
                        viewModel.getAccountsById(parent.getItemAtPosition(position) as String)
                            .observe(this@MainActivity, { accounts ->
                                viewModel.remainingBalance.set(accounts.accountBalance.toLong())

                                if (accounts.accountBalance < viewModel.amount.get()) {
                                    Snackbar.make(
                                        binding.root,
                                        "Not Enough Balance",
                                        Snackbar.LENGTH_LONG
                                    )
                                        .show()
                                }
                            })
                    }
                    submitButton.isEnabled = viewModel.validate()
                }


            /**
             * Listener for selecting receive Currency [AutoCompleteTextView] drop down items
             */
            receiveCompleteTextView.onItemClickListener =
                OnItemClickListener { parent, _, position, _ ->
                    viewModel.toCurrency.set(parent.getItemAtPosition(position) as String)
                    binding.submitButton.isEnabled = viewModel.validate()
                }


            /**
             * Submit button for amount conversion, only enable if conditions met.
             */
            submitButton.setOnClickListener(View.OnClickListener {

                viewModel.commision.set(
                    if (sharedPref.getInt(getString(R.string.number_of_conversion), 0) > 5) {
                        viewModel.amount.get() * (0.7 / 100)
                    } else {
                        0.0
                    }
                )
                viewModel.balance.set(false)
                //using viewModel call convertAmount service
                viewModel.getConvertAmount(
                    viewModel.toCurrency.get(),
                    viewModel.fromCurrency.get(),
                    viewModel.amount.get()
                )
                saveExchangeCount()
            })


            /**
             *  [FloatingActionButton] for calling Dialog,
             */
            binding.addAccountFab.setOnClickListener { view ->
                showAddAccountDialog()
            }

        }

        setDefaultAccount(sharedPref)
        initObserver()

    }

    /** Observer function for all LiveData objects */
    private fun initObserver() {

        //convert currency observer for Api
        viewModel.convertedAmount.observe(this, {
            binding.tvReceiveAmount.text = getString(R.string.tv_add_balance, it.result.toString())
            binding.tvReceiveAmount.setTextColor(Color.GREEN)

            //show Dialog for conversion success message with commission
            showSuccessDialog(
                this@MainActivity,
                """${it.query.amount}${" "}${it.query.from}""",
                """${it.result}${" "}${it.query.to}""",
                String.format("%.2f", viewModel.commision.get())
            )

            //check if the selected currency account exist
         lifecycleScope.launch {
                viewModel.checkKey(it.query.to).cancellable().collect {checkStatus->
                    if (checkStatus == 1) {
                        viewModel.updateMinus(it.query.from, if(viewModel.balance.get()) 0.0 else viewModel.amount.get().toLong()+viewModel.commision.get())
                        viewModel.updateSum(it.query.to, if(viewModel.balance.get()) 0  else it.result.toLong())
                        Snackbar.make(
                            binding.root,
                            it.result.toString() + " amount added to your account " + it.query.to,
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                        this.cancel()
                    }
                }
            }

            viewModel.getAll()

        })


        // Get Currency symbols observer for Api
        viewModel.symbolList.observe(this, {
            adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line, it
            )
            binding.sellAutoCompleteTextView.setAdapter(userAccountsAdapter)
            binding.receiveCompleteTextView.setAdapter(adapter)
        })

        //Get userAccounts observer for Database
        viewModel.userAccounts.observe(this, { accounts ->
            accountsAdapter.setAccounts(accounts)
            accountList.clear()

            // add List<AccountsEntity to List<String> for adapter
            for (item in accounts) {
                accountList.add(item.accountName)
            }
            userAccountsAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line, accountList
            )
        })

    }

    /**
     *  Function for commission count for currency converter,
     */
    private fun saveExchangeCount() {
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(
                getString(R.string.number_of_conversion),
                sharedPref.getInt(getString(R.string.number_of_conversion), 0) + 1
            )
                .commit()
        }
    }


    /**
     *  Dialog for Adding new Currency Accounts̵̵̵̵̵̵
     */
    private fun showAddAccountDialog() {
        val dialog = Dialog(this@MainActivity)
        with(dialog) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.add_account_dialog)
            var selectedAccount = ""
            var selectCurrency =
                this.findViewById<AutoCompleteTextView>(R.id.sellAutoCompleteTextView)
            var btnNext =
                this.findViewById<Button>(R.id.btn_done)
            var btnClose =
                this.findViewById<Button>(R.id.btnClose)
            selectCurrency.setAdapter(adapter)
            selectCurrency.onItemClickListener =
                OnItemClickListener { parent, _, position, _ ->
                    selectedAccount = parent.getItemAtPosition(position) as String
                    btnNext.isEnabled = !selectedAccount.isNullOrEmpty()
                }

            btnNext.setOnClickListener { view ->

                lifecycleScope.launch {

                    viewModel.checkKey(selectedAccount).cancellable().collect { check ->

                        if (check == 0) {
                            viewModel.insert(
                                AccountsEntity(
                                    selectedAccount,
                                    0.0
                                )
                            )
                            var formattedMesaage =
                                getString(R.string.message_account_created, selectedAccount)
                            viewModel.getAll()
                            viewModel.balance.set(true)
                            //dialog.window.decorView
                            showSnakbar( dialog.window!!.decorView, formattedMesaage)
                        } else {
                            var formattedMesaage =
                                getString(R.string.message_account_exist, selectedAccount)
                            showSnakbar(dialog.window!!.decorView, formattedMesaage)
                        }
                        this.cancel()
                    }
                }

                // dismiss()
            }
            btnClose.setOnClickListener {
                dismiss()
            }
            show()
        }
    }


    /**
     *  For setting Default account in our case it's EURO with amount 1000.0 , can be change from UTILS
     */
   private fun setDefaultAccount(sharedPref: SharedPreferences) {
        try {
            with(sharedPref.edit()) {
                if (sharedPref.getLong(getString(R.string.user_account_balance), 0) == 0L) {
                    putLong(
                        getString(R.string.user_account_balance),
                        Utils.ACCOUNT_BALANCE.toLong()
                    )
                    apply()
                    viewModel.insert(
                        AccountsEntity(
                            Utils.defaultAccount,
                            Utils.ACCOUNT_BALANCE
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("Preference Exception", e.message.toString())
            sharedPref.edit().putLong(getString(R.string.user_account_balance), 0L)
        }

    }

    fun showSnakbar(decorView: View, message: String) {
        Snackbar.make(decorView.rootView, message, Snackbar.LENGTH_LONG)
            .show()
    }

}