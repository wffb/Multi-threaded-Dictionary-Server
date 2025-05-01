//BO HUANG 1584795

import com.alibaba.fastjson2.JSONObject;
import common.config.Params;
import common.feedback.RequestCode;
import common.feedback.ResponseCode;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

@Slf4j
public class Gui {

    // item for pannel
    JComboBox<String> cmb;

    private JTextField wordText;

    private JTextArea meaningText;
    private JTextArea newMeaningText;
    private JLabel newMeaningLabel;

    private JLabel warningLabel;

    public static Gui INSTANCE ;


    public static void start() {
        INSTANCE = new Gui();
        INSTANCE.init();
    }

    //item for cmb
    private final String addWordS = "AddWord";
    private final String addMeaningS = "AddMeaning";
    private final String deleteS = "Delete";
    private final String updateS = "Update";
    private final String searchS = "Search";


    private void init() {

        JFrame frame = new JFrame("Dictionary Client");
        // Setting the width and height of frame
        frame.setSize(600, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        // add panel
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);

        log.info("The user interface has been successfully launched.");
    }

    private void placeComponents(JPanel panel) {

        panel.setLayout(null);


        /**
         * Operation options
         */

        cmb=new JComboBox<String>();
        cmb.setBounds(270,5, 80, 25);

        cmb.addItem(addWordS);
        cmb.addItem(deleteS);
        cmb.addItem(updateS);
        cmb.addItem(searchS);
        cmb.addItem(addMeaningS);

        cmb.addActionListener((ActionEvent e)->{

            if(Objects.equals(cmb.getSelectedItem(), updateS)){
                newMeaningText.setVisible(true);
                newMeaningLabel.setVisible(true);
            }
            else{
                newMeaningText.setVisible(false);
                newMeaningLabel.setVisible(false);
            }

        });

        panel.add(cmb);


        /**
         *  words input
         */
        JLabel wordLabel = new JLabel("the word:");
        wordLabel.setBounds(110,50,80,25);
        panel.add(wordLabel);

        wordText = new JTextField();
        wordText.setBounds(210,50,260,25);
        panel.add(wordText);




        /**
         * Output: Meaning(s) of the word
         */
        JLabel meaningLabel = new JLabel("content :");
        meaningLabel.setBounds(110,80,80,25);
        panel.add(meaningLabel);

        meaningText = new JTextArea("The meaning will be presented here。");
        meaningText.setBounds(210,80,260,100);
        meaningText.setLineWrap(true);
        panel.add(meaningText);

        newMeaningLabel = new JLabel("new meaning:");
        newMeaningLabel.setBounds(110,190,80,25);
        panel.add(newMeaningLabel);

        newMeaningText = new JTextArea("The new meaning will be presented here。");
        newMeaningText.setBounds(210,200,260,100);
        newMeaningText.setLineWrap(true);
        panel.add(newMeaningText);

        newMeaningText.setVisible(false);newMeaningLabel.setVisible(false);

        /**
         * buttons
         */
        //execute
        JButton executeButton = new JButton("Execute");
        executeButton.setBounds(270, 340, 80, 25);
        executeButton.addActionListener((ActionEvent e)->{
            //get
            clanError();
            doWrite();
        });

        panel.add(executeButton);

        /**
         *  warning
         **/
        warningLabel = new JLabel("warning here!");
        warningLabel.setBounds(160,310,340,25);
        warningLabel.setFont(new Font("Arial",Font.BOLD,12));
        warningLabel.setForeground(Color.RED);
        warningLabel.setVisible(false);
        panel.add(warningLabel);

    }


    private void doWrite(){

        String type = (String) cmb.getSelectedItem();
        //judge type
        if(Objects.isNull(type)){
            setError("The operation type cannot be empty.");
            return;
        }

        String key = wordText.getText(); boolean isKey = Objects.isNull(key) || key.isEmpty();
        if(isKey){
            setError("The word cannot be empty.");
            return;
        }
        String value = meaningText.getText(); boolean isValue = Objects.isNull(value) || value.isEmpty();


        //operations
        switch (type) {

            case addWordS:
                if (isValue) {
                    setError("The meaning cannot be empty.");
                    return;
                }
                //format judge

                SocketClient.wirte(RequestCode.ADD_WORD.getCode(), key, value);

                break;

            case addMeaningS:
                if (isValue) {
                    setError("The meaning cannot be empty.");
                    return;
                }
                //format judge

                SocketClient.wirte(RequestCode.ADD_MEANING.getCode(), key, value);

                break;


            case updateS:
                if (isValue) {
                    setError("The existing meaning cannot be empty.");
                    return;
                }
                String newMeaning = newMeaningText.getText();
                if(Objects.isNull(newMeaning) || newMeaning.isEmpty()){
                    setError("The new meaning cannot be empty.");
                    return;
                }
                if(newMeaning.equals(value)){
                    setError("The new meaning should not be equal to existing meaning");
                    return;
                }

                SocketClient.wirte(RequestCode.UPDATE.getCode(), key, value,newMeaning);

                break;
            case deleteS:

                SocketClient.wirte(RequestCode.DELETE.getCode(), key);

                break;
            case searchS:

                SocketClient.wirte(RequestCode.SEARCH.getCode(), key);
                break;
        }

    }


    // show error
    public static void setError(String s){
        if(!Objects.isNull(INSTANCE) && !Objects.isNull(s)){
            INSTANCE.warningLabel.setText(s);
            INSTANCE.warningLabel.setVisible(true);
        }
    }

    private static void clanError(){
        if(!Objects.isNull(INSTANCE)){
            INSTANCE.warningLabel.setText("");
            INSTANCE.warningLabel.setVisible(false);
        }
    }

    // show feedback
    public static void setFeedback(JSONObject obj){

        if(!Objects.isNull(INSTANCE) && !Objects.isNull(obj)){
            String status =  obj.get("status").toString();
            if(status.equals(ResponseCode.SUCCESS.getCode().toString())){
                setSuccess(obj);
            }
            //fail
            if(status.equals(ResponseCode.FAILED.getCode().toString())){
                setError((String) obj.get("info"));
            }

        }
    }

    private static void setSuccess(JSONObject obj){
        //success
        String receS = (String) obj.get("info");
        String res = "";
        // is empty
        if(Objects.isNull(receS)||receS.isEmpty()){
            setError("Received feedback information is empty.");
        }
        //process
        String type = (String) obj.get("operationType");
        if(type.equals(RequestCode.SEARCH.getCode())){
            String s = (String) obj.get("info");
            String[] ss = s.split(Params.MEANING_QELIIMTER);

            for(int i=0;i< ss.length;i++){
                res += ((i+1) + ". "+ ss[i]);
                if(i< ss.length-1)
                    res+="\n";
            }
        }
        else
            res = receS;

        INSTANCE.meaningText.setText(res);
    }
}