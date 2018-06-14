package com.parkingarmenia.edvardasus.parkingarmenia

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.TextView
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import data.Car
import data.Cars
import data.onItemClickListener
import data.onTopItemChangedListener
import java.text.FieldPosition

class CarListFragment : Fragment() {

    private var mOnItemClickCallback: onItemClickListener? = null
    private var monTopItemChangedCallback: onTopItemChangedListener? = null

    fun dataChanged() {
        mCarsListRecyclerAdapter.dataChanged()
    }

    inner class CarsRecyclerViewAdapter(ctx : Context) : RecyclerView.Adapter<CarsRecyclerViewAdapter.ViewHolder>(){

        private var mCarsList : ArrayList<Car> = Cars.getInstance(ctx).load()
        private var mLayoutInflater : LayoutInflater? = LayoutInflater.from(ctx)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view : View = mLayoutInflater!!.inflate(R.layout.recyclerview_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mCarsList.size
        }

        fun dataChanged() {
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.carSerial.text = mCarsList[position].mSerial
        }


       inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {

           init {
               val cardView : CardView = view.findViewById(R.id.carCard)
               cardView.setOnClickListener(this)
           }

           override fun onClick(view: View?) {
               mOnItemClickCallback?.onCardClicked(view!!, adapterPosition)
           }

            val carSerial: TextView = view.findViewById(R.id.txtCarSerial)

        }
    }

    private lateinit var mCarsListRecyclerAdapter : CarsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCarsListRecyclerAdapter = CarsRecyclerViewAdapter(activity)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mOnItemClickCallback = activity as onItemClickListener
        monTopItemChangedCallback = activity as onTopItemChangedListener
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v : View = inflater!!.inflate(R.layout.car_list_fragment, container, false)

        val recyclerView : RecyclerView = v.findViewById(R.id.recyclerCards)
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false)
        layoutManager.setPostLayoutListener(object : CarouselZoomPostLayoutListener() {})
        layoutManager.addOnItemSelectionListener {
            // here we get the position of the top element: IT
            monTopItemChangedCallback?.onTopItemChanged(it)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(object : CenterScrollListener() {})
        recyclerView.adapter = mCarsListRecyclerAdapter
        return v
    }

    override fun onDetach() {
        super.onDetach()
        mOnItemClickCallback = null
        monTopItemChangedCallback = null
    }

}