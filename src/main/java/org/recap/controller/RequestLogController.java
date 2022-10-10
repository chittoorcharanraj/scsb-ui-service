package org.recap.controller;

import org.recap.model.request.RequestLogReportRequest;
import org.recap.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dinakar N created on 28/09/22
 */
@RestController
@RequestMapping("/request-log")
public class RequestLogController {

    @Autowired
    private RequestService requestService;

    @PostMapping("/reports")
    public ResponseEntity<RequestLogReportRequest> getRequestsLogReports(@RequestBody RequestLogReportRequest requestLogReportRequest){
        return new ResponseEntity<>(requestService.getRequestReports(requestLogReportRequest), HttpStatus.OK);
    }

    @PostMapping("/submit")
    public ResponseEntity<RequestLogReportRequest> submitRequestsLogReports(@RequestBody RequestLogReportRequest requestLogReportRequest){
        return new ResponseEntity<>(requestService.submitRequestReports(requestLogReportRequest), HttpStatus.OK);
    }
}
