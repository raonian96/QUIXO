package com.raonstudio.quixo

enum class PieceSymbol(val str: String? = null) {
    O("O"), X("X"), BLANK()
}

class Piece {
    var symbol = PieceSymbol.BLANK
}