package me.tevinjeffrey.kubernetes.home.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import me.tevinjeffrey.kubernetes.db.Cluster

class SpinnerAdapter(
    ctx: Context,
    private val spinnerViewRes: Int,
    private val dropDownResource: Int,
    val itemList: MutableList<Cluster>)
  : ArrayAdapter<Cluster>(ctx, spinnerViewRes, itemList) {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
      createItemView(parent, position, spinnerViewRes)

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) =
      createItemView(parent, position, dropDownResource)

  private fun createItemView(parent: ViewGroup, position: Int, itemRes: Int): TextView {
    val inflater = LayoutInflater.from(context)
    val row = inflater.inflate(itemRes, parent, false) as TextView
    row.text = itemList[position].name
    return row
  }
}
