package com.raonstudio.findset

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.raonstudio.findset.databinding.PieceItemBinding
import kotlinx.android.synthetic.main.game_board_fragment.*

class GameBoardFragment : Fragment() {
    companion object {
        private const val ROWS = 5
        private const val COLUMNS = 5
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_board_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        board.apply {
            layoutManager = GridLayoutManager(context, COLUMNS)
            adapter = BoardAdapter(ROWS, COLUMNS)
            isLayoutFrozen = true
        }
    }
}

class BoardAdapter(private val rows: Int, private val columns: Int) : RecyclerView.Adapter<PieceViewHolder>() {
    val board = Array(rows) {Array(columns) {Piece.O} }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieceViewHolder {
        return PieceViewHolder(PieceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return rows * columns
    }

    override fun onBindViewHolder(holder: PieceViewHolder, position: Int) {
        holder.binding.piece = board[position/5][position%5]
        holder.binding.root.setOnClickListener {
            it.foreground = it.context.getDrawable(R.drawable.piece_selector)
        }
    }
}

class PieceViewHolder(val binding: PieceItemBinding) : RecyclerView.ViewHolder(binding.root)