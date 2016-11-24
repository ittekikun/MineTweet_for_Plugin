package com.ittekikun.plugin.minetweet;
import java.io.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FileUtility
{
    /**
     * jarファイルの中に格納されているフォルダを、中のファイルごとまとめてjarファイルの外にコピーするメソッド<br/>
     * テキストファイルは、WindowsだとS-JISで、MacintoshやLinuxだとUTF-8で保存されます。
     *
     * 参考にしたコードからの変更点は親フォルダー毎コピーされる所
     *
     * @param jarFile        jarファイル
     * @param targetFilePath コピー先のフォルダ
     * @param sourceFilePath コピー元のフォルダ
     */
    public static void copyFolderFromJar(File jarFile, File targetFilePath, String sourceFilePath)
    {

        JarFile jar = null;

        if (!targetFilePath.exists())
        {
            targetFilePath.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements())
            {

                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(sourceFilePath))
                {

                    File targetFile = new File(targetFilePath, sourceFilePath);
                    if (!targetFile.getParentFile().exists())
                    {
                        targetFile.getParentFile().mkdirs();
                    }

                    if(!targetFile.exists())
                    {
                        targetFile.mkdir();
                    }

                    File target = new File(targetFile, entry.getName().substring(sourceFilePath.length() + 1));

                    InputStream is = null;
                    FileOutputStream fos = null;
                    BufferedReader reader = null;
                    BufferedWriter writer = null;

                    try
                    {
                        is = jar.getInputStream(entry);
                        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        fos = new FileOutputStream(target);
                        writer = new BufferedWriter(new OutputStreamWriter(fos));

                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            writer.write(line);
                            writer.newLine();
                        }

                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (writer != null)
                        {
                            try
                            {
                                writer.flush();
                                writer.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                        if (reader != null)
                        {
                            try
                            {
                                reader.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                        if (fos != null)
                        {
                            try
                            {
                                fos.flush();
                                fos.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                        if (is != null)
                        {
                            try
                            {
                                is.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }
    }

    /**
     * jarファイルの中に格納されているフォルダを、中のファイルごとまとめてjarファイルの外にコピーするメソッド<br/>
     * テキストファイルは、そのままコピーされます。
     *
     * 参考にしたコードからの変更点はフォルダーもコピーされる所
     *
     * @param jarFile        jarファイル
     * @param targetFilePath コピー先のフォルダ
     * @param sourceFilePath コピー元のフォルダ
     */
    public static void copyRawFolderFromJar(File jarFile, File targetFilePath, String sourceFilePath)
    {

        JarFile jar = null;

        if (!targetFilePath.exists())
        {
            targetFilePath.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements())
            {

                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(sourceFilePath))
                {

                    File targetFile = new File(targetFilePath, sourceFilePath);
                    if (!targetFile.getParentFile().exists())
                    {
                        targetFile.getParentFile().mkdirs();
                    }

                    if(!targetFile.exists())
                    {
                        targetFile.mkdir();
                    }

                    File target = new File(targetFile, entry.getName().substring(sourceFilePath.length() + 1));

                    InputStream is = null;

                    try
                    {
                        is = jar.getInputStream(entry);

                        Files.copy(is, target.toPath());
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (is != null)
                        {
                            try
                            {
                                is.close();
                            }
                            catch (IOException e)
                            {
                                // do nothing.
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }

    }

    /**
     * jarファイルの中に格納されているテキストファイルを、jarファイルの外にコピーするメソッド<br/>
     * WindowsだとS-JISで、MacintoshやLinuxだとUTF-8で保存されます。
     *
     * @author https://github.com/ucchyocean/
     *
     * @param jarFile        jarファイル
     * @param targetFile     コピー先
     * @param sourceFilePath コピー元
     */
    public static void copyFileFromJar(File jarFile, File targetFile, String sourceFilePath)
    {
        JarFile jar = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = targetFile.getParentFile();
        if (!parent.exists())
        {
            parent.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            fos = new FileOutputStream(targetFile);

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(fos));

            String line;
            while ((line = reader.readLine()) != null)
            {
                writer.write(line);
                writer.newLine();
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (writer != null)
            {
                try
                {
                    writer.flush();
                    writer.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }
    }

    /**
     * jarファイルの中に格納されているテキストファイルを、jarファイルの外にコピーするメソッド<br/>
     * ファイルをそのままコピーします。
     *
     * @param jarFile        jarファイル
     * @param targetFile     コピー先
     * @param sourceFilePath コピー元
     */
    public static void copyRawFileFromJar(File jarFile, File targetFile, String sourceFilePath)
    {
        JarFile jar = null;
        InputStream is = null;

        File parent = targetFile.getParentFile();
        if (!parent.exists())
        {
            parent.mkdirs();
        }

        try
        {
            jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            Files.copy(is, targetFile.toPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    // do nothing.
                }
            }
        }
    }
}