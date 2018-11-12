package com.raonstudio.quixo

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.raonstudio.quixo.databinding.PieceItemBinding

enum class PieceSymbol(private val str: String) {
    O("O"), X("X");

    override fun toString(): String {
        return this.str
    }
}

class Piece(val binding: PieceItemBinding) : BaseObservable() {
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

    fun isBoundaryPiece(): Boolean {
        return with(linkedPieces) {
            get(Direction.TOP_LEFT) == null || get(Direction.BOTTOM_RIGHT) == null
        }
    }

    fun getNextPieceOf(direction: Direction): Piece? {
        return linkedPieces[direction]
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