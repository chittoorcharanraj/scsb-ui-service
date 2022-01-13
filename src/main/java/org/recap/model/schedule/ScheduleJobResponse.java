package org.recap.model.schedule;


import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.Date;

/**
 * Created by rajeshbabuk on 5/4/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ScheduleJobResponse {

    private String message;
    private Date nextRunTime;

}
