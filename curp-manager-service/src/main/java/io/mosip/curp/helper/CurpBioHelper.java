package io.mosip.curp.helper;
import io.mosip.curp.dto.MatchedCurpDto;
import io.mosip.curp.entity.CurpBioData;
import io.mosip.curp.entity.MatchedCurp;
import io.mosip.curp.service.CurpService;
import io.mosip.curp.service.MatchedCurpService;
import io.mosip.kernel.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public class CurpBioHelper {
    private static final Logger LOGGER = Logger.getLogger(CurpBioHelper.class.getName());
    @Autowired
    private CurpService curpService;

    @Autowired
    private MatchedCurpService matchedCurpService;

    public String updateCurpBioDataStatus(MatchedCurpDto matchedCurpDto) {
       CurpBioData curpBioData = curpService.findCurpBioDataById(matchedCurpDto.getCurpId());
        LOGGER.info("Updating CurpBioData for curpId: " + matchedCurpDto.getCurpId());
            curpBioData.setCurpStatus(matchedCurpDto.getCurpStatus());
            curpBioData.setStatusComment(matchedCurpDto.getStatusComment());
            curpBioData.setUpdDtimes(DateUtils.getUTCCurrentDateTime());

            String updateResponse = curpService.updateCurpBioData(curpBioData);
            if (!updateResponse.equals("CurpBioData updated successfully")) {
                return updateResponse;
            }
            MatchedCurp matchedCurp = matchedCurpService.findCurpId(matchedCurpDto.getCurpId());
            MatchedCurp matchedCurpId = matchedCurpService.findMatchedCurpId(matchedCurpDto.getMatchedCurpIds());
            if(matchedCurpId!=null){
                LOGGER.info("matched curpId is already exists: "+matchedCurpId.getMatchedCurpIds());
            }
        else if (matchedCurp != null) {
                String updatedMatchedCurpIds = matchedCurp.getMatchedCurpIds() + "," + matchedCurpDto.getMatchedCurpIds();
                matchedCurp.setMatchedCurpIds(updatedMatchedCurpIds);
                String matchedUpdateResponse = matchedCurpService.updateMatchedCurp(matchedCurp);
                if (!matchedUpdateResponse.equals("MatchedCurp updated successfully")) {
                    return matchedUpdateResponse;
                }
            } else {
                MatchedCurp newMatchedCurp = new MatchedCurp();
                newMatchedCurp.setCurpId(matchedCurpDto.getCurpId());
                newMatchedCurp.setMatchedCurpIds(matchedCurpDto.getMatchedCurpIds());
                newMatchedCurp.setStatusCode(matchedCurpDto.getStatusCode());
                newMatchedCurp.setStatusComment(matchedCurpDto.getStatusComment());
                newMatchedCurp.setCreatedBy("TEST_USER");
                newMatchedCurp.setCreatedDateTime(DateUtils.getUTCCurrentDateTime());

                String matchedSaveResponse = matchedCurpService.saveMatchedCurp(newMatchedCurp);
                if (!matchedSaveResponse.equals("MatchedCurp saved successfully")) {
                    return matchedSaveResponse;
                }
            }
            return "CurpBioData and MatchedCurp updated successfully";
    }
    public String findAndProcessCurpByIdAndType(String curpId, String curpType) {

        Optional<CurpBioData> curpBioDataOptional = curpService.findCurpBioDataByIdAndType(curpId, curpType);
        if (curpBioDataOptional.isPresent()) {
            CurpBioData curpBioData = curpBioDataOptional.get();
            if ("NEW".equalsIgnoreCase(curpBioData.getCurpType())) {
                curpBioData.setCurpStatus("PROCESSED");
                curpBioData.setCrBy("SYSTEM");
                curpBioData.setUpdDtimes(DateUtils.getUTCCurrentDateTime());
                curpService.updateCurpBioData(curpBioData);
                return "CurpBioData status updated to PROCESSED for curpId: " + curpId;
            } else {
                return "CurpBioData curpType is not 'NEW', no update made for curpId: " + curpId;
            }
        } else {
            return "CurpBioData not found for curpId: " + curpId + " and curpType: " + curpType;
        }
    }
}
