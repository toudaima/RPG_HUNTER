package com.game.enums;

/**
 * @author liling
 * @date 2025/7/4 15:51
 * @description
 */
public enum GameStatusEnum {

    READY("准备"),
    START("开始"),
    ;

    private final String desc;

    GameStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
