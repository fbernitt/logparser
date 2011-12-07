package de.thecodex.logparser.fetch;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.userauth.keyprovider.KeyPairWrapper;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;

/**
 * Fetches files using SCP. Uses sshj for scp.
 */
public class SCPFetchMethod implements FetchMethod {
    public void fetch(FileInfo fileInfo) {
        SSHClient ssh = new SSHClient();

        try {
            KeyPair keyPair = readKeyFile();
            ssh.loadKnownHosts();
            ssh.setTimeout(1000);
            ssh.setConnectTimeout(1000);
            ssh.connect(fileInfo.getHostName());
            try {
                System.out.println("Trying auth for user");
                ssh.authPublickey("folker", new KeyPairWrapper(keyPair));
                SFTPClient sftp = ssh.newSFTPClient();
                sftp.get(fileInfo.getRemoteFile(), fileInfo.getLocalFile());
                System.out.println("hmm...what are we waiting for...");
            } finally {
                ssh.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyPair readKeyFile() {
        PasswordFinder passwordFinder = createPasswordFinder();
        try {
            final PEMReader pem = new PEMReader(new InputStreamReader(new FileInputStream("/Users/folker/.ssh/id_rsa_eunike")), passwordFinder);
            try {
                Object obj;
                while ((obj = pem.readObject()) != null) {
                    if (obj instanceof KeyPair) {
                        KeyPair o = (KeyPair) obj;
                        pem.close();
                        return o;
                    }
                }
            } finally {
                pem.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("NO key pair found!");
    }

    private PasswordFinder createPasswordFinder() {
        return new PasswordFinder() {

            public char[] getPassword() {
                return "foobar".toCharArray();
            }
        };
    }
}
