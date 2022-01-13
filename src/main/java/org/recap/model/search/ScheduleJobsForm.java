package org.recap.model.search;


import lombok.Data;
import org.recap.model.jpa.JobEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 4/4/17.
 */
@Data
public class ScheduleJobsForm {

    private Integer jobId;
    private String jobName;
    private String jobDescription;
    private String cronExpression;
    private String scheduleType;
    private String jobParameter;
    private String message;
    private String errorMessage;
    private List<JobEntity> jobEntities = new ArrayList<>();

}
