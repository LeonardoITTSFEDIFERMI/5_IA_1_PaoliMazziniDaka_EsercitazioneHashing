package pkg5_ia_1_paolimazzinidaka_esercitazionehashing;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Leonardo Mazzini Drovandi & Flavio Paoli
 */

public class algoritmoHashingReceiver extends JFrame
{
    private JButton bottoneDownload;
    private JTextArea areaStampa;
    private JComboBox<String> boxSceltaAlgoritmo;
    private int Porta;

    public algoritmoHashingReceiver(int localPorta)
    { 
        this.Porta = localPorta;
        
        setTitle("Esercitazione Hashing");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        bottoneDownload = new JButton("Attesa Download File");
        areaStampa = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaStampa);
        areaStampa.setEditable(false);

        panel.add(bottoneDownload);
        panel.add(new JLabel("Selezione algoritmo:"));
        boxSceltaAlgoritmo = new JComboBox<>(new String[]{"MD5", "SHA-1", "SHA-256"});
        panel.add(boxSceltaAlgoritmo);

        bottoneDownload.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                downloadFile();
            }
        });

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
    
    private void downloadFile()
    {
        try 
        {
            ServerSocket serverSocket = new ServerSocket(Porta);
            Socket socket = serverSocket.accept();
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);

            // Ricevi nome file e hash dall'uploader
            String fileName = (String) ois.readObject();
            String receivedHash = (String) ois.readObject();

            // Ottieni il path del sistema della cartella downloader
            String downloadFolderPath = System.getProperty("user.home") + File.separator + "Downloads";
            String filePath = downloadFolderPath + File.separator + fileName;

            // Crea un output per scrivere il file sul disco
            FileOutputStream fos = new FileOutputStream(filePath);
            byte[] dataBytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(dataBytes)) != -1) 
            {
                fos.write(dataBytes, 0, bytesRead);
            }
            fos.close();

            // Verifica integrità
            String selectedAlgorithm = (String) boxSceltaAlgoritmo.getSelectedItem();
            MessageDigest md = MessageDigest.getInstance(selectedAlgorithm);
            FileInputStream fis = new FileInputStream(filePath);
            
            byte[] buffer = new byte[1024];
            int n;
            
            while ((n = fis.read(buffer)) != -1)
            {
                md.update(buffer, 0, n);
            }
            
            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            
            for (byte b : hashBytes)
            {
                sb.append(String.format("%02x", b));
            }
            
            String fileHash = sb.toString();
            
            areaStampa.append("Downloaded File Hash (" + selectedAlgorithm + "): " + fileHash + "\n");
            String integrityMessage = (fileHash.equals(receivedHash)) ? "Integrity Verified" : "Integrity Failed";
            areaStampa.append(integrityMessage + "\n");

            // Chiudi la connessione
            fis.close();
            ois.close();
            is.close();
            socket.close();
            serverSocket.close();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
