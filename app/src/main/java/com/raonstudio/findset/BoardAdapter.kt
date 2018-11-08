package com.raonstudio.findset

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.raonstudio.findset.databinding.PieceItemBinding


class BoardAdapter(private val rows: Int, private val columns: Int) : RecyclerView.Adapter<PieceViewHolder>() {
    val board = Array(rows) {Array(columns) {Piece()} }
    var selectedPosition: Int? = null

    private fun selectClear() {selectedPosition = null}
    private fun getPieceOf(position: Int): Piece{
        return board[position/5][position%5]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieceViewHolder {
        return PieceViewHolder(PieceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            binding.root.setOnClickListener {
                if(selectedPosition == adapterPosition) {
                    selectClear()
                }
                else {
                    selectedPosition.takeIf { it != null }?.also { notifyItemChanged(it)}
                    selectedPosition = adapterPosition
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return rows * columns
    }

    override fun onBindViewHolder(holder: PieceViewHolder, position: Int) {
        with(holder.binding) {
            piece = getPieceOf(position)
            selected = selectedPosition?.let { it == position } ?: false
        }
    }

    fun movePiece(position: Int, direction: Direction){
        getPieceOf(position).symbol = PieceSymbol.O
        selectClear()
        notifyItemChanged(position)
        swapPiece(position, direction)
    }

    private fun swapPiece(position: Int, direction: Direction){
        nextPosition(position, direction)?.let {nextPosition ->
            board[position/5][position%5] = getPieceOf(nextPosition)
                    .also { board[nextPosition/5][nextPosition%5] = getPieceOf(position) }
            swapPiece(nextPosition, direction)
        }
        notifyItemChanged(position)
    }

    private fun nextPosition(position: Int, direction: Direction): Int? {
        return when (direction){
            Direction.LEFT -> if(position%5 == 4) null else position+1
            Direction.RIGHT -> if(position%5 == 0) null else position-1
            Direction.UP -> if(position/5 == 4) null else position+5
            Direction.DOWN -> if(position/5 == 0) null else position-5
        }
    }
}

enum class Direction{
    LEFT, RIGHT, UP, DOWN
}

class PieceViewHolder(val binding: PieceItemBinding) : RecyclerView.ViewHolder(binding.root)