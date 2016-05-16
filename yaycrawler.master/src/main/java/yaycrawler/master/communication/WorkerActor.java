package yaycrawler.master.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.master.model.MasterContext;
import yaycrawler.master.service.WorkInfoService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class WorkerActor {

    @Autowired
    private WorkInfoService workInfoService;

    public void assignTasks() {
        ConcurrentHashMap<String, WorkerRegistration> workerListMap = MasterContext.workerRegistrationMap;
        for (WorkerRegistration workerRegistration : workerListMap.values()) {
            List<CrawlerRequest> crawlerRequestList = workInfoService.listWorks(10);
            String targetUrl = CommunicationAPIs.getFullRemoteUrl(workerRegistration.getWorkerContextPath(), CommunicationAPIs.MASTER_POST_WORKER_TASK_ASSIGN);
            RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, crawlerRequestList);
            if (result==null||result.hasError()) {

            } else {
                workInfoService.moveRunningQueue(crawlerRequestList);
            }
        }

    }

    public void regeditTasks(List<CrawlerRequest> crawlerRequestList) {
        workInfoService.regeditWorks(crawlerRequestList);
    }

    public void removeTask(String key ) {
        workInfoService.removeCrawler(key);
    }

}
