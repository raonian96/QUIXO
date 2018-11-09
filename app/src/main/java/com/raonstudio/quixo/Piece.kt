package com.raonstudio.quixo

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View

enum class PieceSymbol(val str: String? = null) {
    O("O"), X("X"), BLANK()
}

class Piece(val view: View, val row: Int, val column: Int): BaseObservable() {
    var symbol = PieceSymbol.BLANK
    @Bindable get
    set(value) {
        field = value
    }
}