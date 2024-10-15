package com.PFA.Gestion_des_archives.Dto;

import com.PFA.Gestion_des_archives.Model.SiteArchivage;

import java.util.List;

public class SiteArchivageDto {
    private SiteArchivage siteArchivage;
    private List<CategorieArchiveCountDto> categorieArchiveCounts;

    // Getters
    public SiteArchivage getSiteArchivage() {
        return siteArchivage;
    }

    public List<CategorieArchiveCountDto> getCategorieArchiveCounts() {
        return categorieArchiveCounts;
    }

    // Setters
    public void setSiteArchivage(SiteArchivage siteArchivage) {
        this.siteArchivage = siteArchivage;
    }

    public void setCategorieArchiveCounts(List<CategorieArchiveCountDto> categorieArchiveCounts) {
        this.categorieArchiveCounts = categorieArchiveCounts;
    }
}


