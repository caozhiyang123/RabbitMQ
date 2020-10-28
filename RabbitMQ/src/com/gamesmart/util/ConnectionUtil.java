package com.gamesmart.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtil {
	public static Connection getConnection() throws Exception {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost("192.168.148.128");//rabbitmq-server ip
			connectionFactory.setPort(5672);//default port
			connectionFactory.setUsername("guest");//default user
			connectionFactory.setPassword("guest");//default pass
			connectionFactory.setVirtualHost("/");//default host
			return connectionFactory.newConnection();
		}
}
