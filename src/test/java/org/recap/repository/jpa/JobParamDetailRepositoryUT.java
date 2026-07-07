package org.recap.repository.jpa;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCase;
import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.JobParamEntity;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by rajeshbabuk on 17/May/2021
 */
public class JobParamDetailRepositoryUT extends BaseTestCase {

    @Autowired
    JobParamDetailRepository jobParamDetailRepository;

    @Disabled
    @Test
    public void findByJobName() throws Exception {
        JobParamEntity byJobName = jobParamDetailRepository.findByJobName(ScsbCommonConstants.GENERATE_ACCESSION_REPORT_JOB);
        assertNotNull(byJobName);
        assertNotNull(byJobName.getJobName());
        assertEquals(ScsbCommonConstants.GENERATE_ACCESSION_REPORT_JOB, byJobName.getJobName());
    }
}