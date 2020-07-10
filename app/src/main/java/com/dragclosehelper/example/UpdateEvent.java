package com.dragclosehelper.example;

/**
 * @author Jsonshi
 * @date 2020/7/10
 * <p>
 * Description:
 */
class UpdateEvent {
   public String tag;
   public int pos;

    public UpdateEvent() {
    }

    public UpdateEvent(String tag, int pos) {
        this.tag = tag;
        this.pos = pos;
    }
}
