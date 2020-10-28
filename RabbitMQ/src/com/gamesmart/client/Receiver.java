package com.gamesmart.client;

import java.io.IOException;

import com.gamesmart.util.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Receiver {
	private static final String QUEUE_NAME = "hello word";
	private static final String QUEUE_NAME2 = "hello word2";
	
	public static void main(String[] args){
		Connection connection;
		final Channel channel;
		try{
			connection = ConnectionUtil.getConnection();
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			DeclareOk declareOk = channel.queueDeclarePassive(QUEUE_NAME);
		    int num = declareOk.getMessageCount();
		    if(num < 0) {return;}
		    
			Consumer consumer = new DefaultConsumer(channel) {
	            @Override
	            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	                    throws IOException {
	            	//exchange
	                String exchange = envelope.getExchange();
	                //routing key
	                String routingKey = envelope.getRoutingKey();
	                //message id
	                long deliveryTag = envelope.getDeliveryTag();
	                
	                String msg = new String(body, "UTF-8");
	                System.out.println("Received is = '" + msg + "'");
	                
                    try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	                
	                if ((Integer) properties.getHeaders().get("num") == 8) {
	                	/*System.out.println("nack message:"+msg);
	                	channel.basicNack(envelope.getDeliveryTag(), false, true);*/
	                	channel.basicAck(deliveryTag, false);
	                }else{
	                	channel.basicAck(deliveryTag, false);
	                }
	            }
	        };
	        channel.basicConsume(QUEUE_NAME, false, consumer);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			/*try {
				channel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}
}
