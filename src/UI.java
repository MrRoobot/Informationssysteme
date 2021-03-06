import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UI extends javax.swing.JFrame{

        private JPanel jPanel1 = new javax.swing.JPanel();

        
        private JLabel instructions = new javax.swing.JLabel();
        private JButton jButton1 = new javax.swing.JButton();
        private JTextField inputField = new javax.swing.JTextField();
        public JLabel result=new JLabel();
        private Font f = new Font("serif", Font.PLAIN, 20);
        private static NaiveBayes naiveBayes=new NaiveBayes();



        public UI() {
            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jPanel1.setLayout( new javax.swing.BoxLayout(
                    jPanel1, javax.swing.BoxLayout.Y_AXIS ) );


            instructions.setFont(f);
            instructions.setText("<html><body>Geben sie hier die Bewertung für: Essen, Service,<br>Qualität und Einrichtung ein Bsp: 1,2,3,4</body></html>");

            jPanel1.add(instructions);

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

            inputField.setFont(f);
            inputField.setText ("") ;
            result.setFont(f);
            result.setText("Result:");


            jPanel1.add(inputField) ;
            jPanel1.add(result);
            jPanel1.add(jButton1) ;
            this.getContentPane().add ( jPanel1 ) ;
            pack();
        }


        private void jButton1ActionPerformed( java.awt.event.ActionEvent evt ) throws Exception {
            String ratings=inputField.getText();
            System.out.println(checkValidInput(ratings));
            if(checkValidInput(ratings)) {
                generateRatingsFile(ratings);
                naiveBayes.naiveBayes();
                result.setText("Result: " + naiveBayes.bayesResult);
            }else {
                result.setText("Input is not valid. Please check your input.");
            }
        }

        private boolean checkValidInput(String input){
            boolean validInput = false;
            if(input.length() == 7){
                if(input.matches("[1-5,]+")){
                    validInput = true;
                }
            }
            return validInput;
        }


        public static void main(String args[]) {
            UI test=new UI();
            test.setVisible(true);
            test.setSize(500,210);
            test.setTitle("NaiveBayes Predictor");

        }

        private void generateRatingsFile(String ratings){
            try{
                File file = new File("TestDaten.arff");
                String weka = "@relation 'TestDaten -weka.filters.unsupervised.attribute.Reorder-R2,3,4,5,1'\n" +
                        "\n" +
                        "@attribute Essen {1,2,3,4,5}\n" +
                        "@attribute Service {1,2,3,4,5}\n" +
                        "@attribute Qualität {1,2,3,4,5}\n" +
                        "@attribute Einrichtung {1,2,3,4,5}\n" +
                        "@attribute Gesamtbewertung {1,2,3,4,5}\n";
                if(file.createNewFile()){
                    FileWriter fileWriter = new FileWriter(file,true);
                    fileWriter.write(weka);
                    fileWriter.write("\n@data\n" + ratings + ",?");
                    fileWriter.close();
                }else{
                    System.out.println("File exists");
                    FileWriter fileWriter = new FileWriter(file,false);
                    fileWriter.write(weka);
                    fileWriter.write("\n@data\n" + ratings +",?");
                    fileWriter.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

}
