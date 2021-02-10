package pl.org.seva.myapplication.main.extension

import android.view.View

operator fun View.invoke(l: (View) -> Unit) = onClick(l)

infix fun View.onClick(l: (View) -> Unit) = setOnClickListener(l)
