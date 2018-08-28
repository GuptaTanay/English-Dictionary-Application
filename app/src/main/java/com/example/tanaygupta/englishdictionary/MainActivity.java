package com.example.tanaygupta.englishdictionary;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanaygupta.englishdictionary.utility.Operation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    EditText et;
    String word;
    ImageButton img,img1,img2;
    TextToSpeech t1;
    public boolean eventcheck= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.textView);

        et= (EditText) findViewById(R.id.editText);
        img= (ImageButton) findViewById(R.id.imageButton);
        img1= (ImageButton) findViewById(R.id.imgstart);
        img2= (ImageButton) findViewById(R.id.imgstop);
        texttoSpeech();
    }

    @Override
    protected void onResume() {
        super.onResume();
        texttoSpeech();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        texttoSpeech();
    }

    public void texttoSpeech() {
        t1 = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener()
                {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.UK);
                        }
                    }
                });
    }

    public void play(View view) {
            String toSpeak = et.getText().toString();
            if(toSpeak.isEmpty())
            {
                toSpeak="Please enter a word to get results";
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        else{
                t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
            }
    }

    public void fire(View view)
    {
            String speak=tv.getText().toString();
            t1.speak(speak,TextToSpeech.QUEUE_FLUSH,null);
    }
    public void stopping(View view)
    {
        if(t1 !=null){
            t1.stop();
        }
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();}


    public void search(View  view){
        String message="";
        word= et.getText().toString();
        if(word.isEmpty())
        {
            message="Please Enter a Word";
            Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT).show();
        }else{
            new AccessWebServiceTask().execute(word);
        }

    }

    private String wordDefinition(String s) {
        DocumentBuilder db;
        String result = "";
        InputStream in = null;
        try {
            Operation op = new Operation();
            try {
                in = op.openGetURLConnection(Config.DICTIONARY_URL + "?word=" + word);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Document doc = null;
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();

            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();
            NodeList definitionElements =
                    doc.getElementsByTagName("Definition");
            for (int i = 0; i < definitionElements.getLength(); i++) {
                Node itemNode = definitionElements.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element definitionElement = (Element) itemNode;
                    NodeList wordDefinitionElements =
                            (definitionElement).getElementsByTagName("WordDefinition");
                    String strDefinition = "";
                    for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
                        Element wordDefinitionElement =
                                (Element) wordDefinitionElements.item(j);
                        NodeList textNodes =
                                ((Node) wordDefinitionElement).getChildNodes();
                        strDefinition +=
                                ((Node) textNodes.item(0)).getNodeValue() + ".\n\n\n";
                    }
                    result += strDefinition;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
     private class AccessWebServiceTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd= new ProgressDialog(MainActivity.this);
            pd.setTitle("Searching for Word");
            pd.setMessage("Please wait..." +
                    "It may take a while...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return wordDefinition(params[0]);
        }
        protected void onPostExecute(String s){
            pd.dismiss();
            tv.setText(s);
            if(s.isEmpty())
            {
                Toast.makeText(MainActivity.this, "No Word Found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.firstitem:
                showDialog(0);
                break;
            case R.id.seconitem:
                showDialog(1);
                break;
            case R.id.thirditem:
                showDialog(2);
                break;
            case R.id.Fourthitem:
                showDialog(3);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case 0:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Help!");
                String m="Hello there, This App only works with Internet Connectivity!\n\nSo if you're unable to find thee word..\n\nPlease switch on your Internet connection.";
                builder.setMessage(m);
               return builder.create();
            case 1:
                AlertDialog.Builder buil=new AlertDialog.Builder(this);
                buil.setTitle("Contact Us!");
                String m1="Hello there, You can contact us by dropping an email to \n\n";
                m1+="tanaygupta.gupta@gmail.com";
                buil.setMessage(m1);
               return  buil.create();
            case 2:
                AlertDialog.Builder b=new AlertDialog.Builder(this);
                b.setTitle("About?");
                String m2="Hello there, Welcome to English Dictionary. \n\n";
                m2+="This dictionary fetches your word information from the Internet";
                b.setMessage(m2);
                return b.create();
            case 3:
                finish();
        }

        return null;
    }
}
