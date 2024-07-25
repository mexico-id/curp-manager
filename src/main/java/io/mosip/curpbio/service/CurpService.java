package io.mosip.curpbio.service;


import io.mosip.curpbio.entity.CurpBioData;
import io.mosip.curpbio.repositary.CurpBioDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CurpService {
    private static final Logger LOGGER = Logger.getLogger(CurpService.class.getName());

    @Autowired
    private CurpBioDataRepository curpBioDataRepository;

    public String updateCurpBioData(CurpBioData curpBioData) {
        try {
            curpBioDataRepository.save(curpBioData);
            return "CurpBioData updated successfully";
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Error updating CurpBioData", e);
            return "Error updating CurpBioData: " + e.getMessage();
        }
    }

    public CurpBioData findCurpBioDataById(String curp_id) {

        Optional<CurpBioData> curpId = curpBioDataRepository.findByCurpId(curp_id);
        return curpId.orElse(null);
    }
}

