package Week_01;

/*
    2.（必做）自定义一个 Classloader，加载一个 Hello.xlass 文件，
    执行 Hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。文件在我的教室下载。
 */
import java.lang.reflect.Method;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) throws Exception {
        //要处理的class文件名为Hello，执行的方法名是hello
        final String className = "Hello";
        final String methodName = "hello";

        //创建类加载器，自定义的
        ClassLoader classLoader = new HelloClassLoader();

        //加载Hello类
        Class<?> clasz = classLoader.loadClass(className);

        //打印Hello类（加载入类加载器的类）里面的方法名
        for (Method m : clasz.getDeclaredMethods()) {
            System.out.println(clasz.getSimpleName() + "." + m.getName());
        }

        //创建对象
        Object instance = clasz.getDeclaredConstructor().newInstance();

        //调用实例方法
        Method method = clasz.getMethod(methodName);

        //调用类中的方法
        method.invoke(instance);

    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //进行路径转换，将路径中的.换为/
        String resourcePath = name.replace(".", "/");

        //文件后缀
        final String suffix = ".xlass";

        //输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath + suffix);

        try {
            //读取数据
            int length = inputStream.available();
            byte[] byteArray = new byte[length];
            inputStream.read(byteArray);

            //解码为字节
            byte[] classBytes = decode(byteArray);

            //通知底层定义这个类
            return defineClass(name, classBytes, 0, classBytes.length);

        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            close(inputStream);
        }
    }

    //解码
    private static byte[] decode(byte[] byteArray) {
        byte[] targetArray = new byte[byteArray.length];

        for (int i = 0; i < byteArray.length; i ++) {
            targetArray[i] = (byte) (255 - byteArray[i]);
        }

        return targetArray;
    }

    //关闭
    private static void close(Closeable cls) {
        if (null != cls) {
            try {
                cls.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
