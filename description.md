# 爬虫细节描述

本页用于开发流程的思路理解和描述


# 工作流程设计(初稿)

1. Master节点启动，Client节点启动并连接注册到master，定时发送心跳
2. Master记录心跳信息作为元数据
3. 用户提交一个Job(初步设定是一个文件夹形式，包括jar和相关配置信息在内)
4. Master节点检测Job合法性
5. 任务初始化：1. 将任务下发到一个节点进行种子URL的爬取；2. Master分割种子URL到各个Worker。并将用户提交文件打包分发到worker节点
6. 解压用户文件，并运行任务，生成新的URL
7. 根据策略，如果需要去重检测则将新生成的URL全部提交到worker，否则提交一部分到master供其他节点工作
8. 某个节点完成当前任务可以向Master主动申请新任务
9. 任务完成后master告知client节点进行清理
10. 需要处理client和master节点可能出现的情况
11. 用户可以删除线上任务
12. Master节点可以统计运行情况(定期持久化)