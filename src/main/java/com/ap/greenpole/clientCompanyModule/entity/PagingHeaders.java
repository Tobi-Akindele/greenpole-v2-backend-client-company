package com.ap.greenpole.clientCompanyModule.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@Getter
@AllArgsConstructor
public enum PagingHeaders {

    PAGE_SIZE("Page-Size"),
    PAGE_NUMBER("Page-Number"),
    PAGE_OFFSET("Page-Offset"),
    PAGE_TOTAL("Page-Total"),
    COUNT("Count");

    private final String name;
}
