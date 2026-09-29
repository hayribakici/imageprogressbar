package eu.bakici.imageprogressbar.indicator

/** Reveals blocks from the center outward in a square spiral. */
class SpiralBlockIndicator(
    blockSize: Int = BLOCK_SIZE_MEDIUM
) : BlockIndicator(blockSize) {

    override fun orderBlocks(order: IntArray, columns: Int, rows: Int) {
        val used = BooleanArray(order.size)
        var written = 0
        var column = (columns - 1) / 2
        var row = (rows - 1) / 2
        var stepLength = 1
        var direction = 0

        fun addCurrent() {
            if (column !in 0 until columns || row !in 0 until rows) return
            val index = row * columns + column
            if (!used[index]) {
                used[index] = true
                order[written++] = index
            }
        }

        addCurrent()
        while (written < order.size) {
            repeat(2) {
                repeat(stepLength) {
                    when (direction % 4) {
                        0 -> column++ // right
                        1 -> row++    // down
                        2 -> column-- // left
                        else -> row-- // up
                    }
                    addCurrent()
                }
                direction++
            }
            stepLength++
        }
    }
}
