package com.wishly.wishlistservice.dto;

import java.util.List;

public record SupportedSiteResponse(
        String name,
        List<String> domains,
        boolean enabled
) {
}
