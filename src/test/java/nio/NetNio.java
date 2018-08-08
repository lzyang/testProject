package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NetNio extends Thread{


    @Override
    public void run() {
        // 创建 Selector 和 Channel
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(),18888));
            serverSocket.configureBlocking(false);

            serverSocket.register(selector,SelectionKey.OP_ACCEPT);

            while (true){
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectionKeys.iterator();
                System.out.println(selectionKeys.size());
                while (iter.hasNext()){
                    SelectionKey key = iter.next();
                    System.out.println("#####SelectionKey:");
                    sayBack(serverSocket);
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sayBack(ServerSocketChannel server) throws IOException {
        SocketChannel client = server.accept();
//        client.write(Charset.defaultCharset().encode("Nio me me me"));
        client.write(ByteBuffer.wrap(new String("Nio me me me").getBytes()));
    }

    public static void main(String[] args) {
        NetNio server = new NetNio();
        server.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {

            SocketChannel channelS = SocketChannel.open();
            channelS.configureBlocking(false);
            Selector selector = Selector.open();
            channelS.connect(new InetSocketAddress(InetAddress.getLocalHost(),18888));
            channelS.register(selector,SelectionKey.OP_CONNECT);
            while (true){
                selector.select();
                Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
                while (ite.hasNext()){
                    SelectionKey key = ite.next();
                    ite.remove();
                    if(key.isConnectable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        if(channel.isConnectionPending()){
                            channel.finishConnect();
                        }
                        channel.configureBlocking(false);
                        channel.write(ByteBuffer.wrap(new String("nio me client send!").getBytes()));
                        channel.register(selector,SelectionKey.OP_READ);
                    }else if(key.isReadable()){
                        SocketChannel channel1 = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        channel1.read(buffer);
                        byte[] data = buffer.array();
                        String msg = new String(data);
                        System.out.println(">>>>>>client receive:" + msg);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
