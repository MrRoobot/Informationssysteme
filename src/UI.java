import javax.swing.*;
import java.awt.*;

public class UI extends javax.swing.JFrame{

        private JPanel jPanel1 = new javax.swing.JPanel();

        
        private JLabel jLabel1 = new javax.swing.JLabel();
        private JButton jButton1 = new javax.swing.JButton();
        private JTextField jTextField1 = new javax.swing.JTextField();
        public JLabel result=new JLabel();
        private Font f = new Font("serif", Font.PLAIN, 20);
        private static NaiveBayes naiveBayes=new NaiveBayes();



        public UI() {
            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jPanel1.setLayout( new javax.swing.BoxLayout(
                    jPanel1, javax.swing.BoxLayout.Y_AXIS ) );

            jLabel1.setFont(f);
            jLabel1.setText ( "<html><body>Geben sie hier die Bewertung für: Essen, Service,<br>Qualität und Einrichtung ein Bsp: 1,2,3,4</body></html>" );

            jPanel1.add ( jLabel1 );

            jButton1.setFont(f);
            jButton1.setText("Predict");

            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        jButton1ActionPerformed(evt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            jTextField1.setFont(f);
            jTextField1.setText ("") ;
            result.setFont(f);
            result.setText("Result:");


            jPanel1.add(jTextField1) ;
            jPanel1.add(result);
            jPanel1.add(jButton1) ;
            this.getContentPane().add ( jPanel1 ) ;
            pack();
        }


        private void jButton1ActionPerformed( java.awt.event.ActionEvent evt ) throws Exception {
            String ratings=jTextField1.getText();
            System.out.println(ratings);
            naiveBayes.naiveBayes();
            result.setText("Result: "+naiveBayes.bayesResult);
        }


        public static void main(String args[]) {
            UI test=new UI();
            test.setVisible(true);
            test.setSize(500,210);

        }

}
