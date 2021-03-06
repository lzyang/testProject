package nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetIo {



    class NetServer extends Thread{
        private ServerSocket  serverSocket;

        public int getPort(){
            return serverSocket.getLocalPort();
        }

        @Override
        public void run(){
            try {
                serverSocket = new ServerSocket(0);
                Executor executor = Executors.newFixedThreadPool(10);
                while (true){
                    Socket socket = serverSocket.accept();
                    RequestHandler requestHandler = new RequestHandler(socket);
//                    requestHandler.start();
                    //改为线程池作业
                    executor.execute(requestHandler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(serverSocket != null){
                    try {
                        serverSocket.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    class RequestHandler extends Thread{

        private Socket socket;

        public RequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try{
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("me me me");
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        NetServer server = new NetIo().new NetServer();
        server.start();

        try {

            Socket clent = new Socket(InetAddress.getLocalHost(),server.getPort());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clent.getInputStream()));
            bufferedReader.lines().forEach(s -> System.out.println(s));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
