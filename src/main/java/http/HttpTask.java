package http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpTask extends Thread {
    private Socket socket;
    private int bufferSize = 1024;

    private byte[] message200 = "HTTP/1.1 200 Ok\n\n".getBytes(); // "\n\n" - тело ответа отделяется от заголовка одной пустой строкой
    private byte[] message404 = "HTTP/1.1 404 Failed\n".getBytes();

    public HttpTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        try {
            System.out.println("Task start");
            is = socket.getInputStream();
            os = socket.getOutputStream();

            byte[] buffer = new byte[bufferSize];

            int readCount = is.read(buffer);
            if(readCount<0) return; // -1 если соединение разорвано

            String command = new String(buffer,0, readCount);

            System.out.println("command:\n"+command);

            if(command.startsWith("GET ")) {
                // запрос GET
                os.write(message200);
                File dir = new File(".");
                for(String name: dir.list()) {
                    os.write(name.getBytes());
                    os.write(0xA); // перевод строки
                }
            } else {
                // другие запросы
                os.write(message404);
            }
            os.flush();

        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally {
            // закрываем потоки по завершению работы
            try {
                if (os != null) os.close();
                if (is != null) is.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Task done");
        }
    }
}
