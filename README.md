# RabbitMQ
different exchange type/RPC
installing RabbitMQ service:
1>installing Erlang language(https://www.erlang.org/downloads),
  ubuntu: apt-get install erlang
2>installing MQ service(https://blog.csdn.net/s_lisheng/article/details/79580601) ,
  ubuntu: sudo apt-get install rabbitmq-server
3>login failed(https://blog.csdn.net/doubleqinyan/article/details/81081673),
  step1 input cmd:rabbitmqctl set_user_tags guest administrator
  step2 input cmd:rabbitmqctl set_permissions -p / guest '.*' '.*' '.*'
  step3 touch rabbitmq.config in /etc/rabbitmq, then edit this file ,input '[{rabbit, [{loopback_users, []}]}].' then save it
  step4 service rabbitmq-server stop
  step5 service rabbitma-server start
  step6 service rabbitma-server status
4>try login again(http://192.168.148.128:15672/#/),my local ubuntu ip is '192.168.148.128' you can replace it by yourselves,default port is 15672
