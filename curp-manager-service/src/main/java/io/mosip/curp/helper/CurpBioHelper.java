package io.mosip.curp.helper;
import io.mosip.curp.dto.MatchedCurpDto;
import io.mosip.curp.entity.CurpBioData;
import io.mosip.curp.entity.MatchedCurp;
import io.mosip.curp.service.CurpService;
import io.mosip.curp.service.MatchedCurpService;
import io.mosip.kernel.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class CurpBioHelper {
    private static final Logger LOGGER = Logger.getLogger(CurpBioHelper.class.getName());
    @Autowired
    private CurpService curpService;

    @Autowired
    private MatchedCurpService matchedCurpService;

    public String updateCurpBioData(MatchedCurpDto matchedCurpDto) {
        CurpBioData curpBioData = curpService.findCurpBioDataById(matchedCurpDto.getCurpId());
        if (curpBioData == null) {
            LOGGER.info("Curp data not found for the given id: " + matchedCurpDto.getCurpId());
            return "Curp data not found for the given id: " + matchedCurpDto.getCurpId();
        }
        LOGGER.info("Updating CurpBioData for curpId: " + matchedCurpDto.getCurpId());
        curpBioData.setCurpStatus(matchedCurpDto.getCurpStatus());
        curpBioData.setStatusComment(matchedCurpDto.getStatusComment());
        curpBioData.setUpdBy("MOSIP_SYSTEM");
        curpBioData.setUpdDtimes(DateUtils.getUTCCurrentDateTime());

        String updateResponse = curpService.updateCurpBioData(curpBioData);
        if (!updateResponse.equals("CurpBioData updated successfully")) {
            return updateResponse;
        }
        MatchedCurp matchedCurp = matchedCurpService.findCurpId(matchedCurpDto.getCurpId());
        if (matchedCurp != null) {
            Set<String> existingMatchedCurpIds = new HashSet<>(matchedCurp.getMatchedCurpIds());
            Set<String> newMatchedCurpIds = new HashSet<>(matchedCurpDto.getMatchedCurpIds());
            Set<String> duplicates = new HashSet<>(existingMatchedCurpIds);
            duplicates.retainAll(newMatchedCurpIds);
            if (!duplicates.isEmpty()) {
                LOGGER.info("MatchedCurpIds already exist: " + duplicates);
            }
            existingMatchedCurpIds.addAll(newMatchedCurpIds);
            matchedCurp.setMatchedCurpIds(new ArrayList<>(existingMatchedCurpIds));
            String matchedUpdateResponse = matchedCurpService.updateMatchedCurp(matchedCurp);
            if (!matchedUpdateResponse.equals("MatchedCurp updated successfully")) {
                return matchedUpdateResponse;
            }
        } else {
            MatchedCurp newMatchedCurp = new MatchedCurp();
            newMatchedCurp.setCurpId(matchedCurpDto.getCurpId());
            newMatchedCurp.setMatchedCurpIds(new ArrayList<>(matchedCurpDto.getMatchedCurpIds()));
            newMatchedCurp.setStatusCode(matchedCurpDto.getStatusCode());
            newMatchedCurp.setStatusComment(matchedCurpDto.getStatusComment());
            newMatchedCurp.setCreatedBy("SYSTEM");
            newMatchedCurp.setCreatedDateTime(DateUtils.getUTCCurrentDateTime());

            String matchedSaveResponse = matchedCurpService.saveMatchedCurp(newMatchedCurp);
            if (!matchedSaveResponse.equals("MatchedCurp saved successfully")) {
                return matchedSaveResponse;
            }
        }
        return "CurpBioData and MatchedCurp updated successfully";
    }

    public String findAndUpdateCurpStatus(String curpId, String curpType) {

        Optional<CurpBioData> curpBioDataOptional = curpService.findCurpBioDataByIdAndType(curpId, curpType);
        if (curpBioDataOptional.isPresent()) {
            CurpBioData curpBioData = curpBioDataOptional.get();
            curpBioData.setCurpStatus("PROCESSED");
            curpBioData.setUpdBy("MOSIP_SYSTEM");
            curpBioData.setUpdDtimes(DateUtils.getUTCCurrentDateTime());
            curpService.updateCurpBioData(curpBioData);
            return "CurpBioData status updated to PROCESSED for curpId: " + curpId;
        } else {
            return "CurpBioData not found for curpId: " + curpId + " and curpType: " + curpType;
        }
    }
}
