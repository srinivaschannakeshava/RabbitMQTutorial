package com.srini.clients;

import java.util.Scanner;

public class PublishMain {
	public static void main(String[] args) {
		RBPublishClient publisher = new RBPublishClient();
		Scanner s = new Scanner(System.in);
		String command = "";
		System.out.println("Enter the command : ");
		while (!command.equalsIgnoreCase("stop")) {
			command = s.next();
			if (command.equalsIgnoreCase("p")) {
				System.out.println("Basic Publish messages ");
				try {
					publisher.randomBasicPublishStart("queue1");
					Thread.sleep(50000);
					publisher.randomPublishStop();
					System.out.println("Basic Publish messages **DONE** ");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (command.equalsIgnoreCase("e")) {
				System.out.println("Exchange fan-out Publish messages ");
				try {
					publisher.randomFanExchangePublishStart("logs");
					Thread.sleep(50000);
					publisher.randomPublishStop();
					System.out.println("Exchange fan-out Publish messages **DONE**");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (command.equalsIgnoreCase("d")) {
				System.out.println("Exchange direct Publish messages ");
				try {
					publisher.randomDirectExchangePublishStart("direct_logs");
					Thread.sleep(50000);
					publisher.randomPublishStop();
					System.out.println("Exchange direct Publish messages **DONE**");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (command.equalsIgnoreCase("t")) {
				System.out.println("Exchange Topic Publish messages ");
				try {
					publisher.randomTopicExchangePublishStart("topic_exchange");
					Thread.sleep(50000);
					publisher.randomPublishStop();
					System.out.println("Exchange direct Publish messages **DONE**");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Enter the command : ");

		}

	}
}
