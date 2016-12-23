package tcp.tcpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

import CDC.Cdc;

public class TcpServerThread implements Runnable {
    private int ClientID = 0;
    volatile static private int totalClient = 0;
    private PrintWriter output;
    private BufferedReader input;
    volatile static public boolean load = false;
    private static ArrayList<String> nameList = new ArrayList<String>();
    Gson gson;

    TcpServerThread() {
        gson = new Gson();
    }

    public TcpServerThread(Socket sc, int ClientID) {
        this.ClientID = ClientID;
        totalClient++;
        try {
            output = new PrintWriter(sc.getOutputStream(), true);
            input = new BufferedReader(
                    new InputStreamReader(sc.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String nickName = initGame(input, output);
            Cdc.getInstance().addVirtualCharacter(ClientID, nickName);
            waitLoad(input, output);
            loadGame(output, nickName);
            // game state
            while (true) {
                String buf = recv(input);
                Gson gson = new Gson();
                boolean[] keys = gson.fromJson(buf, boolean[].class);
                // "wsad j"
                Cdc.getInstance().updateKeys(ClientID, keys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ClientID + " sc close");
            System.exit(0);
        }
    }

    String initGame(BufferedReader input, PrintWriter output)
            throws IOException {
        // room wait
        String myName = recv(input);
        nameList.add(myName);
        send(output, String.valueOf(ClientID));
        return myName;
    }

    void waitLoad(BufferedReader input, PrintWriter output) throws IOException {
        boolean localLoad = false;
        while (!load || !localLoad) {
            String action = recv(input);
            System.out.println(ClientID + " " + action + " in while");
            switch (action) {
                case "Get list":
                    send(output, new Gson().toJson(nameList));
                    localLoad = false;
                    break;
                case "game load?":
                    send(output, String.valueOf(load));
                    localLoad = false;
                    break;
                case "Start":
                    load = true;
                    localLoad = false;
                    break;
                case "game load":
                    localLoad = true;
                    break;
            }
        }
    }

    synchronized void loadGame(PrintWriter output, String nickName)
            throws IOException, InterruptedException {
        // loading state
        if (recv(input).equals("Get Number")) {
            output.println(totalClient);
        }
        System.out.println(ClientID + " start");
        if (ClientID == 0) {
            System.out.println(ClientID + " room start");
            Cdc cdc = Cdc.getInstance();
            cdc.gameItemsInital();
            System.out.println(
                    "Cdc player num" + cdc.getPlayersUpdateInfo().size());
            cdc.startUpdatingTimer();
        }
    }

    void send(PrintWriter output, String outputString) {
        output.println(outputString);
    }

    private static String recv(BufferedReader input) throws IOException {
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            break;
        }
        return inputLine;
    }
}
