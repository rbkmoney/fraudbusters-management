bash -c 'echo Waiting for Kafka to be ready... && \n 
cub kafka-ready -b broker:9092 1 60 && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic wb-list-event-sink  && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic wb-list-command && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic unknown_initiating_entity   && \n
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_p2p && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_reference && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_p2p_reference && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_list && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_p2p_list && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_reference && \n 
kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_p2p_reference && \n 
echo Waiting 60 seconds for Connect to be ready... && \n 
sleep 60'
