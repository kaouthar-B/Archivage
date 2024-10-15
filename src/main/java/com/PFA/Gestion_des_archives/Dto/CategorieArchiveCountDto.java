package com.PFA.Gestion_des_archives.Dto;

import com.PFA.Gestion_des_archives.Model.CategorieArchive;

public class CategorieArchiveCountDto {
    private CategorieArchive categorieArchive;
    private long count;

    // Getters
    public CategorieArchive getCategorieArchive() {
        return categorieArchive;
    }

    public long getCount() {
        return count;
    }

    // Setters
    public void setCategorieArchive(CategorieArchive categorieArchive) {
        this.categorieArchive = categorieArchive;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

