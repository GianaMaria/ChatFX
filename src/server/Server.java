package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();

        ServerSocket server = null;
        Socket socket;

        final int PORT = 8189;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                System.out.println("socket.getRemoteSocketAddress(): " + socket.getRemoteSocketAddress());
                System.out.println("socket.getLocalSocketAddress() " + socket.getLocalSocketAddress());
                subscribe(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void lsMsg(ClientHandler from, String nick, String msg) { // Класс для отправки личных сообщений
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getName().equals(nick)) {
                clientHandler.sendMsg("от " + from.getName());
                from.sendMsg("клиенту " + nick + ": " + msg);
                return;
            }
        }
        from.sendMsg("Участника с ником " + nick + " нет в чат-комнате");
    }

    void broadcastMsg(String msg) {
        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
