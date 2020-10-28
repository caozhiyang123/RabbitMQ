package com.gamesmart.client;

import java.io.IOException;

import com.gamesmart.util.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RPCServer {
	private static final String QUEUE_RPC = "queue_rpc";

    public static void main(String[] args){
    	try{
    		RPCServer rpcServer = new RPCServer();
    		rpcServer.Server();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void Server() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                    throws IOException {
                System.out.println(new String(body));

                AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
                builder.correlationId(properties.getCorrelationId());
                AMQP.BasicProperties prop = builder.build();

                channel.basicPublish("", properties.getReplyTo(), prop, ("reply: "+new String(body)).getBytes());
            }
        };
        channel.basicConsume(QUEUE_RPC, true, consumer);
    }

}