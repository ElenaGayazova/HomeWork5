package http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class App
{
    private static int port = 8080;
    private static String bindHost = "localhost";


    //
    // java http.App [port] [host]
    // port - номер порта (по умолчанию 8080)
    // host - имя сервера ("localhost")
    //
    public static void main( String[] args )
    {
        if(args.length>0) {
            port = Integer.parseInt(args[0]);
        }
        if(args.length>1) {
            bindHost = args[1];
        }

        try {

            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(bindHost, port));

            System.out.println(String.format("Server started: %s:%d", bindHost, port));

            // в бесконечном цикле ожидаем подключения пользователя
            while (true) {
                // ожидаем подключения
                Socket socket = serverSocket.accept();

                // Обработку запроса пользователя выполняем в отдельном потоке
                HttpTask task = new HttpTask(socket);
                task.start();
            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }finally {
            System.out.println("Server closed");
        }

    }
}
