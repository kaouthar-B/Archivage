package com.PFA.Gestion_des_archives.Repository;

import com.PFA.Gestion_des_archives.Model.SiteArchivage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteArchivageRepository extends JpaRepository<SiteArchivage, Long> {

}
