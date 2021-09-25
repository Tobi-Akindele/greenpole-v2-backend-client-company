package com.ap.greenpole.clientCompanyModule.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * Created by Nelu Akejelu on 18/08/2020.
 */


@Getter
@Setter
@AllArgsConstructor
public class PagingResponse<T> {

    /**
     * entity count
     */
    private Long count;
    /**
     * page number, 0 indicate the first page.
     */
    private Long pageNumber;
    /**
     * size of page, 0 indicate infinite-sized.
     */
    private Long pageSize;
    /**
     * Offset from the of pagination.
     */
    private Long pageOffset;
    /**
     * the number total of pages.
     */
    private Long pageTotal;
    /**
     * elements of page.
     */
    private List<T> elements;
}
