package org.recap.model.schedule;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Created by rajeshbabuk on 5/4/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ScheduleJobRequest {

    private Integer jobId;
    private String jobName;
    private String cronExpression;
    private String scheduleType;

}
