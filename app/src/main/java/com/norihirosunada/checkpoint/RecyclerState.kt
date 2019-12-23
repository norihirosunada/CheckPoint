package com.norihirosunada.checkpoint

import java.util.*

class RecyclerState() {
    //header
    constructor(type: RecyclerType, progress: Int, size: Int): this(){
        this.type = type
        this.progress = progress
        this.size = size
    }

    // body
    constructor(type: RecyclerType, checkPoint: CheckPoint, date: Date): this(){
        this.type = type
        this.checkPoint = checkPoint
        this.date = date
    }

    // footer
    constructor(type: RecyclerType):this(){
        this.type = type
    }

    // section未実装

    // RecyclerAdapterにて追加するレコードのタイプ
    var type: RecyclerType = RecyclerType.BODY
    var progress: Int = 0
    var size: Int = 0
    var checkPoint: CheckPoint? = null
    var date: Date? = null
}