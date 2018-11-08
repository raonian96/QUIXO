package com.raonstudio.findset

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.raonstudio.findset.databinding.GameBoardFragmentBinding
import kotlinx.android.synthetic.main.game_board_fragment.*

class GameBoardFragment : Fragment() {
    companion object {
        private const val ROWS = 5
        private const val COLUMNS = 5
    }

    private val boardAdapter = BoardAdapter(ROWS, COLUMNS)
    private lateinit var binding: GameBoardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_board_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        board.apply {
            layoutManager = GridLayoutManager(context, COLUMNS)
            adapter = boardAdapter
        }

        binding.presenter = BoardPresenter(boardAdapter)
    }
}

class BoardPresenter(private val boardAdapter: BoardAdapter){
    fun onDirectionClick(direction: Direction) {
        boardAdapter.selectedPosition?.let { position ->
            boardAdapter.movePiece(position, direction)
        }
    }
}