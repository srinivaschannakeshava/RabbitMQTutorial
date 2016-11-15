package com.srini.clients;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP.BasicProperties;

public class RBSubscribeClient {
	String server = "localhost";
	Connection connection;
	Channel basicChannel, exchangeChannel;
	int msgCount = 0;
	int logCount = 0;

	public void createBasicChannel(String queueName) throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		basicChannel.queueDeclare(queueName, true, false, false, null);
		System.out.println("Queue Declared");
	}

	public String createFanOutExchangeChannel(String exchangeName) throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		exchangeChannel.exchangeDeclare("logs", "fanout");
		String queueName = exchangeChannel.queueDeclare().getQueue();
		exchangeChannel.queueBind(queueName, exchangeName, "");
		System.out.println("Exchange Declared and Listening");
		return queueName;
	}

	public String createDirectExchangeChannel(String exchangeName, String bindingKey)
			throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		exchangeChannel.exchangeDeclare("direct_logs", "direct");
		String queueName = exchangeChannel.queueDeclare().getQueue();
		exchangeChannel.queueBind(queueName, exchangeName, bindingKey);
		System.out.println("Direct Exchange Declared and Listening");
		return queueName;
	}
	public String createTopicExchangeChannel(String exchangeName, String bindingKey)
			throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		exchangeChannel.exchangeDeclare("topic_exchange", "topic");
		String queueName = exchangeChannel.queueDeclare().getQueue();
		exchangeChannel.queueBind(queueName, exchangeName, bindingKey);
		System.out.println("Topic Exchange Declared and Listening");
		return queueName;
	}

	public void getConnection() throws IOException, TimeoutException {
		ConnectionFactory cf = new ConnectionFactory();
		cf.setHost(server);
		cf.setPort(5673);

		// cf.portOrDefault(5673, false);
		connection = cf.newConnection();
		basicChannel = connection.createChannel();
		exchangeChannel = connection.createChannel();
		System.out.println("Connection and channel created : ");
	}

	public void exchangeFanSubscribeAndListen(String exchangeName) throws IOException, TimeoutException {
		String queueName = createFanOutExchangeChannel(exchangeName);
		Consumer consumer = new DefaultConsumer(exchangeChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				// TODO Auto-generated method stub
				logCount++;
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "' : logCount " + logCount);
			}

		};
		exchangeChannel.basicConsume(queueName, true, consumer);

	}

	public void exchangeDirectSubscribeAndListen(String exchangeName,String bindingKey) throws IOException, TimeoutException {
		String queueName = createDirectExchangeChannel(exchangeName,bindingKey);
		Consumer consumer = new DefaultConsumer(exchangeChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				// TODO Auto-generated method stub
				logCount++;
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "' : logCount " + logCount);
			}

		};
		exchangeChannel.basicConsume(queueName, true, consumer);

	}
	
	public void exchangeTopicSubscribeAndListen(String exchangeName,String bindingKey) throws IOException, TimeoutException {
		String queueName = createTopicExchangeChannel(exchangeName,bindingKey);
		Consumer consumer = new DefaultConsumer(exchangeChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				// TODO Auto-generated method stub
				logCount++;
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message +" : "+ envelope.getExchange()+"' : logCount " + logCount);
			}

		};
		exchangeChannel.basicConsume(queueName, true, consumer);

	}

	public void basicSubscribeAndListen(String queueName) throws IOException, TimeoutException {
		createBasicChannel(queueName);

		Consumer cons = new DefaultConsumer(basicChannel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				// TODO Auto-generated method stub
				msgCount++;
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "' : msgCount " + msgCount);
				basicChannel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		basicChannel.basicConsume(queueName, false, cons);
	}

}
