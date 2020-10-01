import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client extends JFrame implements  ActionListener, KeyListener{
    public static Socket socketClient;
    public static DataInputStream ClientInput;
    public static DataOutputStream ClientOutput;
    public String SelectedText;
    public String ClientIDToShare;
    JTextArea t;
    JFrame f;
    public static void main(String[] args) {
        try {
            socketClient = new Socket("2.tcp.ngrok.io",13355);
            System.out.println("Connected !");
            Client c1 = new Client();
            Scanner scn = new Scanner(System.in);
            ClientInput = new DataInputStream(socketClient.getInputStream());
            ClientOutput = new DataOutputStream(socketClient.getOutputStream());
            System.out.println("Enter your id/username");
            String id = scn.nextLine();
            ClientOutput.writeUTF(id);
            ClientOutput.flush();
            System.out.println("Write your frame name");
            String filename = scn.nextLine();
            c1.ClientGUI(filename);

            System.out.println("now starts the real connection");
            while(true) {
                String NewDataInTextArea = ClientInput.readUTF();
                c1.ChangeText(NewDataInTextArea);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ClientGUI(String name) {
        f = new JFrame(name);
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
        t = new JTextArea();
        t.setLineWrap(true);
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi10 = new JMenuItem("Share");

        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi10.addActionListener(this);

        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m1.add(mi10);

        JMenuItem mc = new JMenuItem("close");

        mc.addActionListener(this);

        mb.add(m1);
        mb.add(mc);

        f.setJMenuBar(mb);
        f.add(t);
        f.setSize(500, 500);
        f.show();
        t.addKeyListener(this);
    }
    public void ChangeText(String str) {
        t.setText(str);
    }
    // nice implementation
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if(s.equals("close")) {
            f.setVisible(false);
            try{
                ClientOutput.close();
                ClientInput.close();
                socketClient.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if(s.equals("Share")) {
            try {
                ClientOutput.writeUTF("Share");
                ClientOutput.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            ClientIDToShare = JOptionPane.showInputDialog("Enter the id's of client to share");
            try {
                ClientOutput.writeUTF(ClientIDToShare);
                ClientOutput.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
    }
            // done checking and changes made
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        SelectedText = t.getText();
        try{
            ClientOutput.writeUTF(SelectedText);
            ClientOutput.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
