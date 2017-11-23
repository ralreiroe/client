import com.jcraft.jsch.*;

import java.io.File;
import java.util.Scanner;


/**
 *
 * https://www.maciverse.com/how-to-turn-on-your-macs-sftp.html
 * (Turn on Remote Login in System Preferences >> Sharing
 *
 * https://search.itunes.apple.com/WebObjects/MZContentLink.woa/wa/link?mt=11&path=mac%2fcyberduck
 *
 */
public class TestJSchWithPassword {
    public static void main(String args[]) {
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession("admin", "127.0.0.1", 22);
            session.setConfig("StrictHostKeyChecking", "no");

//            String password = System.console().readPassword().toString();
            String password = new Scanner(System.in).nextLine();

            session.setPassword(password);
            System.out.println(password);
//            session.setPassword(System.getProperty("password"));

            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            System.out.println(new File("local.txt").exists());


            sftpChannel.rm("localfile.txt");
            sftpChannel.put("localfile.txt", "localfile.txt");
            sftpChannel.get("localfile.txt", "local.txt");

            System.out.println(new File("local.txt").exists());

            new File("local.txt").delete();

            System.out.println(new File("local.txt").exists());


            sftpChannel.exit();
            session.disconnect();
            System.out.println("Done");
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
}