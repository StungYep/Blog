package com.zhouyu.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;          //状态
    private Long notifier;           //通知者
    private String notifierName;     //通知者名字
    private String outerTitle;       //问题标题
    private Long outerid;            //问题 ID
    private String typeName;         //中间文案
    private Integer type;            //通知者
}
