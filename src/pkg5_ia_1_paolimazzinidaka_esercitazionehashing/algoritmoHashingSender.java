package pkg5_ia_1_paolimazzinidaka_esercitazionehashing;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;

/**
 *
 * @author Leonardo Mazzini Drovandi & Flavio Paoli
 */

public class algoritmoHashingSender extends JFrame
{
    JLabel labelSceltaAlgoritmo = new JLabel("Selezione algoritmo:");
    private JButton bottoneInvio, bottoneInvioCorruzione;
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

        bottoneInvio = new JButton("Invia File");
        bottoneInvioCorruzione = new JButton("Invia file simulando corruzione");
        areaStampa = new JTextArea();
        boxSceltaAlgoritmo = new JComboBox<>(new String[]{"MD5", "SHA-1", "SHA-256"});
        JScrollPane scrollPane = new JScrollPane(areaStampa);
        areaStampa.setEditable(false);

        panel.add(bottoneInvio);
        panel.add(bottoneInvioCorruzione);
        panel.add(labelSceltaAlgoritmo);
        panel.add(boxSceltaAlgoritmo);

        bottoneInvio.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                invioFile();
            }
        });
        
        bottoneInvioCorruzione.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                invioFileCorruzione();
            }
        });

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void invioFile()
    { 
        String userDir = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop");
        
        int tempReturn = fileChooser.showOpenDialog(null);
        
        if(tempReturn == JFileChooser.APPROVE_OPTION)
        {
            File fileSelezionato = fileChooser.getSelectedFile();
            String stringAlgoritmoScelto = (String) boxSceltaAlgoritmo.getSelectedItem();
            
            try
            {
                MessageDigest algoritmoScelto = MessageDigest.getInstance(stringAlgoritmoScelto);
                FileInputStream input = new FileInputStream(fileSelezionato);
                byte[] vettoreBytes = new byte[1024];
                int byteLetto = 0;
                
                while ((byteLetto = input.read(vettoreBytes)) != -1)
                {
                    algoritmoScelto.update(vettoreBytes, 0, byteLetto);
                }
                
                // Vettore con i valori del vettoreBytes hashati 
                byte[] vettoreHashByte = algoritmoScelto.digest();
                StringBuilder tempStringBuilder = new StringBuilder();
                
                for (int i = 0; i < vettoreHashByte.length; i++)
                {
                    byte tempByteLetto = vettoreHashByte[i];
                    //Conversione in esadecimale
                    tempStringBuilder.append(String.format("%02x", tempByteLetto));
                }
                
                String valoreHashFinale = tempStringBuilder.toString();
                areaStampa.append("CLIENT\nAlgoritmo scelto: " + stringAlgoritmoScelto + "\nHash: " + valoreHashFinale + "\n\n");

                // Crea connessione al server
                Socket socket = new Socket(IP, Porta);
                OutputStream output = socket.getOutputStream();
                ObjectOutputStream objOutput = new ObjectOutputStream(output);

                // Invia il nome del file e gli hash al downloader
                objOutput.writeObject(fileSelezionato.getName());
                objOutput.writeObject(valoreHashFinale);

                // Invia il contenuto del file al downloader
                BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(fileSelezionato));
                byte[] buffer = new byte[1024];
                int tempbyteLetto;
                
                while ((tempbyteLetto = bufferInput.read(buffer)) > 0)
                {
                    output.write(buffer, 0, tempbyteLetto);
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
    
    private void invioFileCorruzione()
    {
        String userDir = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser(userDir +"/Desktop");
        
        int tempReturn = fileChooser.showOpenDialog(null);
        
        if(tempReturn == JFileChooser.APPROVE_OPTION)
        {
            File fileSelezionato = fileChooser.getSelectedFile();
            File tempFile = new File(userDir +"/Desktop"+"tempFile.txt");
            
            copiaFile(fileSelezionato.toPath(), tempFile.toPath());
            
            String stringAlgoritmoScelto = (String) boxSceltaAlgoritmo.getSelectedItem();
            
            try
            {
                MessageDigest algoritmoScelto = MessageDigest.getInstance(stringAlgoritmoScelto);
                FileInputStream input = new FileInputStream(fileSelezionato);
                byte[] vettoreBytes = new byte[1024];
                int byteLetto = 0;
                
                while ((byteLetto = input.read(vettoreBytes)) != -1)
                {
                    algoritmoScelto.update(vettoreBytes, 0, byteLetto);
                }
                
                // Vettore con i valori del vettoreBytes hashati (crea l'HASH)
                byte[] vettoreHashByte = algoritmoScelto.digest();
                StringBuilder tempStringBuilder = new StringBuilder();
                
                // Corrompe il contenuto del file da inviare
                byte[] vectRandomByte = generaDatiCasuali();
                simulaCorruzione(fileSelezionato, vectRandomByte);
                
                for (int i = 0; i < vettoreHashByte.length; i++)
                {
                    byte tempByteLetto = vettoreHashByte[i];
                    //Conversione in esadecimale
                    tempStringBuilder.append(String.format("%02x", tempByteLetto));
                }
                
                String valoreHashFinale = tempStringBuilder.toString();
                areaStampa.append("CLIENT\nAlgoritmo scelto: " + stringAlgoritmoScelto + "\nHash: " + valoreHashFinale + "\n\n");

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
                int tempbyteLetto;
                
                while ((tempbyteLetto = bufferInput.read(buffer)) > 0)
                {
                    output.write(buffer, 0, tempbyteLetto);
                }
                
                input.close();
                bufferInput.close();
                objOutput.close();
                output.close();
                socket.close();
                
                copiaFile(tempFile.toPath(), fileSelezionato.toPath());
                tempFile.delete();
                
            } 
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    private void simulaCorruzione(File file, byte[] vettoreByte) 
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            // Aggiungi l'array di byte al file
            fileOutputStream.write(vettoreByte);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Errore", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static byte[] generaDatiCasuali() 
    {
        int lunghezza = 100;
        byte[] datiCasuali = new byte[lunghezza];
        SecureRandom random = new SecureRandom();
        random.nextBytes(datiCasuali);
        return datiCasuali;
    }
    
    private static void copiaFile(Path pathFileMittente, Path pathFileDestinatario)
    {
        try{
            Files.copy(pathFileMittente, pathFileDestinatario, StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Errore", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
