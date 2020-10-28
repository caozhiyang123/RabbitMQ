package com.gamesmart.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gamesmart.util.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;

public class Producer {
	private static final String QUEUE_NAME = "hello word";
	private static final String QUEUE_NAME2 = "hello word2";
	private static final String EXCHANG_FANOUT = "amq.fanout";
	private static final String EXCHANG_DIRECT = "amq.direct";
	private static final String FANOUT = "fanout";
	private static final String DIRECT = "direct";
	private static final String BINDING_KEY = "test1";
	private static final String BINDING_KEY2 = "test2";
	private static final String ROUTING_KEY = "test1";
	
	public static void main(String[] args){
		Connection connection = null;
		Channel channel = null;
		try {
			connection = ConnectionUtil.getConnection();
			channel = connection.createChannel();
			//fanoutTypeExchange(channel);
			directTypExchange(channel);
		} catch (Exception e) {
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

	public static void directTypExchange(Channel channel) throws Exception {
		//exchange type "direct"
		channel.confirmSelect();
		channel.exchangeDeclare(EXCHANG_DIRECT, DIRECT,true);
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.queueDeclare(QUEUE_NAME2, false, false, false, null);
		channel.queueBind(QUEUE_NAME, EXCHANG_DIRECT, BINDING_KEY);
		channel.queueBind(QUEUE_NAME2, EXCHANG_DIRECT, BINDING_KEY2);
		for(int i=0;i<10;i++){
			if(i==9){
				channel.basicPublish(EXCHANG_DIRECT, "miss", true,null, (i+"hell!word!!").getBytes());
			}
			//set every message flag
			Map<String, Object> headers = new HashMap<String, Object>();
            headers.put("num" ,i);
			AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .headers(headers)
                    .build();
			
			//@fourth param is message properties
			//@third param set true indicates that miss message will be auto deleted
			channel.basicPublish(EXCHANG_DIRECT, ROUTING_KEY, true,properties, (i+"hell!word!!").getBytes());
			//1.0 confirm everyone message
			/*if(channel.waitForConfirms()){
				continue;
			}*/
		}
		//2.0 batch confirm messages
		channel.addConfirmListener(new ConfirmListener(){

			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("handle success!!! deliveryTag:"+deliveryTag+", multiple:"+multiple);
			}

			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("handle failed!!! deliveryTag:"+deliveryTag+", multiple:"+multiple);
			}
			
		});
		
		//add return listener
		channel.addReturnListener(new ReturnListener() {
			@Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("return relyCode: " + replyCode);
                System.out.println("return replyText: " + replyText);
                System.out.println("return exchange: " + exchange);
                System.out.println("return routingKey: " + routingKey);
                System.out.println("return properties: " + properties);
                System.out.println("return body: " + new String(body));
            }
        });
	}

	public static void fanoutTypeExchange(Channel channel) throws IOException {
		//exchange type "fanout"
		channel.confirmSelect();
		channel.exchangeDeclare(EXCHANG_FANOUT, DIRECT,true);
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.queueDeclare(QUEUE_NAME2, false, false, false, null);
		channel.queueBind(QUEUE_NAME, EXCHANG_FANOUT, BINDING_KEY);
		channel.queueBind(QUEUE_NAME2, EXCHANG_FANOUT, BINDING_KEY2);
		for(int i=0;i<10;i++){
			channel.basicPublish(EXCHANG_FANOUT, ROUTING_KEY, null, (i+"hell!word!!").getBytes());
		}
	}
}
