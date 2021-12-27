package com.cellstudio.cellstream.ui.utilities

import android.content.Context
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.R
import com.cellstudio.cellstream.ui.presentations.action.adapters.ActionAdapter
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel

object DialogUtils {
    fun showDialog(
        context: Context,
        items: List<ActionModel>,
    ) {
        val builder = AlertDialog.Builder(context).setView(R.layout.fragment_action)

        val dialog = builder.create()
        dialog.show()

        val listView = dialog.findViewById<RecyclerView>(R.id.rvSingleSelectionMain)

        val adapter = ActionAdapter(items)
        adapter.listener = object: ActionAdapter.Listener {
            override fun onClick(model: ActionModel) {}
        }
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        listView?.layoutManager = layoutManager
        listView?.adapter = adapter

//        val arrayAdapter = ArrayAdapter<String>(context, R.layout.item_action)
//        arrayAdapter.addAll(items)
//
//        listView?.adapter = arrayAdapter
//        listView?.choiceMode = AbsListView.CHOICE_MODE_SINGLE

//        for (select in selectedIndex) {
//            listView.setItemChecked(select, true)
//        }
//
//        selectedIndex.minOrNull()?.let {
//            listView.setSelection(it)
//        }
//
//        //  var lastSelectedIndex = if(selectedIndex.isNotEmpty()) selectedIndex.first() else -1
//
//        dialog.setOnDismissListener {
//            dismissCallback.invoke()
//        }
//
//        listView.setOnItemClickListener { _, _, which, _ ->
//            //  lastSelectedIndex = which
//            if (realShowApply) {
//                if (!isMultiSelect) {
//                    listView.setItemChecked(which, true)
//                }
//            } else {
//                callback.invoke(listOf(which))
//                dialog.dismiss()
//            }
//        }
//        if (realShowApply) {
//            applyButton.setOnClickListener {
//                val list = ArrayList<Int>()
//                for (index in 0 until listView.count) {
//                    if (listView.checkedItemPositions[index])
//                        list.add(index)
//                }
//                callback.invoke(list)
//                dialog.dismiss()
//            }
//            cancelButton.setOnClickListener {
//                dialog.dismiss()
//            }
//        }
    }
}