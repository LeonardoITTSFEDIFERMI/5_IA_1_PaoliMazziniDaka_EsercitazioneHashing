package pkg5_ia_1_paolimazzinidaka_esercitazionehashing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author Leonardo Mazzini Drovandi & Flavio Paoli
 */

public class InterfacciaScelta extends JFrame implements ActionListener
{    
    private JPanel p; //Creazione pannello
    private JLabel labelScelta = new JLabel("Scegli se inviare o ricevere");
    private JButton buttonInviare = new JButton("Inviare");
    private JButton buttonRicevere = new JButton("Ricevere");
    private FlowLayout Flow = new FlowLayout();
    
    public InterfacciaScelta()
    { 
        super("Interfaccia");
        p = new JPanel();
        
        p=(JPanel)this.getContentPane();
        p.setLayout(Flow);
        
        p.add(labelScelta);
        p.add(buttonInviare);
        p.add(buttonRicevere);
        
        buttonInviare.addActionListener(this);
        buttonRicevere.addActionListener(this);
        
        this.setBounds(650, 300, 200, 100);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {        
        if(e.getSource() == buttonInviare)
        {
            this.dispose();
            
            InterfacciaConnessioneSender intConSender = new InterfacciaConnessioneSender();    
        }
        
        if(e.getSource() == buttonRicevere)
        {
            this.dispose();       
            
            InterfacciaConnessioneReceiver intConReceiver = new InterfacciaConnessioneReceiver();    
        }      
    }
}
