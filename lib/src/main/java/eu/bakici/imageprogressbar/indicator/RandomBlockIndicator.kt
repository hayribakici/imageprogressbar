package eu.bakici.imageprogressbar.indicator

import kotlin.random.Random

/** Reveals image blocks in a shuffled order. */
class RandomBlockIndicator(
    blockSize: Int = BLOCK_SIZE_MEDIUM,
    private val seed: Long = System.nanoTime()
) : BlockIndicator(blockSize) {

    override fun orderBlocks(order: IntArray, columns: Int, rows: Int) {
        order.shuffle(Random(seed))
    }
}
