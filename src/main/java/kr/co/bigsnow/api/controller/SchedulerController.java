package kr.co.bigsnow.api.controller;

import kr.co.bigsnow.core.controller.StandardController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component

@SpringBootApplication
@EnableScheduling

public class SchedulerController extends StandardController {

    @Scheduled(cron = "0 0,30 * * * *")
    public void BatchLession() {

        Map<String, Object> mapReq = new HashMap();

        try {

            System.out.println("===================[Lesson Batch Start]======================");
            System.out.println(new Date().toString());

            dbSvc.dbInsert("lesson.insertLessionBatch", mapReq); // 수강정보 생성
            dbSvc.dbUpdate("lesson.updateLectureRoundBatch", mapReq);  // 현 진행회차

            System.out.println("===================[Lesson Batch End]======================");

        } catch (Exception e) {

            log.error(e.getMessage());

        }
    }

    @Scheduled(cron = "0 0,10,20,30,40,50 * * * *")
    public void BatchAttend() {

        try {

            System.out.println("===================[Attend Batch Start]======================");
            System.out.println(new Date().toString());

            List lstRs = dbSvc.dbList("lesson.lectureAppListBatch");

            if (lstRs != null && !lstRs.isEmpty()) {

                for (int nLoop = 0; nLoop < lstRs.size(); nLoop++) {
                    Map mapRs = (Map) lstRs.get(nLoop);

                    mapRs.put("att_yn", "N");

                    try {
                        dbSvc.dbInsert("lesson.insertAttendanceBatch", mapRs);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                    dbSvc.dbUpdate("lesson.updateLectureAppAtt", mapRs);
                }

            }

            System.out.println("===================[Attend Batch End]======================");

        } catch (Exception e) {

            log.error(e.getMessage());

        }
    }
}

