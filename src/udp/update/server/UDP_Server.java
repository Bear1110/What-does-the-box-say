package udp.update.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import renderer.data.DynamicObjectManager;

public class UDP_Server implements Runnable {

	public static void initUDPServer() {
		Thread serverThread = new Thread(new UDP_Server()); // 產生Thread物件
		serverThread.start();
	}

	public void run() {
		DatagramSocket serverSocket = null;
		try {
			byte[] receiveData;
			serverSocket = new DatagramSocket(3335);

			while (true) {
				receiveData = new byte[1024];
				DatagramPacket receivePacket =
						new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				// get EncodedData in ArrayList and parse.
				String receiveString =
						new String(receivePacket.getData()).trim();
				parseData(receiveString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}
	}

	private Gson gson = new Gson();

	public void parseData(String receiveString) {
		ArrayList<EncodedData> encodedData = gson.fromJson(receiveString,
				new TypeToken<ArrayList<EncodedData>>() {}.getType());

		//System.out.println(receiveString);

		ClientPlayerFeature player = null;
		ClientItemFeature item = null;
		DynamicObjectManager instance = DynamicObjectManager.getInstance();
		for (EncodedData eachEnData : encodedData) {
			switch (eachEnData.getType()) {
				case "UpdateP":
					player = gson.fromJson(eachEnData.getData(),
							ClientPlayerFeature.class);
					instance.updateVirtualCharacter(player.getClientNo(),
							player.getWeaponType(), player.getNickname(),
							player.getLocX(), player.getLocY(),
							player.getFaceAngle(), player.getHP(),
							player.getKillCount(), player.getDeadCount(),
							player.getBulletCount(), player.isAttackFlag(),
							player.isAttackedFlag(), player.isCollisionFlag(),
							player.isDead());
					break;
				case "AddP":
					player = gson.fromJson(eachEnData.getData(),
							ClientPlayerFeature.class);
					instance.addVirtualCharacter(player.getClientNo(),
							player.getNickname());

					System.out.println("Add Player");
					break;
				case "UpdateI":
					item = gson.fromJson(eachEnData.getData(),
							ClientItemFeature.class);
					instance.updateItem(item.getItemID(), item.isDead(),
										item.getItemOwner());

					System.out.println("Update Item");
					break;
				case "AddI":
					item = gson.fromJson(eachEnData.getData(),
							ClientItemFeature.class);
					instance.addItem(item.getItemType(), item.getItemID(),
							item.isDead(), item.getLocX(), item.getLocY());

					System.out.println("Add Item");
					break;
				default:
					System.out.println("ERROR");
					throw new Error("ERROR");
			}
		}
	}
}
