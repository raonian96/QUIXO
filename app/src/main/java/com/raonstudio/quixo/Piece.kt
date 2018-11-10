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

            piece1.linkedPieces = piece2.linkedPieces
                    .also { piece2.linkedPieces = piece1.linkedPieces }

            when (direction) {
                Direction.UP -> piece2.linkedPieces.top = piece1.also { piece1.linkedPieces.bottom = piece2 }
                Direction.LEFT -> piece2.linkedPieces.left = piece1.also { piece1.linkedPieces.right = piece2 }
                Direction.RIGHT -> piece2.linkedPieces.right= piece1.also { piece1.linkedPieces.left = piece2 }
                Direction.DOWN -> piece2.linkedPieces.bottom = piece1.also { piece1.linkedPieces.top = piece2 }
            }

            piece1.updateLinkfromOther()
            piece2.updateLinkfromOther()
        }
    }

    var symbol = PieceSymbol.BLANK
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.symbol)
        }

    lateinit var linkedGuidLineIDs: LinkedGuidLineIDs
    var linkedPieces = LinkedPieces()

    fun isBoundaryPiece(): Boolean {
        return with(linkedPieces){
            top == null || left == null || right == null || bottom == null
        }
    }

    fun getLinkedPiece(direction: Direction): Piece? {
        return when (direction) {
            Direction.UP -> linkedPieces.top
            Direction.LEFT -> linkedPieces.left
            Direction.RIGHT -> linkedPieces.right
            Direction.DOWN -> linkedPieces.bottom
        }
    }

    private fun updateLinkfromOther(){
        with(linkedPieces){
            top?.linkedPieces?.bottom = this@Piece
            bottom?.linkedPieces?.top = this@Piece
            left?.linkedPieces?.right = this@Piece
            right?.linkedPieces?.left = this@Piece
            topLeft?.linkedPieces?.bottomRight = this@Piece
            topRight?.linkedPieces?.bottomLeft = this@Piece
            bottomLeft?.linkedPieces?.topRight = this@Piece
            bottomRight?.linkedPieces?.topLeft = this@Piece
        }
    }
}

data class LinkedGuidLineIDs(
        val top: Int,
        val bottom: Int,
        val start: Int,
        val end: Int
)

data class LinkedPieces(
        var top: Piece? = null,
        var bottom: Piece? = null,
        var left: Piece? = null,
        var right: Piece? = null,
        var topLeft: Piece? = null,
        var topRight: Piece? = null,
        var bottomLeft: Piece? = null,
        var bottomRight: Piece? = null
)