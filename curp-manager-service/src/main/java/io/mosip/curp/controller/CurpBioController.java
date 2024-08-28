package io.mosip.curp.controller;

import io.mosip.curp.dto.MatchedCurpDto;
import io.mosip.curp.helper.CurpBioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("/api/curp-bio-data")
public class CurpBioController {

    @Autowired
    private CurpBioHelper curpBioHelper;

    @PostMapping("/updatematch")
    public ResponseEntity<String> updateCurpBioDataStatus(@RequestBody @Valid MatchedCurpDto matchedCurpDto) {
        String response = curpBioHelper.updateCurpBioDataStatus(matchedCurpDto);
        if (response.equals("CurpBioData and MatchedCurp updated successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/process-curp/{curpId}/{curpType}")
    public String processCurpByIdAndType(
            @PathVariable("curpId") String curpId,
            @PathVariable("curpType") String curpType) {
        return curpBioHelper.findAndProcessCurpByIdAndType(curpId, curpType);
    }
}
