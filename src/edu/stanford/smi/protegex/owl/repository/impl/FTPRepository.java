package edu.stanford.smi.protegex.owl.repository.impl;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 3, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FTPRepository implements Repository {

    private URI ftpURI;

    private URI projectDirectory;

    private File localCopy;

    private URI ontologyName;

    private static FTPRepositoryPasswordProvider passwordProvider;


    static {
        passwordProvider = new FTPRepositoryPasswordProvider() {
            public String getUserName() {
                return null;
            }


            public String getPassword() {
                JPasswordField pwf = new JPasswordField();
                LabeledComponent lc = new LabeledComponent("FTP Password", pwf);
                JOptionPane.showMessageDialog(null, lc, "Password", JOptionPane.INFORMATION_MESSAGE);
                return new String(pwf.getPassword());
            }
        };
    }


    public FTPRepository(URI ftpURI, URI projectDirectory) {
        this.ftpURI = ftpURI;
        this.projectDirectory = projectDirectory;
        setLocalFile();
    }


    public boolean contains(URI ontologyName) {
        if (this.ontologyName != null) {
            return this.ontologyName.equals(ontologyName);
        }
        else {
            return false;
        }
    }


    private void setLocalFile() {
        try {
            File f = new File(ftpURI.getPath());
            URI localURI = new URI(new URL(projectDirectory.toURL(), f.getName()).toString());
            localCopy = new File(localURI);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void ftpGet() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setRemoteHost(ftpURI.getHost());
            ftpClient.connect();
            if (passwordProvider != null) {
                ftpClient.login(ftpURI.getUserInfo(), passwordProvider.getPassword());
            }
            File f = new File(ftpURI.getPath());
            ftpClient.chdir(f.getParentFile().toString());
            FileOutputStream fos = new FileOutputStream(localCopy);
            ftpClient.get(fos, f.getName());
            ftpClient.quit();
            FileInputStream fis = new FileInputStream(localCopy);
            OntologyNameExtractor extractor = new OntologyNameExtractor(fis, localCopy.toURI().toURL());
            ontologyName = extractor.getOntologyName();
            System.out.println(extractor.getOntologyName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (FTPException e) {
            e.printStackTrace();
        }
    }


    public void ftpPut() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setRemoteHost(ftpURI.getHost());
            ftpClient.connect();
            if (passwordProvider != null) {
                ftpClient.login(ftpURI.getUserInfo(), passwordProvider.getPassword());
            }
            File f = new File(ftpURI.getPath());
            ftpClient.chdir(f.getParentFile().toString());
            FileInputStream fis = new FileInputStream(localCopy);
            ftpClient.put(fis, f.getName());
            ftpClient.quit();
            System.out.println("Put!");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (FTPException e) {
            e.printStackTrace();
        }
    }


    public void refresh() {
        ftpGet();
    }


    public Collection getOntologies() {
        if (ontologyName != null) {
            return Collections.singleton(ontologyName);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    public InputStream getInputStream(URI ontologyName)
            throws IOException {
        if (contains(ontologyName)) {
            if (isWritable(ontologyName)) {
                return new FileInputStream(localCopy);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }


    public boolean isWritable(URI ontologyName) {
        if (contains(ontologyName)) {
            if (localCopy.exists() && localCopy.canWrite()) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }


    public OutputStream getOutputStream(URI ontologyName)
            throws IOException {
        if (contains(ontologyName)) {
            if (isWritable(ontologyName)) {
                return new FileOutputStream(localCopy);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }


    public boolean isSystem() {
        return false;
    }


    public String getRepositoryDescription() {
        return "FTP Repository (" + ftpURI + ")";
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        if (localCopy != null) {
            return localCopy.toString();
        }
        else {
            return "";
        }
    }


    public String getRepositoryDescriptor() {
        return ftpURI.toString();
    }


    public static void setFTPRepositoryPasswordProvider(FTPRepositoryPasswordProvider provider) {
        passwordProvider = provider;
    }


    public static void main(String [] args) {
        try {
            FTPRepositoryPasswordProvider pwp = new FTPRepositoryPasswordProvider() {
                public String getUserName() {
                    return null;
                }


                public String getPassword() {
                    JPasswordField pwd = new JPasswordField();
                    JOptionPane.showMessageDialog(null, pwd, "Password", JOptionPane.INFORMATION_MESSAGE);
                    return new String(pwd.getPassword());
                }
            };
            FTPRepository.setFTPRepositoryPasswordProvider(pwp);
            String s = "ftp://horridgm@kiss.cs.man.ac.uk:21/home/M02/cc/horridgm/ontologies/TeachingFlattened.owl";
            URI uri = new URI(s);
            System.out.println("Scheme: " + uri.getScheme());
            System.out.println("Host: " + uri.getHost());
            System.out.println("Port: " + uri.getPort());
            System.out.println("UserInfo: " + uri.getUserInfo());
            System.out.println("Path: " + uri.getPath());
            File f = new File(uri.getPath());
            System.out.println("FileName: " + f.getName());
            System.out.println("FileDirectory: " + f.getParentFile());
            URI pd = new File("/Users/matthewhorridge/Desktop").toURI();
            FTPRepository rep = new FTPRepository(uri, pd);
            rep.ftpGet();
            rep.ftpPut();
//			URI ontName = new URI("http://www.cs.man.ac.uk/~horridgm/ontologies/TeachingFlattened.owl");
//			try {
//				OutputStream os = rep.getOutputStream(ontName);
//				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
//				writer.write("Test!");
//				writer.flush();
//				writer.close();
//			}
//			catch(IOException e) {
//				e.printStackTrace();
//			}
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

