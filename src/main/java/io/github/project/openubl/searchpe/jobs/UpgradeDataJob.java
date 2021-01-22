/**
 * Copyright 2019 Project OpenUBL, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.project.openubl.searchpe.jobs;

import io.github.project.openubl.searchpe.managers.UpgradeDataManager;
import io.github.project.openubl.searchpe.models.jpa.entity.Status;
import io.github.project.openubl.searchpe.models.jpa.entity.VersionEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import org.quartz.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.*;
import java.util.Date;
import java.util.UUID;

@ApplicationScoped
public class UpgradeDataJob {

    @Inject
    Scheduler quartz;

    @Inject
    UpgradeDataManager upgradeDataManager;

    @Inject
    UserTransaction tx;

    public void trigger(VersionEntity version) throws SchedulerException {
        String versionId = String.valueOf(version.id);

        String jobId = UUID.randomUUID().toString();
        JobDetail job = JobBuilder.newJob(MyJob.class)
                .withIdentity(jobId, "ImportData")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobId, "ImportData")
                .usingJobData("versionId", versionId)
                .startNow()
                .build();
        quartz.scheduleJob(job, trigger);
    }

    @Scheduled(cron = "{searchpe.scheduled.cron}")
    void schedule() {
        VersionEntity version;

        try {
            tx.begin();

            Date currentTime = new Date();

            version = VersionEntity.Builder.aVersionEntity()
                    .withCreatedAt(currentTime)
                    .withUpdatedAt(currentTime)
                    .withStatus(Status.SCHEDULED)
                    .withRecords(0)
                    .build();
            version.persist();
            version.flush();

            tx.commit();
        }  catch (NotSupportedException | HeuristicRollbackException | HeuristicMixedException | RollbackException | SystemException e) {
            try {
                tx.rollback();
            } catch (SystemException se) {
                throw new IllegalStateException(se);
            }
            return;
        }

        upgradeDataManager.upgrade(version.id);
    }

    void upgradeData(Long versionId) {
        upgradeDataManager.upgrade(versionId);
    }

    @RegisterForReflection
    public static class MyJob implements Job {
        @Inject
        UpgradeDataJob job;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            String versionId = (String) context.getTrigger().getJobDataMap().get("versionId");
            job.upgradeData(Long.valueOf(versionId));
        }
    }
}
