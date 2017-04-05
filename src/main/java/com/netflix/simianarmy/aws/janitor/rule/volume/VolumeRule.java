package com.netflix.simianarmy.aws.janitor.rule.volume;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.netflix.simianarmy.MonkeyCalendar;
import com.netflix.simianarmy.Resource;
import com.netflix.simianarmy.aws.AWSResource;
import com.netflix.simianarmy.janitor.JanitorMonkey;
import com.netflix.simianarmy.janitor.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VolumeRule implements Rule {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VolumeRule.class);

    /** The date format used to print or parse the user specified termination date. **/
    public static final DateTimeFormatter TERMINATION_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");


    public boolean isValid(Resource resource) {
        Validate.notNull(resource);
        if (!resource.getResourceType().name().equals("EBS_VOLUME")) {
            return true;
        }
        if (!"available".equals(((AWSResource) resource).getAWSResourceState())) {
            return true;
        }
        String janitorTag = resource.getTag(JanitorMonkey.JANITOR_TAG);
        if (janitorTag != null) {
            if ("donotmark".equals(janitorTag)) {
                LOGGER.info(String.format("The volume %s is tagged as not handled by Janitor",
                        resource.getId()));
                return true;
            }
            try {
                // Owners can tag the volume with a termination date in the "janitor" tag.
                Date userSpecifiedDate = new Date(
                        TERMINATION_DATE_FORMATTER.parseDateTime(janitorTag).getMillis());
                resource.setExpectedTerminationTime(userSpecifiedDate);
                resource.setTerminationReason(String.format("User specified termination date %s", janitorTag));
                return false;
            } catch (Exception e) {
                LOGGER.error(String.format("The janitor tag is not a user specified date: %s", janitorTag));
            }
        }
        
        return isValidResource(resource);
        
    }
    
    abstract boolean isValidResource(Resource resource);
    
}
