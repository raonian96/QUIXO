package com.raonstudio.quixo

import android.databinding.BaseObservable
import android.databinding.Bindable

enum class PieceSymbol {
    O, X;

    operator fun not(): PieceSymbol {
        return when (this) {
            O -> X
            X -> O
        }
    }
}

class Piece : BaseObservable() {
    companion object {
        fun swapLink(piece1: Piece, piece2: Piece, direction: Direction) {
            piece1.linkedGuidLineIDs = piece2.linkedGuidLineIDs
                    .also { piece2.linkedGuidLineIDs = piece1.linkedGuidLineIDs }
            piece1.linkedPieces = piece2.linkedPieces
                    .also { piece2.linkedPieces = piece1.linkedPieces }
            piece2.linkedPieces[direction] = piece1.also { piece1.linkedPieces[direction.opposite] = piece2 }

            piece1.updateLinkFromOther()
            piece2.updateLinkFromOther()
        }
    }

    var symbol: PieceSymbol? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.symbol)
        }

    lateinit var linkedGuidLineIDs: LinkedGuidLineIDs
    var linkedPieces = HashMap<Direction, Piece>()
    var linkedViewId: Int = 0

    fun isBoundaryPiece(): Boolean {
        return with(linkedPieces) {
            get(Direction.TOP_LEFT) == null || get(Direction.BOTTOM_RIGHT) == null
        }
    }

    fun getNextPiece(direction: Direction): Piece? {
        return linkedPieces[direction]
    }

    private fun checkLinearSymbol(direction: Direction, symbol: PieceSymbol): Boolean {
        return linkedPieces[direction]?.let { piece ->
            piece.checkLinearSymbol(direction, symbol).takeIf { symbol == piece.symbol } ?: false
        } ?: true
    }

    private fun checkMakeFiveVertically(symbol: PieceSymbol): Boolean {
        return (checkLinearSymbol(Direction.BOTTOM, symbol) && checkLinearSymbol(Direction.TOP, symbol))
    }

    private fun checkMakeFiveHorizontally(symbol: PieceSymbol): Boolean {
        return (checkLinearSymbol(Direction.LEFT, symbol) && checkLinearSymbol(Direction.RIGHT, symbol))
    }

    fun checkMakeFiveDiagonally(): Boolean {
        return symbol?.let {
            ((checkLinearSymbol(Direction.TOP_LEFT, it) && checkLinearSymbol(Direction.BOTTOM_RIGHT, it))
                    || (checkLinearSymbol(Direction.TOP_RIGHT, it) && checkLinearSymbol(Direction.BOTTOM_LEFT, it)))
        } ?: false
    }

    fun checkMakeFive(): Boolean {
        return symbol?.let {
            checkMakeFiveHorizontally(it) || checkMakeFiveVertically(it)
        } ?: false
    }

    private fun updateLinkFromOther() {
        linkedPieces.forEach {
            it.value.linkedPieces[it.key.opposite] = this
        }
    }
}

data class LinkedGuidLineIDs(
        val top: Int,
        val bottom: Int,
        val start: Int,
        val end: Int
)