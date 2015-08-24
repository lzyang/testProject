package designpatterns.create.prototype;

import java.io.*;

/**
 * Created by Morningsun(515190653@qq.com) on 15-8-24.
 */
public class Prototype implements Cloneable,Serializable{
    private static final long serialVersionUID = 1L;
    private String str;
    private SerializableObject obj;

    /**
     * 浅克隆
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException{
        Prototype proto = (Prototype)super.clone();
        return proto;
    }


    /**
     * 深度克隆
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object deepClone() throws IOException, ClassNotFoundException {

        /**
         * 写入当前对象的二进制流
         */
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);

        /**
         * 读出二进制流产生的新对象
         */
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return  ois.readObject();
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public SerializableObject getObj() {
        return obj;
    }

    public void setObj(SerializableObject obj) {
        this.obj = obj;
    }
}
