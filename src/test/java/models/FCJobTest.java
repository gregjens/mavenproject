package models;

import jobs.models.FCJob;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FCJobTest {

    private FCJob job;

    @Before
    public void setup () {

        job = new FCJob(100);
    }

    @Test
    public void incrementProgress() {

        job = job.updateAndGetProgress(5);
        job = job.updateAndGetProgress(5);
        assertEquals("progress should equal 10", 10, job.getProgress());
    }

    @Test
    public void setProgress() {

        job = job.setAndGetProgress(25);
        assertEquals("progress should equal 25", 25, job.getProgress());
    }
}
