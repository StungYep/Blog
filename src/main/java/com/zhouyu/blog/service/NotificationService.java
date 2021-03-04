package com.zhouyu.blog.service;

import com.zhouyu.blog.dto.NotificationDTO;
import com.zhouyu.blog.dto.PaginationDTO;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.enums.NotificationStatusEnum;
import com.zhouyu.blog.enums.NotificationTypeEnum;
import com.zhouyu.blog.exception.CustomizeErrorCode;
import com.zhouyu.blog.exception.CustomizeException;
import com.zhouyu.blog.mapper.NotificationMapper;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
        Integer totalPage = totalCount / size;         //用户通知的总数

        if(totalCount % size > 0) totalPage++;          //问题总页码数
        totalPage = Math.max(1, totalPage);
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        page = Math.min(Math.max(page, 1), totalPage);
        paginationDTO.setPagination(totalPage, page);   //设置需要显示的页码
        Integer offset = (page - 1) * size;

        //pageHelper实现分页, notifications为当前页面的通知
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        if(notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);        //设置当前页面需要显示的通知
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(notification.getReceiver() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);  //不能已读别人的通知
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
