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

public class algoritmoHashingSender extends JFrame
{
    private JButton bottoneUpload, bottoneUploadCorrupted;
    private JTextArea areaStampa;
    private JComboBox<String> boxSceltaAlgoritmo;
    private String IP;
    private int Porta;

    public algoritmoHashingSender(String localIP, int localPorta)
    {   
        this.IP = localIP;
        this.Porta = localPorta;
        
        setTitle("Esercitazione Hashing");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        bottoneUpload = new JButton("Carica File");
        bottoneUploadCorrupted = new JButton("Carica File Corrotto");
        areaStampa = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaStampa);
        areaStampa.setEditable(false);

        panel.add(bottoneUpload);
        panel.add(bottoneUploadCorrupted);
        panel.add(new JLabel("Selezione algoritmo:"));
        boxSceltaAlgoritmo = new JComboBox<>(new String[]{"MD5", "SHA-1", "SHA-256"});
        panel.add(boxSceltaAlgoritmo);

        bottoneUpload.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                uploadFile();
            }
        });
        
        bottoneUploadCorrupted.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                uploadCorruptedFile();
            }
        });

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void uploadFile()
    { 
        String userDir = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop");
        
        int valoreRitorno = fileChooser.showOpenDialog(null);
        
        if (valoreRitorno == JFileChooser.APPROVE_OPTION)
        {
            File fileSelezionato = fileChooser.getSelectedFile();
            String stringAlgoritmoScelto = (String) boxSceltaAlgoritmo.getSelectedItem();
            
            try
            {
                MessageDigest algoritmoScelto = MessageDigest.getInstance(stringAlgoritmoScelto);
                FileInputStream input = new FileInputStream(fileSelezionato);
                byte[] vettoreBytes = new byte[1024];
                int bytes = 0;
                
                while ((bytes = input.read(vettoreBytes)) != -1)
                {
                    algoritmoScelto.update(vettoreBytes, 0, bytes);
                }
                
                // Vettore con i valori del vettoreBytes hashati 
                byte[] vettoreHashedBytes = algoritmoScelto.digest();
                StringBuilder sb = new StringBuilder();
                
                for (int i = 0; i < vettoreHashedBytes.length; i++)
                {
                    byte b = vettoreHashedBytes[i];
                    sb.append(String.format("%02x", b));
                }
                
                String valoreHashFinale = sb.toString();
                areaStampa.append("Uploaded File Hash (" + stringAlgoritmoScelto + "): " + valoreHashFinale + "\n");

                // Crea connessione al downloader
                Socket socket = new Socket(IP, Porta);
                OutputStream output = socket.getOutputStream();
                ObjectOutputStream objOutput = new ObjectOutputStream(output);

                // Invia il nome del file e gli hash al downloader
                objOutput.writeObject(fileSelezionato.getName());
                objOutput.writeObject(valoreHashFinale);

                // Invia il contenuto del file al downloader
                BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(fileSelezionato));
                byte[] buffer = new byte[1024];
                int contatore;
                
                while ((contatore = bufferInput.read(buffer)) > 0)
                {
                    output.write(buffer, 0, contatore);
                }

                input.close();
                bufferInput.close();
                objOutput.close();
                output.close();
                socket.close();
                
            }
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        }
    }
    
    private void uploadCorruptedFile()
    { 
        String userDir = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop");
        
        int valoreRitorno = fileChooser.showOpenDialog(null);
        
        if(valoreRitorno == JFileChooser.APPROVE_OPTION)
        {
            File fileSelezionato = fileChooser.getSelectedFile();
            String stringAlgoritmoScelto = (String) boxSceltaAlgoritmo.getSelectedItem();
            
            try
            {
                MessageDigest algoritmoScelto = MessageDigest.getInstance(stringAlgoritmoScelto);
                FileInputStream input = new FileInputStream(fileSelezionato);
                byte[] vettoreBytes = new byte[1024];
                int bytes = 0;
                
                while ((bytes = input.read(vettoreBytes)) != -1)
                {
                    // Simula la corruzione del file
                    corruptData(vettoreBytes);
                    algoritmoScelto.update(vettoreBytes, 0, bytes);
                }
                
                // Vettore con i valori del vettoreBytes hashati 
                byte[] vettoreHashedBytes = algoritmoScelto.digest();
                StringBuilder sb = new StringBuilder();
                
                for (int i = 0; i < vettoreHashedBytes.length; i++)
                {
                    byte b = vettoreHashedBytes[i];
                    sb.append(String.format("%02x", b));
                }
                
                String valoreHashFinale = sb.toString();
                areaStampa.append("Uploaded File Hash (" + stringAlgoritmoScelto + "): " + valoreHashFinale + "\n");

                // Crea connessione al downloader
                Socket socket = new Socket(IP, Porta);
                OutputStream output = socket.getOutputStream();
                ObjectOutputStream objOutput = new ObjectOutputStream(output);

                // Invia il nome del file e gli hash al downloader
                objOutput.writeObject(fileSelezionato.getName());
                objOutput.writeObject(valoreHashFinale);

                // Invia il contenuto del file al downloader
                BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(fileSelezionato));
                byte[] buffer = new byte[1024];
                int contatore;
                
                while ((contatore = bufferInput.read(buffer)) > 0)
                {
                    output.write(buffer, 0, contatore);
                }

                input.close();
                bufferInput.close();
                objOutput.close();
                output.close();
                socket.close();
                
            } 
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void corruptData(byte[] dataBytes)
    {
        // Simula la corruzione
        byte savedBytes = dataBytes[0];
        dataBytes[0] = (byte) (Math.random() * 255);
        
        while(dataBytes[0] == savedBytes)
        {
            dataBytes[0] = (byte) (Math.random() * 255);
        }
    }
}
