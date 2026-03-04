package com.wishly.wishlistservice.service;

import com.wishly.wishlistservice.dto.SupportedSiteResponse;

import java.util.List;

public interface SupportedSitesService {
    List<SupportedSiteResponse> getSupportedSites();
    boolean isSupported(String url);
    String getSiteName(String url);
}
