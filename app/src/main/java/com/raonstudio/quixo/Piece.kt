package com.raonstudio.quixo

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.raonstudio.quixo.databinding.PieceItemBinding

enum class PieceSymbol(val str: String? = null) {
    O("O"), X("X"), BLANK();

    override fun toString(): String {
        return this.str?:""
    }
}

class Piece(val binding: PieceItemBinding) : BaseObservable() {
    companion object {
        fun swapLink(piece1: Piece, piece2: Piece, direction: Direction){
            piece1.linkedGuidLineIDs = piece2.linkedGuidLineIDs
                    .also { piece2.linkedGuidLineIDs = piece1.linkedGuidLineIDs }

            piece1.linkedPieces1 = piece2.linkedPieces1
                    .also { piece2.linkedPieces1 = piece1.linkedPieces1 }

            piece2.linkedPieces1[direction] = piece1.also { piece1.linkedPieces1[direction.oppsite] = piece2 }

            piece1.updateLinkFromOther()
            piece2.updateLinkFromOther()
        }
    }

    var symbol = PieceSymbol.BLANK
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.symbol)
        }

    lateinit var linkedGuidLineIDs: LinkedGuidLineIDs
    var linkedPieces1 = HashMap<Direction, Piece>()

    fun isBoundaryPiece(): Boolean {
        return with(linkedPieces1){
            get(Direction.TOP) == null || get(Direction.BOTTOM) == null
                    || get(Direction.LEFT) == null || get(Direction.RIGHT) == null
        }
    }

    fun getNextPieceOf(direction: Direction): Piece? {
        return linkedPieces1[direction]
    }

    private fun updateLinkFromOther(){
        linkedPieces1.forEach {
            it.value.linkedPieces1[it.key.oppsite] = this
        }
    }
}

data class LinkedGuidLineIDs(
        val top: Int,
        val bottom: Int,
        val start: Int,
        val end: Int
)