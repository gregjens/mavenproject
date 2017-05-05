package jobs.controllers;

import jobs.models.FCJob;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController()
public class FCJobController {

    private static final int EXPIRATION_IN_MINUTES = 1;

    private final AtomicLong idCounter = new AtomicLong();

    private ConcurrentHashMap<String, FCJob> jobsMap = new ConcurrentHashMap<String, FCJob>();

    @RequestMapping(method = GET, value = "/job/{id}")
    public ResponseEntity<FCJob> getFCJob(@PathVariable(value = "id") String id) {

        if (!jobsMap.containsKey(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FCJob fcJob = jobsMap.get(id);

        if (isExpired(fcJob.getLastModified())) {
            jobsMap.remove(id);
            return new ResponseEntity<>(HttpStatus.GONE);
        }

        return ResponseEntity.ok(fcJob);
    }

    @RequestMapping(method = GET, path = "/jobs")
    public @ResponseBody Collection<FCJob> getFCJobs() {

        Collection<FCJob> fcJobs = jobsMap.values();
        for (FCJob fcJob : fcJobs) {
            if (isExpired(fcJob.getLastModified())) {
                jobsMap.remove(fcJob.getId());
            }
        }

        return jobsMap.values();
    }

    @RequestMapping(method = PUT, value = "/job/{id}/setProgress/{amount}")
    public ResponseEntity<FCJob> updateFCJobProgress(@PathVariable(value = "id") String id, @PathVariable(value = "amount") long amount) {

        if (!jobsMap.containsKey(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FCJob fcJob = jobsMap.get(id);

        return ResponseEntity.ok(fcJob.setAndGetProgress(amount));
    }

    @RequestMapping(method = PUT, value = "/job/{id}/addProgress/{amount}")
    public ResponseEntity<FCJob> incrementFCJobProgress(@PathVariable(value = "id") String id, @PathVariable(value = "amount") long amount) {

        if (!jobsMap.containsKey(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FCJob fcJob = jobsMap.get(id);

        return ResponseEntity.ok(fcJob.updateAndGetProgress(amount));
    }

    @RequestMapping(method = POST, value = "/job", consumes = "application/json")
    public ResponseEntity<FCJob> createFCJob(@RequestBody FCJob job) {

        FCJob fcJob = new FCJob(String.valueOf(idCounter.incrementAndGet()), job.getTotal());
        jobsMap.put(String.valueOf(fcJob.getId()), fcJob);

        return ResponseEntity.ok(fcJob);
    }

    private Boolean isExpired(DateTime then) {

        DateTime now = new DateTime();
        Interval interval = new Interval(then, now);
        if (interval.toDuration().getStandardMinutes() > EXPIRATION_IN_MINUTES) {
            return true;
        }

        return false;
    }
}
