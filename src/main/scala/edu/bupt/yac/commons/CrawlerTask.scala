package edu.bupt.yac.commons

/**
 * Created by chenlingpeng on 15/9/21.
 */
class CrawlerTask(val taskId: Int, val crawlerJob: CrawlerJob) {

}

class CrawlerTaskAttempt(val attemptId: Int, val crawlerTask: CrawlerTask) {

}
