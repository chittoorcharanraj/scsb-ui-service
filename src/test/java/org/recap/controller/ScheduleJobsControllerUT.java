package org.recap.controller;

import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.jpa.JobEntity;
import org.recap.model.schedule.ScheduleJobRequest;
import org.recap.model.schedule.ScheduleJobResponse;
import org.recap.model.search.ScheduleJobsForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.JobDetailsRepository;
import org.recap.service.RestHeaderService;
import org.recap.util.UserAuthUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ScheduleJobsControllerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    ScheduleJobsController scheduleJobsController;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    JobDetailsRepository jobDetailsRepository;

    @Mock
    RestTemplate restTemplate;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    HttpHeaders httpHeaders;

    @Test
    public void displayJobs(){
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        ScheduleJobsForm jobsForm = scheduleJobsController.displayJobs(request);
        assertNotNull(jobsForm);
    }
    @Test
    public void displayJobsForSuperAdmin(){
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        JobEntity jobEntity = getJobEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(jobDetailsRepository.findAll()).thenReturn(Arrays.asList(jobEntity));
        ScheduleJobsForm jobsForm = scheduleJobsController.displayJobs(request);
        assertNotNull(jobsForm);
    }
    @Test
    public void scheduleJob(){
        ScheduleJobsForm scheduleJobsForm = getScheduleJobsForm();
        ScheduleJobResponse scheduleJobResponse = getScheduleJobResponse();
        when(scheduleJobsController.getRestTemplate()).thenReturn(restTemplate);
        when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        when(jobDetailsRepository.findByJobName(any())).thenReturn(getJobEntity());
        ResponseEntity responseEntity1 = new ResponseEntity<ScheduleJobResponse>(scheduleJobResponse, HttpStatus.OK);
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ScheduleJobRequest>>any());
        ScheduleJobsForm jobsForm = scheduleJobsController.scheduleJob(scheduleJobsForm);
        assertNotNull(jobsForm);
    }
    @Test
    public void scheduleJobUNSCHEDULE(){
        ScheduleJobsForm scheduleJobsForm = getScheduleJobsForm();
        scheduleJobsForm.setScheduleType(RecapConstants.UNSCHEDULE);
        ScheduleJobResponse scheduleJobResponse = getScheduleJobResponse();
        when(scheduleJobsController.getRestTemplate()).thenReturn(restTemplate);
        when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        when(jobDetailsRepository.findByJobName(any())).thenReturn(getJobEntity());
        ResponseEntity responseEntity1 = new ResponseEntity<ScheduleJobResponse>(scheduleJobResponse, HttpStatus.OK);
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ScheduleJobRequest>>any());
        ScheduleJobsForm jobsForm = scheduleJobsController.scheduleJob(scheduleJobsForm);
        assertNotNull(jobsForm);
    }
    @Test
    public void scheduleJobFailure(){
        ScheduleJobsForm scheduleJobsForm = getScheduleJobsForm();
        scheduleJobsForm.setScheduleType(RecapConstants.UNSCHEDULE);
        ScheduleJobResponse scheduleJobResponse = getScheduleJobResponse();
        scheduleJobResponse.setMessage("Failure");
        when(scheduleJobsController.getRestTemplate()).thenReturn(restTemplate);
        when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        ResponseEntity responseEntity1 = new ResponseEntity<ScheduleJobResponse>(scheduleJobResponse, HttpStatus.OK);
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ScheduleJobRequest>>any());
        ScheduleJobsForm jobsForm = scheduleJobsController.scheduleJob(scheduleJobsForm);
        assertNotNull(jobsForm);
    }
    @Test
    public void scheduleJobException(){
        ScheduleJobsForm scheduleJobsForm = getScheduleJobsForm();
        scheduleJobsForm.setScheduleType(RecapConstants.UNSCHEDULE);
        ScheduleJobResponse scheduleJobResponse = getScheduleJobResponse();
        scheduleJobResponse.setMessage("Failure");
        when(scheduleJobsController.getRestTemplate()).thenReturn(restTemplate);
        when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ScheduleJobRequest>>any());
        ScheduleJobsForm jobsForm = scheduleJobsController.scheduleJob(scheduleJobsForm);
        assertNotNull(jobsForm);
    }

    private ScheduleJobResponse getScheduleJobResponse() {
        ScheduleJobResponse scheduleJobResponse = new ScheduleJobResponse();
        scheduleJobResponse.setMessage("SUCCESS");
        scheduleJobResponse.setNextRunTime(new Date());
        return scheduleJobResponse;
    }

    private JobEntity getJobEntity() {
        JobEntity jobEntity = new JobEntity();
        jobEntity.setId(1);
        jobEntity.setJobName("Test");
        jobEntity.setCronExpression("0 53 19 1/1 * ? *");
        jobEntity.setJobDescription("Test");
        jobEntity.setLastExecutedTime(new Date());
        jobEntity.setStatus("Success");
        jobEntity.setNextRunTime(new Date());
        return jobEntity;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setRecapPermissionAllowed(true);
        return userDetailsForm;
    }

    private ScheduleJobsForm getScheduleJobsForm(){
        ScheduleJobsForm scheduleJobsForm = new ScheduleJobsForm();
        scheduleJobsForm.setCronExpression("cron");
        scheduleJobsForm.setErrorMessage("error");
        scheduleJobsForm.setJobDescription("description");
        scheduleJobsForm.setJobId(1);
        scheduleJobsForm.setJobName("SCHEDULEDER");
        scheduleJobsForm.setMessage("SUCCESS");
        scheduleJobsForm.setScheduleType("UNSCHEDULE");
        return scheduleJobsForm;
    }
}
