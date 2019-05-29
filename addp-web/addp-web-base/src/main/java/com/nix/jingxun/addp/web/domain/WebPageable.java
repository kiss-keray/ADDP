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
public class WebPageable implements Pageable {
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
        WebPageable webPageable = new WebPageable();
        BeanUtil.copyProperties(this, webPageable);
        webPageable.setPageNumber(this.pageNumber + 1);
        return webPageable;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        WebPageable webPageable = new WebPageable();
        BeanUtil.copyProperties(this, webPageable);
        webPageable.setPageNumber(0);
        return webPageable;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
