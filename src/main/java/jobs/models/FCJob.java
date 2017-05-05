package jobs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicLong;

public class FCJob {

    private final String id;
    private long total; //total size of the job
    private final AtomicLong progress = new AtomicLong(0); //current progress of the job such that progress == total means completion
    @JsonIgnore
    private DateTime lastModified;//jobs older than 1 minute expire

    public FCJob(String id, long total) {
        this.id = id;
        this.total = total;
        lastModified = new DateTime();
    }

    public FCJob()
    {
        this.id = "";
        this.total = 0;
        lastModified = new DateTime();
    }

    public FCJob(long total)
    {
        this.id = "";
        this.total = total;
        lastModified = new DateTime();
    }

    public String getId() {
        return id;
    }

    public long getTotal() {
        return total;
    }

    public long getProgress() {
        return progress.get();
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public FCJob setAndGetProgress(long progress) {
        this.progress.updateAndGet(n -> progress);
        this.lastModified = new DateTime();
        return this;
    }

    public FCJob updateAndGetProgress(long progress) {
        this.progress.accumulateAndGet(progress, (n,x) -> n + x);
        lastModified = new DateTime();
        return this;
    }

}
