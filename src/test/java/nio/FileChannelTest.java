package nio;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by root on 15-5-15.
 */
public class FileChannelTest {

    @Test
    public void TestFileChannelRead() {
        try {
            RandomAccessFile afile = new RandomAccessFile("/server/dev/data/transfer.txt", "rw");
            FileChannel inChannel = afile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(48);

            int bytesRead = inChannel.read(buf);

            while (bytesRead != -1) {
                //System.out.println("read" + bytesRead);
                buf.flip();

                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }

                buf.clear();
                bytesRead = inChannel.read(buf);
            }
            inChannel.close();
            afile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileChannelWrite() {
        RandomAccessFile afile = null;
        try {
            afile = new RandomAccessFile("/server/dev/data/nio.txt", "rw");
            FileChannel channel = afile.getChannel();
            for (int i = 0; i < 100; i++) {
                String data = "hello word" + System.currentTimeMillis();
                ByteBuffer buf = ByteBuffer.allocate(48);
                buf.clear();
                buf.put(data.getBytes());
                buf.flip();
                while (buf.hasRemaining()) {
                    channel.write(buf);
                }
            }
            channel.close();
            afile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void socketChannelRead() {
        try {
            RandomAccessFile file = new RandomAccessFile("/server/dev/data/baidu.txt", "rw");

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));

            while (!socketChannel.finishConnect()) {
                System.out.println("connecting to server...");
            }

            System.out.print("connected!!");

            ByteBuffer buf = ByteBuffer.allocate(48);
            int bufRead = socketChannel.read(buf);
            while (bufRead != -1) {
                buf.flip();
                System.out.print((char) buf.get());
            }
            socketChannel.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
