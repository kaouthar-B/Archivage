package com.PFA.Gestion_des_archives.Repository;


import com.PFA.Gestion_des_archives.Model.EntiteRattachee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntiteRattacheeRepository extends JpaRepository<EntiteRattachee, Long> {
    EntiteRattachee findByNom(String nomEntite);

}
