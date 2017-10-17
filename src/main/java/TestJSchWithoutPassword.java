import com.jcraft.jsch.*;

import java.io.File;


/**
 * google: jsch kerberos prompt
 *
 * https://www.programcreek.com/java-api-examples/index.php?class=com.jcraft.jsch.ChannelSftp&method=put
 *
 *
 * http://www.jcraft.com/jsch/
 * https://stackoverflow.com/questions/14617/how-to-retrieve-a-file-from-a-server-via-sftp
 *
 *
 *
 */
public class TestJSchWithoutPassword {
    public static void main(String args[]) {
        JSch jsch = new JSch();
        Session session = null;
        try {
            System.out.println(System.getProperty("user.home"));
            jsch.setKnownHosts(System.getProperty("user.home") + "/.ssh/known_hosts");
            jsch.addIdentity(System.getProperty("user.home") + "/.ssh/id_rsa");
            session = jsch.getSession("admin", "127.0.0.1", 22);
            session.setConfig("StrictHostKeyChecking", "no");
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