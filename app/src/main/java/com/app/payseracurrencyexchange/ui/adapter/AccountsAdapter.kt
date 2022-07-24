package com.app.payseracurrencyexchange.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.payseracurrencyexchange.R
import com.app.payseracurrencyexchange.data.room.AccountsEntity
import com.google.android.material.textview.MaterialTextView

/**
 * This is the Adapter class for [RecyclerView] setting list of user accounts.
 */

class AccountsAdapter : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    private val accountsValue : MutableList<AccountsEntity> = mutableListOf()

  inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      var accountTitle = itemView.findViewById<View>(R.id.accountBalance) as MaterialTextView
      var accountBalance = itemView.findViewById<View>(R.id.accountTitle) as TextView

      fun bind(item : AccountsEntity){
          accountTitle.text = item.accountBalance.toString()
          accountBalance.text = item.accountName
      }
    }

    fun setAccounts(accounts: List<AccountsEntity>){
        accountsValue.clear()
        accountsValue.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_accounts, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {

        holder.bind(accountsValue[position])
    }

    override fun getItemCount(): Int {
       return accountsValue.size
    }


}