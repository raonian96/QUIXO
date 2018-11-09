package com.raonstudio.quixo

import android.content.Context
import android.content.res.Resources
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.raonstudio.quixo.databinding.PieceItemBinding


class GameBoardLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    companion object {
        /**
         * [ConstraintSet]을 이용하기 위해서는 아이디를 세팅하여야 합니다.
         * 이 프로젝트에서는 가이드라인과 애니메이션에 사용될 뷰들을 코드상에서 레이아웃에 추가하고 있습니다.
         * 연관된 뷰들을 직접 동적으로 추가하고 있어 ID 의 시작 값에 대한 상수를 미리 정의해 놓아 사용합니다.
         */
        private const val VERTICAL_GUIDELINE_ID = 101010
        private const val HORIZONTAL_GUIDELINE_ID = 201010
        private const val SYMBOL_ID = 202020
        private const val ROW = 5
        private const val COLUMN = 5
    }

    private val symbolViews: Array<Array<View>>
    private val margin = (Resources.getSystem().displayMetrics.density * 4).toInt()

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

        val symbolWidth = run {
            Resources.getSystem().displayMetrics.let {
                (it.widthPixels - (32 + 4 * 10) * it.density) / COLUMN
            }
        }.toInt()

        symbolViews = Array(ROW) { i ->
            Array(COLUMN) { j ->
                PieceItemBinding.inflate(LayoutInflater.from(context), this, true).root.apply {
                    id = SYMBOL_ID + i * COLUMN + j
                }.also {
                    constraintSet.apply {
                        rearrangeVerticalConstraint(this, it, i)
                        rearrangeHorizontalConstraint(this, it, j)
                        constrainWidth(it.id, symbolWidth)
                        constrainHeight(it.id, symbolWidth)
                    }
                }
            }
        }
        constraintSet.applyTo(this)
    }

    fun move(direction: Direction) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        moveToBoundary(constraintSet, 4, 2, direction)
        TransitionManager.beginDelayedTransition(this)
        constraintSet.applyTo(this)
    }

    private fun moveToBoundary(constraintSet: ConstraintSet, row: Int, column: Int, direction: Direction) {
        nextPosition(row, column, direction)?.let {
            when (direction) {
                Direction.UP, Direction.DOWN -> {
                    swapVerticalConstraint(constraintSet, column, row, it)
                    moveToBoundary(constraintSet, it, column, direction)
                }
                Direction.LEFT, Direction.RIGHT -> {
                    swapHorizontalConstraint(constraintSet, row, column, it)
                    moveToBoundary(constraintSet, row, it, direction)
                }
            }
        }
    }

    private fun swapHorizontalConstraint(constraintSet: ConstraintSet, row: Int, a: Int, b: Int) {
        symbolViews[row][a] = symbolViews[row][b].also { symbolViews[row][b] = symbolViews[row][a] }
        rearrangeHorizontalConstraint(constraintSet, symbolViews[row][a], a)
        rearrangeHorizontalConstraint(constraintSet, symbolViews[row][b], b)
    }

    private fun swapVerticalConstraint(constraintSet: ConstraintSet, column: Int, a: Int, b: Int) {
        symbolViews[a][column] = symbolViews[b][column].also { symbolViews[b][column] = symbolViews[a][column] }
        rearrangeVerticalConstraint(constraintSet, symbolViews[a][column], a)
        rearrangeVerticalConstraint(constraintSet, symbolViews[b][column], b)
    }

    private fun rearrangeHorizontalConstraint(constraintSet: ConstraintSet, view: View, index: Int) {
        constraintSet.apply {
            connect(view.id, ConstraintSet.START, getVerticalGuidLineId(index), ConstraintSet.END, margin)
            connect(view.id, ConstraintSet.END, getVerticalGuidLineId(index + 1), ConstraintSet.START, margin)
        }
    }

    private fun rearrangeVerticalConstraint(constraintSet: ConstraintSet, view: View, index: Int) {
        constraintSet.apply {
            connect(view.id, ConstraintSet.TOP, getHorizontalGuidLineId(index), ConstraintSet.BOTTOM, margin)
            connect(view.id, ConstraintSet.BOTTOM, getHorizontalGuidLineId(index + 1), ConstraintSet.TOP, margin)
        }
    }

    private fun nextPosition(row: Int, column: Int, direction: Direction): Int? {
        return when (direction) {
            Direction.LEFT -> if (column == 0) null else column - 1
            Direction.RIGHT -> if (column == 4) null else column + 1
            Direction.UP -> if (row == 0) null else row - 1
            Direction.DOWN -> if (row == 4) null else row + 1
        }
    }
}