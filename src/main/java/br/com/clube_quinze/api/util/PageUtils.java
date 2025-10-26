package br.com.clube_quinze.api.util;

import br.com.clube_quinze.api.dto.common.PageResponse;
import org.springframework.data.domain.Page;

public final class PageUtils {

    private PageUtils() {
    }

    public static <T> PageResponse<T> toResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize());
    }
}
