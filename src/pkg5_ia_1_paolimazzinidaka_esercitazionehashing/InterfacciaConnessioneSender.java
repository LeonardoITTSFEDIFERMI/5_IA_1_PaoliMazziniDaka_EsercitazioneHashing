package pkg5_ia_1_paolimazzinidaka_esercitazionehashing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Integer.parseInt;
import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author Leonardo Mazzini Drovandi & Flavio Paoli
 */

public class InterfacciaConnessioneSender extends JFrame implements ActionListener
{
    private JPanel p; //Creazione pannello
    private JLabel labelIP= new JLabel("Inserisci l'IP:");
    private JTextField campoIP = new JTextField();
    private JLabel labelPorta = new JLabel("Inserisci la porta:");
    private JTextField campoPorta = new JTextField();
    private JButton buttonConferma = new JButton("CONFERMA");
    private FlowLayout Flow = new FlowLayout();
    private static int tempCont = 0;
    private static String IP = null;
    private static String Porta = null;
    
    public InterfacciaConnessioneSender()
    {
        super("Interfaccia");
        p = new JPanel();
        
        p=(JPanel)this.getContentPane();
        p.setLayout(Flow);
        campoIP.setColumns(6);
        campoPorta.setColumns(6);
        
        p.add(labelIP);
        p.add(campoIP);
        p.add(labelPorta);
        p.add(campoPorta);
        p.add(buttonConferma);
        
        buttonConferma.addActionListener(this);
        
        this.setBounds(650, 300, 250, 150);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {     
        if(e.getSource() == buttonConferma && !campoIP.getText().isEmpty() && !campoPorta.getText().isEmpty())
        {
            while(Porta==null || Porta=="" || campoPorta.getText().isEmpty() || IP==null || IP=="" || campoIP.getText().isEmpty()){
            IP = campoIP.getText();
            Porta = campoPorta.getText();
        }
            new algoritmoHashingSender(IP, parseInt(Porta));
            this.dispose();   
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi");
        }
    }
    
    public String getIP()
    {
        return this.IP;
    }
    
    public String getPorta()
    {
        return this.Porta;
    }
}
