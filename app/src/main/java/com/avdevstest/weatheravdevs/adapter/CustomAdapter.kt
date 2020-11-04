package com.example.admin.kotlinvideorecyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.avdevstest.weatheravdevs.R
import com.avdevstest.weatheravdevs.databinding.CategoryBinding
import com.example.admin.kotlinvideorecyclerview.viewmodel.DateTemperatureViewModel

class CustomAdapter(
    private val context: Context,
    private val arrylist: ArrayList<DateTemperatureViewModel>
) :
    RecyclerView.Adapter<CustomAdapter.CustomView>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomView {

        val layoutInflater = LayoutInflater.from(parent.context)

        val categoryBinding: CategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.innerlayout, parent, false)

        return CustomView(categoryBinding)

    }

    override fun getItemCount(): Int {

        return arrylist.size
    }

    override fun onBindViewHolder(holder: CustomView, position: Int) {

        val categoryViewModel = arrylist[position]
        holder.bind(categoryViewModel)

    }

    class CustomView(val categoryBinding: CategoryBinding) :
        RecyclerView.ViewHolder(categoryBinding.root) {
        fun bind(categoryViewModel: DateTemperatureViewModel) {

            this.categoryBinding.categorymodel = categoryViewModel
            categoryBinding.executePendingBindings()


        }


    }


}