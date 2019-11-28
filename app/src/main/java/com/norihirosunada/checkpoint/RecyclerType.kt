package com.norihirosunada.checkpoint

enum class RecyclerType(val int: Int) {
    HEADER(0),
    FOOTER(1),
    SECTION(2),
    BODY(3);

    companion object {
        // Intからenumへの変換
        fun fromInt(int: Int): RecyclerType{
            return values().firstOrNull { it.int == int }
                ?: RecyclerType.BODY
        }
    }
}