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
    
    private void downloadFile() {
        try {
            ServerSocket serverSocket = new ServerSocket(Porta);
            Socket socket = serverSocket.accept();
            InputStream input = socket.getInputStream();
            ObjectInputStream output = new ObjectInputStream(input);

            // Ricevi nome file e hash dall'uploader
            String nomeFile = (String) output.readObject();
            String hashRicevuto = (String) output.readObject();

            // Ottieni il path del sistema della cartella downloader
            String cartellaDownload = System.getProperty("user.home") + File.separator + "Downloads";
            String filePath = cartellaDownload + File.separator + "(Copia)" + nomeFile;

            // Crea un output per scrivere il file sul disco
            FileOutputStream fileOutput = new FileOutputStream(filePath);
            byte[] vettoreByte = new byte[1024];
            int byteLetti;
            while ((byteLetti = input.read(vettoreByte)) != -1) {
                fileOutput.write(vettoreByte, 0, byteLetti);
            }
            fileOutput.close();

            // Verifica integrità
            String stringAlgoritmoScelto = (String) boxSceltaAlgoritmo.getSelectedItem();
            MessageDigest md = MessageDigest.getInstance(stringAlgoritmoScelto);
            FileInputStream fileInput = new FileInputStream(filePath);
            
            byte[] buffer = new byte[1024];
            int byteLetto = 0;
            
            while ((byteLetto = fileInput.read(buffer)) != -1) {
                md.update(buffer, 0, byteLetto);
            }
            
            byte[] vettoreHashByte = md.digest();
            StringBuilder tempStringBuilder = new StringBuilder();
            
            for (int i = 0; i < vettoreHashByte.length; i++) {
                tempStringBuilder.append(String.format("%02x", vettoreHashByte[i]));
            }
            
            String fileHash = tempStringBuilder.toString();
            
            String risultatoIntegrita;
            if (fileHash.equals(hashRicevuto)) {
                risultatoIntegrita = "Integrity Verified";
            } else {
                risultatoIntegrita = "Integrity Failed";
            }
            
            areaStampa.append("SERVER\nAlgoritmo scelto: " + stringAlgoritmoScelto + "\nHash: " + fileHash + "\nIntegrità: " + risultatoIntegrita + "\n\n");

            // Chiudi la connessione
            fileInput.close();
            output.close();
            input.close();
            socket.close();
            serverSocket.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
