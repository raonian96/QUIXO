package com.raonstudio.findset

import android.content.Context
import android.content.res.Resources
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.raonstudio.findset.databinding.PieceItemBinding


class GameBoardLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    companion object {
        private const val VERTICAL_GUIDELINE_ID = 101010
        private const val HORIZONTAL_GUIDELINE_ID = 201010
        private const val SYMBOL_ID = 202020
        private const val ROW = 5
        private const val COLUMN = 5
    }

    private val symbolViews: Array<Array<View>>
    private val constraintSet = ConstraintSet()
    private val margin = (Resources.getSystem().displayMetrics.density * 4).toInt()

    private fun getVerticalGuidLineId(index: Int) = VERTICAL_GUIDELINE_ID + index
    private fun getHorizontalGuidLineId(index: Int) = HORIZONTAL_GUIDELINE_ID + index

    init {
        constraintSet.clone(this)

        repeat(6) {
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

    fun move() {
        constraintSet.clone(this)
//        swapHorizontalConstraint(constraintSet, 4, 2, 3)
//        swapHorizontalConstraint(constraintSet, 4, 3, 4)
        TransitionManager.beginDelayedTransition(this)
        constraintSet.applyTo(this)
    }

    private fun swapHorizontalConstraint(constraintSet: ConstraintSet, row: Int, a: Int, b: Int) {
        symbolViews[row][a] = symbolViews[row][b].also { symbolViews[row][b] = symbolViews[row][a] }
        rearrangeHorizontalConstraint(constraintSet, symbolViews[row][a], a)
        rearrangeHorizontalConstraint(constraintSet, symbolViews[row][b], b)
    }

    private fun swapVerticalConstraint(constraintSet: ConstraintSet, column: Int, a: Int, b: Int) {
        symbolViews[a][column] = symbolViews[a][column].also { symbolViews[b][column] = symbolViews[a][column] }
        rearrangeVerticalConstraint(constraintSet, symbolViews[a][column], a)
        rearrangeVerticalConstraint(constraintSet, symbolViews[b][column], b)
    }

    private fun rearrangeHorizontalConstraint(constraintSet: ConstraintSet, view: View, index: Int){
        constraintSet.apply {
            connect(view.id, ConstraintSet.START, getVerticalGuidLineId(index), ConstraintSet.END)
            connect(view.id, ConstraintSet.END, getVerticalGuidLineId(index + 1), ConstraintSet.START)
        }
    }

    private fun rearrangeVerticalConstraint(constraintSet: ConstraintSet, view: View, index: Int){
        constraintSet.apply {
            connect(view.id, ConstraintSet.TOP, getHorizontalGuidLineId(index), ConstraintSet.BOTTOM, margin)
            connect(view.id, ConstraintSet.BOTTOM, getHorizontalGuidLineId(index + 1), ConstraintSet.TOP, margin)
        }
    }
}