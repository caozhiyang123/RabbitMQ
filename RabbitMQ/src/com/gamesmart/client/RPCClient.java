package com.gamesmart.client;

import java.io.IOException;
import java.util.UUID;

import com.gamesmart.util.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RPCClient {
	private static final String QUEUE_RPC = "queue_rpc";
	private static final String EXCHANGE_RPC = "exchange_rpc";
	private static final String ROUNTING_KEY = "rpc";
	private static final String EXCHANGE_TYPE = "direct";
	
    public static void main(String[] args){
        try {
        	RPCClient rpcClient = new RPCClient();
			rpcClient.request();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void request() throws Exception {
    	Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();
        channel.exchangeDelete(EXCHANGE_RPC);
        channel.exchangeDeclare(EXCHANGE_RPC, EXCHANGE_TYPE, false, false, null);

        channel.queueDelete(QUEUE_RPC);
        channel.queueDeclare(QUEUE_RPC, false, false, false, null);

        channel.queueBind(QUEUE_RPC, EXCHANGE_RPC, ROUNTING_KEY);

        String callbackQueueName  = channel.queueDeclare().getQueue();
        final String correlation = UUID.randomUUID().toString();

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.replyTo(callbackQueueName).correlationId(correlation);
        AMQP.BasicProperties properties = builder.build();
        for (int i = 0; i < 2; i++) {
            channel.basicPublish(EXCHANGE_RPC, ROUNTING_KEY, properties,
                    (System.currentTimeMillis() + "-rpc send message 1").getBytes());
        }

        AMQP.BasicProperties.Builder builder2 = new AMQP.BasicProperties.Builder();
        builder2.replyTo(callbackQueueName );
        AMQP.BasicProperties properties2 = builder2.build();
        for (int i = 0; i < 2; i++) {
            channel.basicPublish(EXCHANGE_RPC, ROUNTING_KEY, properties2,
                    (System.currentTimeMillis() + "-rpc send message 2").getBytes());
        }

        
        /*for (int i = 0; i < 2; i++) {
            channel.basicPublish(EXCHANGE_RPC, ROUNTING_KEY, null, (System.currentTimeMillis() + "-rpc send message 3").getBytes());
        }*/

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
                    throws IOException {
                if (correlation.equals(properties.getCorrelationId())) {
                    System.out.println("correlationID message£º" + new String(body));
                } else {
                    System.out.println("correlationID message£º" + new String(body));
                }
            }
        };
        channel.basicConsume(callbackQueueName , true, consumer);
    }

}