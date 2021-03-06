package com.raonstudio.quixo

import android.content.Context
import android.databinding.ObservableInt
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import com.raonstudio.quixo.databinding.PieceItemBinding
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast

class GameBoardLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    companion object {
        private const val VERTICAL_GUIDELINE_ID = 101010
        private const val HORIZONTAL_GUIDELINE_ID = 201010
        private const val SYMBOL_ID = 202020
        private const val ROW = 5
        private const val COLUMN = 5
    }

    private val pieces: Array<Array<Piece>>
    private val margin = dip(4)
    private var selectedViewId = ObservableInt()
    private var selectedPiece: Piece? = null
    private var touchable = true
    private var nowTurn = PieceSymbol.O

    //(스크린 너비 - (좌우 여백 + 블럭 좌우 여백)) / COLUMN
    private val symbolWidth = (getScreenWidth() - dip(32 + 4 * 10)) / COLUMN

    private fun changeTurn() {
        Log.d("checkWinner()", checkWinner()?.name ?: "none")
        nowTurn = !nowTurn
        selectedViewId.set(0)
        selectedPiece = null
    }

    private fun getVerticalGuidLineId(index: Int) = VERTICAL_GUIDELINE_ID + index
    private fun getHorizontalGuidLineId(index: Int) = HORIZONTAL_GUIDELINE_ID + index

    init {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        repeat(ROW + 1) {
            constraintSet.apply {
                create(getVerticalGuidLineId(it), ConstraintSet.VERTICAL_GUIDELINE)
                setGuidelinePercent(getVerticalGuidLineId(it), 0.2f * it)
                create(getHorizontalGuidLineId(it), ConstraintSet.HORIZONTAL_GUIDELINE)
                setGuidelinePercent(getHorizontalGuidLineId(it), 0.2f * it)
            }
        }

        pieces = Array(ROW) { i ->
            Array(COLUMN) { j ->
                val binding = PieceItemBinding.inflate(LayoutInflater.from(context), this, true).apply {
                    root.id = SYMBOL_ID + i * COLUMN + j
                    selectedId = selectedViewId
                }
                Piece().apply {
                    linkedGuidLineIDs = LinkedGuidLineIDs(
                            getHorizontalGuidLineId(i),
                            getHorizontalGuidLineId(i + 1),
                            getVerticalGuidLineId(j),
                            getVerticalGuidLineId(j + 1)
                    )
                    linkedViewId = binding.root.id
                }.also { piece ->
                    binding.piece = piece
                    binding.root.setOnClickListener {
                        if (!piece.isBoundaryPiece() || (piece.symbol != null && piece.symbol != nowTurn)) return@setOnClickListener
                        selectedPiece = if (selectedViewId.get() != it.id) {
                            selectedViewId.set(it.id)
                            piece
                        } else {
                            selectedViewId.set(0)
                            null
                        }
                    }
                }
            }
        }

        repeat(ROW) { i ->
            repeat(COLUMN) { j ->
                val piece = pieces[i][j]
                rearrangePiece(constraintSet, piece)
                pieces.getOrNull(i - 1)?.getOrNull(j - 1)?.linkedPieces?.set(Direction.BOTTOM_RIGHT, piece)
                pieces.getOrNull(i - 1)?.getOrNull(j)?.linkedPieces?.set(Direction.BOTTOM, piece)
                pieces.getOrNull(i - 1)?.getOrNull(j + 1)?.linkedPieces?.set(Direction.BOTTOM_LEFT, piece)
                pieces.getOrNull(i)?.getOrNull(j - 1)?.linkedPieces?.set(Direction.RIGHT, piece)
                pieces.getOrNull(i)?.getOrNull(j + 1)?.linkedPieces?.set(Direction.LEFT, piece)
                pieces.getOrNull(i + 1)?.getOrNull(j - 1)?.linkedPieces?.set(Direction.TOP_RIGHT, piece)
                pieces.getOrNull(i + 1)?.getOrNull(j)?.linkedPieces?.set(Direction.TOP, piece)
                pieces.getOrNull(i + 1)?.getOrNull(j + 1)?.linkedPieces?.set(Direction.TOP_LEFT, piece)
            }
        }

        constraintSet.applyTo(this)
    }

    fun move(direction: Direction) {
        if (touchable) selectedPiece?.let { piece ->
            piece.getNextPiece(direction) ?: run {
                context.toast("제자리에 놓을 수 없습니다.")
                return
            }
            piece.symbol = nowTurn

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            moveToBoundary(constraintSet, piece, direction)

            val transition = AutoTransition()
            transition.addListener(transitionListener)
            TransitionManager.beginDelayedTransition(this, transition)

            constraintSet.applyTo(this)
        }
    }

    private fun moveToBoundary(constraintSet: ConstraintSet, piece: Piece, direction: Direction) {
        piece.getNextPiece(direction)?.let {
            Piece.swapLink(piece, it, direction)
            moveToBoundary(constraintSet, piece, direction)
            rearrangePiece(constraintSet, it)
        } ?: rearrangePiece(constraintSet, piece)
    }

    private fun rearrangePiece(constraintSet: ConstraintSet, piece: Piece) {
        val viewID = piece.linkedViewId
        val guidLineIDs = piece.linkedGuidLineIDs

        with(constraintSet) {
            connect(viewID, ConstraintSet.TOP, guidLineIDs.top, ConstraintSet.TOP, margin)
            connect(viewID, ConstraintSet.BOTTOM, guidLineIDs.bottom, ConstraintSet.BOTTOM, margin)
            connect(viewID, ConstraintSet.START, guidLineIDs.start, ConstraintSet.START, margin)
            connect(viewID, ConstraintSet.END, guidLineIDs.end, ConstraintSet.END, margin)
            constrainWidth(viewID, symbolWidth)
            constrainHeight(viewID, symbolWidth)
        }
    }

    private fun checkWinner(): PieceSymbol? {
        var piece = getLeftTopCornerPiece()
        var isNowTurnWin = false

        while (true) {
            if (piece.checkMakeFive()) {
                if (piece.symbol == nowTurn)
                    isNowTurnWin = true
                else
                    return piece.symbol
            }
            piece = piece.linkedPieces[Direction.BOTTOM_RIGHT] ?: break
        }

        val centerPiece = getLeftTopCornerPiece().linkedPieces[Direction.BOTTOM_RIGHT]!!.linkedPieces[Direction.BOTTOM_RIGHT]!!
        if (centerPiece.checkMakeFiveDiagonally()) {
            if (centerPiece.symbol == nowTurn)
                isNowTurnWin = true
            else
                return centerPiece.symbol
        }
        return nowTurn.takeIf { isNowTurnWin }
    }

    private fun getLeftTopCornerPiece(): Piece {
        var piece = pieces[0][0]
        while (piece.linkedPieces[Direction.LEFT] != null || piece.linkedPieces[Direction.TOP] != null) {
            piece.linkedPieces[Direction.TOP_LEFT]?.let { piece = it }
            piece.linkedPieces[Direction.TOP]?.let { piece = it }
            piece.linkedPieces[Direction.LEFT]?.let { piece = it }
        }
        return piece
    }

    private val transitionListener = object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition?) {
            touchable = true
            changeTurn()
        }

        override fun onTransitionResume(transition: Transition?) {
        }

        override fun onTransitionPause(transition: Transition?) {
        }

        override fun onTransitionCancel(transition: Transition?) {
        }

        override fun onTransitionStart(transition: Transition?) {
            touchable = false
        }
    }
}