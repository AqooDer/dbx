package com.dbx.core;

import com.dbx.core.exception.JobException;
import com.dbx.core.job.JobFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 支持多 DbTransferContainerProcessor 解析起
 * 比如 annotation ， xml ==
 *
 * @author Aqoo
 */
@Slf4j
public class BasicJobRunner implements JobRunner {

    private final JobFactory[] jobFactory;

    public BasicJobRunner(JobFactory... jobFactory) {
        if (jobFactory == null) {
            throw new JobException("the params jobFactory is null , please check.");
        }
        this.jobFactory = jobFactory;
    }

    @Override
    public void run() {
        loadBanner();
        for (JobFactory factory : jobFactory) {
            factory.getJob().run();
        }
    }

    private void loadBanner() {
        try {
            Path bannerPath = Paths.get(BasicJobRunner.class.getClassLoader().getResource("banner.txt").toURI());
            String bannerContent = new String(Files.readAllBytes(bannerPath));
            System.out.println(bannerContent);
        } catch (IOException | URISyntaxException e) {
            // ignore
        }
    }
}
