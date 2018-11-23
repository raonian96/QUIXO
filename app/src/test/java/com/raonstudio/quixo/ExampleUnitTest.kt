package com.raonstudio.quixo

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val nowTurn = PieceSymbol.X
    private lateinit var testCase: Array<Array<Array<Piece>>>

    @Before
    fun setUp() {
        testCase = Array(testCaseSymbols.size) { t ->
            Array(5) { i ->
                Array(5) { j ->
                    Piece().apply {
                        symbol = when (testCaseSymbols[t][i][j]) {
                            1 -> PieceSymbol.O
                            2 -> PieceSymbol.X
                            else -> null
                        }
                    }
                }
            }
        }
        testCase.forEach { pieces ->
            repeat(5) { i ->
                repeat(5) { j ->
                    val piece = pieces[i][j]
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
        }
    }


    @Test
    fun checkTestCase() {
        repeat(testCase.size) {
            assertEquals(checkWinner(testCase[it]), testResult[it])
        }
    }

    private fun checkWinner(pieces: Array<Array<Piece>>): PieceSymbol? {
        var isNowTurnWin = false
        repeat(5) {
            if (pieces[it][it].checkMakeFive()) {
                if (pieces[it][it].symbol == nowTurn.not())
                    return nowTurn.not()
                else isNowTurnWin = true
            }
        }
        pieces[2][2].symbol?.let {
            if (pieces[2][2].checkMakeFiveDiagonally(it)) {
                if (it == nowTurn.not())
                    return nowTurn.not()
                else isNowTurnWin = true
            }
        }
        return nowTurn.takeIf { isNowTurnWin }
    }

    private val testCaseSymbols = arrayOf(
            arrayOf(
                    arrayOf(1, 0, 0, 0, 1),
                    arrayOf(0, 0, 0, 0, 0),
                    arrayOf(0, 0, 0, 0, 0),
                    arrayOf(0, 0, 0, 0, 0),
                    arrayOf(1, 0, 0, 0, 1)
            ),
            arrayOf(
                    arrayOf(1, 0, 0, 0, 2),
                    arrayOf(0, 1, 0, 0, 2),
                    arrayOf(1, 1, 1, 1, 2),
                    arrayOf(0, 0, 0, 1, 2),
                    arrayOf(0, 0, 0, 0, 2)
            ),
            arrayOf(
                    arrayOf(1, 0, 0, 0, 1),
                    arrayOf(0, 1, 0, 0, 1),
                    arrayOf(1, 1, 2, 1, 1),
                    arrayOf(0, 0, 0, 1, 1),
                    arrayOf(0, 0, 0, 0, 1)
            ),
            arrayOf(
                    arrayOf(1, 2, 2, 2, 1),
                    arrayOf(2, 1, 2, 2, 1),
                    arrayOf(1, 1, 2, 1, 1),
                    arrayOf(1, 1, 2, 1, 1),
                    arrayOf(2, 2, 2, 1, 1)
            )
    )

    private val testResult = arrayOf(
            null, PieceSymbol.X, PieceSymbol.O, PieceSymbol.O
    )
}