package com.nix.jingxun.addp.ssh.common;
/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the user authentification by public key.
 *   $ CLASSPATH=.:../build javac UserAuthPubKey.java
 *   $ CLASSPATH=.:../build java UserAuthPubKey
 * You will be asked username, hostname, privatekey(id_dsa) and passphrase.
 * If everything works fine, you will get the shell prompt
 *
 */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

public class ShellByKey {
    public static void main(String[] arg){

        try{
            JSch jsch=new JSch();

//            JFileChooser chooser = new JFileChooser();
//            chooser.setDialogTitle("Choose your privatekey(ex. ~/.ssh/id_dsa)");
//            chooser.setFileHidingEnabled(false);
//            int returnVal = chooser.showOpenDialog(null);
//            if(returnVal == JFileChooser.APPROVE_OPTION) {
//                System.out.println("You chose "+
//                        chooser.getSelectedFile().getAbsolutePath()+".");
//                jsch.addIdentity(chooser.getSelectedFile().getAbsolutePath()
////			 , "passphrase"
//                );
//            }

            jsch.addIdentity("no",("-----BEGIN RSA PRIVATE KEY-----\n" +
                    "Proc-Type: 4,ENCRYPTED\n" +
                    "DEK-Info: AES-128-CBC,261D4541B5FC08BBDD8EAF232F1C4C19\n" +
                    "\n" +
                    "xrOjJD2QJC6/Z5p9T5o8BgFTrVjFDjEpeaw1JXai7plzwyDTyTyYLdOmyx9y5hYb\n" +
                    "xBIFY86qOgbHSqFHsrRHID7cOPeL+fQZCz5UtNY5SCyvx1gcU//+iNA3238BZ/t6\n" +
                    "/YU9L4qVfepTYRqxdpBG3fsFrO/FlzLn1JJHqMvw5V/3swji2i4hE0AHAwWrJZ5z\n" +
                    "GC8jq8PLrIrcW16LgdQkMIuamr86ZH+eXF0MA6wSyG/Ehz2HKbFu8o11PSuLDOi9\n" +
                    "uywsEXxU3OjY/MNlUcTyp7IvclYvR8BT+5NQtEyU57RdQ2lS2t3fHP571IQKkQR1\n" +
                    "QKDnpsJf4pl5aTXoZgP4MtpvO6/V+L8xoQbExrh/GIlFBbMxv8o59+Zj+UQ254WY\n" +
                    "V87I/5jmojaUMOX2edcT+pTn0LUPenfjFRv10jda7Wq0tz8o5/TovfNUugWdn8D2\n" +
                    "RbCNXlSc3EGxkunNb6WAZhVqPH+yzsDPSQ5yLLaqBiWAFI8FHFT8OREDIve4jNC8\n" +
                    "YMxkwiY5O1gHi/A7sZomV/vaYG1utFGmGkIIKrfttPrM43pPte1ncAoUZUiMnTLU\n" +
                    "hPGmv+ggyqQz+gcFvoGXlaX0QQkXpdNWnrrvjm8oKDTxzJmXf/ch6Jz4mi+rlgAD\n" +
                    "xCeobINW2pc8ju4HIX+4r5zR/8K1H282C1fGi69EZ1BGJkC0++IajcShICQuIO4d\n" +
                    "LTmbHNEHzEbL84ZcYUdyThQQ5SQTR4GeS1aXto2R9X72Pz+Uyy6MvVe6AbSiLqbh\n" +
                    "Lgcnb1mp3FzRjaCXf4dWpbFB2UYogR2DZLnEHjYKLNeeB3126fwnmtJdLQtSrUmR\n" +
                    "bbETK2hT1FlMoBjxdbSpNHTCEOlXchQh7GWtE/Qp0ZXPmdeH/NTkdi+H/5zr3Vs3\n" +
                    "prjKuPgIZ2QbPkXnCNY34kbo75djeiZmkeJHCcCAmTnnOlTA0nI6zcPzoJ2Wgxzx\n" +
                    "Gbns2QAaEQQhNOIGYjDfYnW9hP+PRp8QXzLPSmK+5sf49w9oMK8+N/zNArs6I0Go\n" +
                    "lhlVlvohPTj9o6yVRNrbdbxwOeGJGYV59e4jQLr9hM/7XIPyJdHoB80dVLhWKSXl\n" +
                    "cmVR/Ox8UWIlLi55K0KRsaPCHs6pCPGi11ASirAr84eGKSOWqM0I7NR92J+yR4Vo\n" +
                    "5zevVshpvm52Tj3EFrspm/hiq5roAfUlISPjQjY5xqaL/9YnodlVecVDaP9qPm6w\n" +
                    "GXbWzEcky74TWnzCs29FHWuCU2gftuULCCBlIO+x0cbfhB+wKJCTir6Cj9q1cmdR\n" +
                    "+Uic/9J/uPzf1IUoasLW4tE6IYtSrdI4cMFGFqabAwon6C5XxmTFgWOtD2PQDJLf\n" +
                    "hcCdwl5UwiXnnxpWuz4y2mSJyHLLRmETQIi1xdgviPgicFl5WBJIiZbW9YA8Xo0W\n" +
                    "gbfg3/m+vq4DaO0c7M78SvBje6pOOVRL96YFnxbf+TMXDCe3dMOzjt8HxqMuOTGQ\n" +
                    "Nm8K8r/JWVHkaeEPVoPJ9AT+a5bG7jRRCRdHQ6Sia6CodXYBSP8oyBE+FN9JScmL\n" +
                    "dYib9ssp/UT6mrpjzpTh2/JBtMLa6eT838kj3Ow5qqK+NnwDmeJtQu6AWag7H1so\n" +
                    "-----END RSA PRIVATE KEY-----\n").getBytes(),null,"kiss4400".getBytes());

            Session session=jsch.getSession("root", "59.110.234.213", 22);

            // username and passphrase will be given via UserInfo interface.
            UserInfo ui=new MyUserInfo();
            session.setUserInfo(ui);
            session.connect();

            Channel channel=session.openChannel("shell");

            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){
            Object[] options={ "yes", "no" };
            int foo=JOptionPane.showOptionDialog(null,
                    str,
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            return foo==0;
        }

        String passphrase;
        JTextField passphraseField=(JTextField)new JPasswordField(20);

        public String getPassphrase(){ return passphrase; }
        public boolean promptPassphrase(String message){
            Object[] ob={passphraseField};
            int result=
                    JOptionPane.showConfirmDialog(null, ob, message,
                            JOptionPane.OK_CANCEL_OPTION);
            if(result==JOptionPane.OK_OPTION){
                passphrase=passphraseField.getText();
                return true;
            }
            else{ return false; }
        }
        public boolean promptPassword(String message){ return true; }
        public void showMessage(String message){
            JOptionPane.showMessageDialog(null, message);
        }
        final GridBagConstraints gbc =
                new GridBagConstraints(0,0,1,1,1,1,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new Insets(0,0,0,0),0,0);
        private Container panel;
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridx = 0;
            panel.add(new JLabel(instruction), gbc);
            gbc.gridy++;

            gbc.gridwidth = GridBagConstraints.RELATIVE;

            JTextField[] texts=new JTextField[prompt.length];
            for(int i=0; i<prompt.length; i++){
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.weightx = 1;
                panel.add(new JLabel(prompt[i]),gbc);

                gbc.gridx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 1;
                if(echo[i]){
                    texts[i]=new JTextField(20);
                }
                else{
                    texts[i]=new JPasswordField(20);
                }
                panel.add(texts[i], gbc);
                gbc.gridy++;
            }

            if(JOptionPane.showConfirmDialog(null, panel,
                    destination+": "+name,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)
                    ==JOptionPane.OK_OPTION){
                String[] response=new String[prompt.length];
                for(int i=0; i<prompt.length; i++){
                    response[i]=texts[i].getText();
                }
                return response;
            }
            else{
                return null;  // cancel
            }
        }
    }
}
