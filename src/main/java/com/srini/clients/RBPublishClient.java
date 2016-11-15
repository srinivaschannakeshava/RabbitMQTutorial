package com.srini.clients;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class RBPublishClient {
	String server = "localhost";
	Connection connection;
	Channel basicChannel;
	Channel exchangeChannel, directExchangeChannel, topicExchangeChannel;
	ScheduledExecutorService scheduledExecutorService;
	ScheduledFuture scheduledFuture;
	int count = 0;

	public void createBasicChannel(String queueName) throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		if (basicChannel == null) {
			basicChannel.queueDeclare(queueName, true, false, false, null);
			System.out.println("Queue Declared");
		}
	}

	public void createExchangeChannel(String ExchangeName) throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		if (exchangeChannel == null) {
			// declare Exchange
			exchangeChannel.exchangeDeclare("logs", "fanout");
			System.out.println("Exchange Declared");
		}
	}

	public void createDirectExchangeChannel(String ExchangeName) throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		if (directExchangeChannel == null) {
			// declare Exchange
			directExchangeChannel.exchangeDeclare("logs", "direct");
			System.out.println(" Direct Exchange Declared");
		}
	}
	
	public void createTopicExchangeChannel(String ExchangeName) throws IOException, TimeoutException {
		if (connection == null) {
			getConnection();
		}
		if (topicExchangeChannel == null) {
			// declare Exchange
			topicExchangeChannel.exchangeDeclare("topic_exchange", "topic");
			System.out.println(" Topic Exchange Declared");
		}
	}

	public void getConnection() throws IOException, TimeoutException {
		ConnectionFactory cf = new ConnectionFactory();
		cf.setHost(server);
		cf.setPort(5673);

		// cf.portOrDefault(5673, false);
		connection = cf.newConnection();
		basicChannel = connection.createChannel();
		exchangeChannel = connection.createChannel();
		directExchangeChannel = connection.createChannel();
		topicExchangeChannel=connection.createChannel();
		System.out.println("Connection and channel created : ");
	}

	public void randomBasicPublishStart(String queueName) throws IOException, TimeoutException {
		createBasicChannel(queueName);
		scheduledExecutorService = Executors.newScheduledThreadPool(5);
		scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					count++;
					basicChannel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,
							(count + "").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 100, 10, TimeUnit.MILLISECONDS);
	}

	public void randomFanExchangePublishStart(String exchangeName) throws IOException, TimeoutException {
		createExchangeChannel(exchangeName);
		scheduledExecutorService = Executors.newScheduledThreadPool(5);
		scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					count++;
					exchangeChannel.basicPublish(exchangeName, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
							("Log count : " + count).getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 100, 10, TimeUnit.MILLISECONDS);
	}

	public void randomDirectExchangePublishStart(String exchangeName) throws IOException, TimeoutException {
		createDirectExchangeChannel(exchangeName);
		scheduledExecutorService = Executors.newScheduledThreadPool(5);
		scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					count++;
					// routing keys
					if (count % 100 == 0) {
						exchangeChannel.basicPublish(exchangeName, "error", MessageProperties.PERSISTENT_TEXT_PLAIN,
								("[error] Log count : " + count).getBytes());
					} else if (count % 50 == 0) {
						exchangeChannel.basicPublish(exchangeName, "warning", MessageProperties.PERSISTENT_TEXT_PLAIN,
								("[warning] Log count : " + count).getBytes());
					} else {
						exchangeChannel.basicPublish(exchangeName, "info", MessageProperties.PERSISTENT_TEXT_PLAIN,
								("[info] Log count : " + count).getBytes());
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 100, 10, TimeUnit.MILLISECONDS);
	}

	public void randomTopicExchangePublishStart(String exchangeName) throws IOException, TimeoutException {
		createTopicExchangeChannel(exchangeName);
		scheduledExecutorService = Executors.newScheduledThreadPool(5);
		scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					count++;
					// routing keys
					if (count % 100 == 0) {
						topicExchangeChannel.basicPublish(exchangeName, "srini.error.hello", MessageProperties.PERSISTENT_TEXT_PLAIN,
								("[error] Log count : " + count).getBytes());
					} else if (count % 50 == 0) {
						topicExchangeChannel.basicPublish(exchangeName, "kavya.warning.hello2", MessageProperties.PERSISTENT_TEXT_PLAIN,
								("[warning] Log count : " + count).getBytes());
					} else {
						topicExchangeChannel.basicPublish(exchangeName, "navya.info.test", MessageProperties.PERSISTENT_TEXT_PLAIN,
								("[info] Log count : " + count).getBytes());
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 100, 10, TimeUnit.MILLISECONDS);
	}
	public void randomPublishStop() {
		scheduledFuture.cancel(true);
		scheduledExecutorService.shutdown();
	}
}
