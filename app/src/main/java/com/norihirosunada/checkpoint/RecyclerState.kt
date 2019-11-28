package com.norihirosunada.checkpoint

class RecyclerState() {
    constructor(type: RecyclerType, text: String): this(){
        this.type = type
        this.text = text
    }

    // RcyclerAdapterにて追加するレコードのタイプ
    var type: RecyclerType = RecyclerType.BODY
    var text: String = ""
}