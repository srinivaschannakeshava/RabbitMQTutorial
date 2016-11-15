package com.srini.clients;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Subscriber2Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RBSubscribeClient subClient = new RBSubscribeClient();
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("Enter the subscription MODE (basic(b)/exchange(e)) : ");
			String command = in.next();
			if (command.equalsIgnoreCase("b")) {
				System.out.println("------- Basic Subscribtion -----");
				subClient.basicSubscribeAndListen("queue1");
			} else if (command.equalsIgnoreCase("e")) {
				System.out.println("------- Exchange Subscribtion -----");
				subClient.exchangeFanSubscribeAndListen("logs");
			} else if (command.equalsIgnoreCase("d")) {
				System.out.println("-------Direct Exchange Subscribtion -----");
				subClient.exchangeDirectSubscribeAndListen("direct_logs", "warning");
			}else if(command.equalsIgnoreCase("t")){
				System.out.println("-------Topic Exchange Subscribtion -----");
				subClient.exchangeTopicSubscribeAndListen("topic_exchange","srini.#");
			}
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
