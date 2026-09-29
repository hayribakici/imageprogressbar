package eu.bakici.imageprogressbar.indicator

/** Reveals blocks from left to right, alternating direction on each row. */
class SnakeIndicator(
    blockSize: Int = BLOCK_SIZE_MEDIUM
) : BlockIndicator(blockSize) {

    override fun orderBlocks(order: IntArray, columns: Int, rows: Int) {
        var position = 0
        for (row in 0 until rows) {
            if (row % 2 == 0) {
                for (column in 0 until columns) {
                    order[position++] = row * columns + column
                }
            } else {
                for (column in columns - 1 downTo 0) {
                    order[position++] = row * columns + column
                }
            }
        }
    }
}
