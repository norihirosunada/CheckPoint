package com.norihirosunada.checkpoint

import java.util.*

class RecyclerState() {
    //header用
    constructor(type: RecyclerType, progress: Int): this(){
        this.type = type
        this.progress = progress
    }

    // body用
    constructor(type: RecyclerType, checkPoint: CheckPoint, date: Date): this(){
        this.type = type
        this.checkPoint = checkPoint
        this.date = date
    }

    // footer用
    constructor(type: RecyclerType):this(){
        this.type = type
    }

    // section用未実装

    // RecyclerAdapterにて追加するレコードのタイプ
    var type: RecyclerType = RecyclerType.BODY
    var progress: Int = 0
    var checkPoint: CheckPoint? = null
    var date: Date? = null
}