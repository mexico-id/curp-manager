package io.mosip.curp.repository;

import io.mosip.curp.entity.CurpBioData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CurpBioDataRepository extends JpaRepository<CurpBioData, String> {

    Optional<CurpBioData> findByCurpId(String curpId);
    Optional<CurpBioData> findByCurpIdAndCurpType(String curpId, String curpType);

}

