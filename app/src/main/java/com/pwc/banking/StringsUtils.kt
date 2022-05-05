package com.pwc.banking

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

fun String.changeDateFormatFromAnother(): String? {
    val inputFormat: DateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val outputFormat: DateFormat =
        SimpleDateFormat("MMM dd  yyyy")
    var resultDate: String? = ""
    try {
        resultDate = outputFormat.format(inputFormat.parse(this))
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return resultDate
}


fun String.maskString() : String?{
    return replace(".(?=.{4})".toRegex(), "\u00b7")
}

fun String.roundup(value:Double) : String?{
    return format("%.2f", value)
}

fun String.method(str: String?): String? {
    var str = str
    if (str != null && str.length > 0 ) {
        str = str.substring(0, str.length - 1)
    }
    return str
}

fun RecyclerView.addItemDecorationWithoutLastDivider(orientation:Int) {

    addItemDecoration(object :
        DividerItemDecoration(context,orientation) {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            if (parent.getChildAdapterPosition(view) == state.itemCount - 1)
                outRect.setEmpty()
            else
                super.getItemOffsets(outRect, view, parent, state)
        }
    })
}