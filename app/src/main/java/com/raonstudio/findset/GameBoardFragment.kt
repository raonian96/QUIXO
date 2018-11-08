package com.raonstudio.findset

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.raonstudio.findset.databinding.GameBoardFragmentBinding
import kotlinx.android.synthetic.main.game_board_fragment.*

class GameBoardFragment : Fragment() {
    private lateinit var binding: GameBoardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_board_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.presenter = BoardPresenter(board_container)
    }
}

class BoardPresenter(private val gameBoardLayout: GameBoardLayout){
    fun onDirectionClick() {
        gameBoardLayout.move()
    }
}