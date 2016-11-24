package com.ittekikun.plugin.minetweet;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class VariousUtility
{
    /**
     * joinArray
     *
     * 指定した所から配列を空白を入れて接続できる。
     * 例: /test a b c d e f
     * ↓
     * args[0] = a, args[1] = b, args[2] = c, args[3] = d....
     * ↓
     * joinArray(args, 1) = "b c d e f"
     *
     * @param par1 繋げたい配列（配列String型）
     * @param par2 どこの配列から繋げたいか（int型）
     *
     * @return 繋げた文字列を返す
     */
    public static String joinArray(String[] par1, int par2)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (int a = par2; a < par1.length; ++a)
        {
            if (a > par2)
            {
                stringBuilder.append(" ");
            }

            String s = par1[a];

            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }


    /**
     * 文字列が整数値に変換可能かどうかを判定する
     *
     * @param source 変換対象の文字列
     * @return 整数に変換可能かどうか
     *
     * @author https://github.com/ucchyocean/
     */
    public static boolean checkIntParse(String source)
    {

        return source.matches("^-?[0-9]{1,9}$");
    }
    /**
     * HTTPサーバー上のテキストの内容を読み込む
     *
     * @param par1 URL
     * @return テキストをListで返す
     */
    public static List getHttpServerText(String par1) throws IOException
    {
        URL url = new URL(par1);
        InputStream i = url.openConnection().getInputStream();

        BufferedReader buf = new BufferedReader(new InputStreamReader(i, "UTF-8"));

        String line;
        List<String> arrayList = new ArrayList();

        while ((line = buf.readLine()) != null)
        {
            arrayList.add(line);
        }
        buf.close();

        return arrayList;
    }

    /**
     * timeGetter
     * フォーマットはここを参照されたし
     * http://java-reference.sakuraweb.com/java_date_format.html
     *
     * @param format 出力する時刻のフォーマット（String）
     *
     * @return 指定したフォーマットの形で現時刻
     */
    public static String timeGetter(String format)
    {
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = sdf.format(date);

        return time;
    }

    /**
     * simpleTimeGetter
     * とりあえず今の時間を返す
     *
     * @return 現時刻
     */
    public static String simpleTimeGetter()
    {
        Calendar calendar = Calendar.getInstance();
        String time = calendar.getTime().toString();

        return time;
    }

    /**
     * 短縮URL生成
     *
     * @param longUrl
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static String getShortUrl(String longUrl, String apikey) throws ClientProtocolException, IOException
    {
        HttpPost post = new HttpPost("https://www.googleapis.com/urlshortener/v1/url?key=" + apikey);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity("{'longUrl': '"+longUrl+"'}", "UTF-8"));

        HttpResponse response = new DefaultHttpClient().execute(post);

        String responseText = EntityUtils.toString(response.getEntity());

        // JsonFactoryの生成
        JsonFactory factory = new JsonFactory();
        // JsonParserの取得
        @SuppressWarnings("deprecation") JsonParser parser = factory.createJsonParser(responseText);

        //JSONのパース処理
        String shotUrl = "";
        while (parser.nextToken() != JsonToken.END_OBJECT)
        {
            String name = parser.getCurrentName();
            if (name != null)
            {
                parser.nextToken();
                if (name.equals("id"))
                {
                    shotUrl = parser.getText();
                }
            }
        }
        return shotUrl;
    }

    public static Object decodeObjectfromJar(File jarFile, String fileName, String secretKey) throws IOException, ClassNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException
    {
        InputStream is;
        JarFile jar;

        jar = new JarFile(jarFile);

        ZipEntry zipEntry = jar.getEntry(fileName);
        is = jar.getInputStream(zipEntry);

        byte[] indata = new byte[(int)zipEntry.getSize()];
        is.read(indata);
        is.close();

        byte[] originalOutdata = Base64.decodeBase64(indata);
        byte[] outdata = cipher(Cipher.DECRYPT_MODE, originalOutdata, secretKey, "AES");

        ByteArrayInputStream bais = new ByteArrayInputStream(outdata);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object= ois.readObject();

        bais.close();
        ois.close();

        return object;
    }

    public static Object encryptionDecodeObject(File file, String secretKey) throws IOException, ClassNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException
    {
        FileInputStream fii = new FileInputStream(file);
        byte[] indata = new byte[(int)file.length()];
        fii.read(indata);
        fii.close();

        byte[] originalOutdata = Base64.decodeBase64(indata);
        byte[] outdata = cipher(Cipher.DECRYPT_MODE, originalOutdata, secretKey, "AES");

        ByteArrayInputStream bais = new ByteArrayInputStream(outdata);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object= ois.readObject();

        bais.close();
        ois.close();

        return object;
    }

    public static void encryptionEncodeObject(File file, Object object, String secretKey) throws IOException, ClassNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();

        byte[] encryptBytes = cipher(Cipher.ENCRYPT_MODE, baos.toByteArray(), secretKey, "AES");
        byte[] in = Base64.encodeBase64(encryptBytes);

        FileOutputStream fo = new FileOutputStream(file);
        fo.write(in);
        fo.close();
    }

    public static Object decodeObject(File file) throws IOException, ClassNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException
    {
        FileInputStream fii = new FileInputStream(file);
        byte[] indata = new byte[(int)file.length()];
        fii.read(indata);
        fii.close();

        byte[] outdata = Base64.decodeBase64(indata);

        ByteArrayInputStream bais = new ByteArrayInputStream(outdata);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object= ois.readObject();

        bais.close();
        ois.close();

        return object;
    }

    public static void encodeObject(File file, Object object) throws IOException, ClassNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();

        byte[] in = Base64.encodeBase64( baos.toByteArray());

        FileOutputStream fo = new FileOutputStream(file);
        fo.write(in);
        fo.close();
    }

    /**
     * Base64されたAES暗号化文字列を元の文字列に復元する
     * @author http://blogs.yahoo.co.jp/dk521123/32780473.html
     */
    private static byte[] cipher(int mode, byte[] source, String secretKey, String algorithm) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] secretKeyBytes = secretKey.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(mode, secretKeySpec);
        return cipher.doFinal(source);
    }

    public static void generationPlayerImage(String playerName, String message, File tweetImage)
    {
        BufferedImage base = null;
        BufferedImage head = null;
        BufferedImage name = null;
        BufferedImage mes = null;
        try
        {
            base = new BufferedImage(600, 200, BufferedImage.TYPE_INT_BGR);

            head = ImageIO.read(new URL("https://minotar.net/avatar/" + playerName + "/200.png"));

            name = new BufferedImage(400, 100, BufferedImage.TYPE_INT_BGR);

            mes = new BufferedImage(400, 100, BufferedImage.TYPE_INT_BGR);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //白く塗りつぶし
        Graphics2D baseGraphics = base.createGraphics();
        baseGraphics.setColor(Color.WHITE);
        baseGraphics.fillRect(0, 0, 600, 200);

        //白く塗りつぶし
        Graphics2D nameGraphics = name.createGraphics();
        nameGraphics.setColor(Color.WHITE);
        nameGraphics.fillRect(0, 0, 400, 100);

        //色々して文字列書き込み
        nameGraphics.setColor(Color.BLACK);
        Font nameFont = new Font("Monospaced", Font.PLAIN, 50);
        nameGraphics.setFont(nameFont);
        nameGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawStringCenter(nameGraphics, 400, 100, playerName);

        //白く塗りつぶし
        Graphics2D mesGraphics = mes.createGraphics();
        mesGraphics.setColor(Color.WHITE);
        mesGraphics.fillRect(0, 0, 400, 100);

        //色々して文字列書き込み
        mesGraphics.setColor(new Color(0, 167, 212));
        Font f = new Font("Monospaced", Font.BOLD, 45);
        mesGraphics.setFont(f);
        mesGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawStringCenter(mesGraphics, 400, 100, message);//16文字まで

        //ベースに統合
        baseGraphics.drawImage(head, 0, 0, null);
        baseGraphics.drawImage(name, 200, 0, null);
        baseGraphics.drawImage(mes, 200, 100, null);

        //	ファイル保存
        try
        {
            ImageIO.write(base, "png", tweetImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void drawStringCenter(Graphics g, int x, int y, String text)
    {
        Rectangle size = new Rectangle(x, y);
        FontMetrics fm = g.getFontMetrics();
        Rectangle rectText = fm.getStringBounds(text, g).getBounds();
        int nx = (size.width - rectText.width) / 2;
        int ny = (size.height - rectText.height) / 2 + fm.getMaxAscent();
        // Draw text
        g.drawString(text, nx, ny);
    }
}