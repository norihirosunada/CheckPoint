package com.norihirosunada.checkpoint

import java.util.*

class RecyclerState() {
    constructor(type: RecyclerType, text: String?, checkPoint: CheckPoint?, date: Date?): this(){
        this.type = type
        this.text = text
        this.checkPoint = checkPoint
        this.date = date
    }

    // RcyclerAdapterにて追加するレコードのタイプ
    var type: RecyclerType = RecyclerType.BODY
    var text: String? = ""
    var checkPoint: CheckPoint? = null
    var date: Date? = null
}