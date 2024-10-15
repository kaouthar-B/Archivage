package com.PFA.Gestion_des_archives.Dto;

import java.time.LocalDate;

public class ArchiveDto {

    private Long id;
    private String name;
    private String reference;
    private LocalDate date;

    // Constructeurs
    public ArchiveDto() {}

    public ArchiveDto(Long id, String name, String reference, LocalDate date) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.date = date;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
