package com.nix.jingxun.addp.web.domain;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author keray
 * @date 2019/05/25 15:57
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebPage implements Pageable {
    private int pageNumber;
    private int pageSize;
    private Sort sort;
    public int getPageNumber() {
        return pageNumber;
    }
    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return pageNumber * pageSize;
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public Pageable next() {
        WebPage webPage = new WebPage();
        BeanUtil.copyProperties(this, webPage);
        webPage.setPageNumber(this.pageNumber + 1);
        return webPage;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        WebPage webPage = new WebPage();
        BeanUtil.copyProperties(this, webPage);
        webPage.setPageNumber(0);
        return webPage;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
