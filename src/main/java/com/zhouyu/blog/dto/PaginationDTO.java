package com.zhouyu.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaginationDTO<T> {
    private List<T> data;     //当前页的问题或通知，Question Or Notification
    private boolean showPreviousPage; //前一页
    private boolean showNextPage;     //后一页
    private boolean showFirstPage;    //第一页
    private boolean showEndPage;      //最后一页

    private Integer page;             //当前页面
    private List<Integer> pages = new ArrayList<>();       //当前页面展示的页码数
    private Integer totalPage;

    //设置分页需要显示的信息
    public void setPagination(Integer totalPage, Integer page) {
        this.page = page;
        this.totalPage = totalPage;

        pages.add(page);
        for(int i = 1; i <= 3; ++i) {
            int tmppage = page - i;
            if(tmppage > 0) pages.add(0, tmppage);
            tmppage = page + i;
            if(tmppage <= totalPage) pages.add(tmppage);
        }

        showPreviousPage = page != 1;
        showNextPage = !page.equals(totalPage);
        showFirstPage = !pages.contains(1);
        showEndPage = !pages.contains(totalPage);
    }
}
