package com.lezijie.note.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteVo {
    private String groupName;//分组姓名
    private long noteCount;//云记数量
    private Integer typeId;//类型id
}
