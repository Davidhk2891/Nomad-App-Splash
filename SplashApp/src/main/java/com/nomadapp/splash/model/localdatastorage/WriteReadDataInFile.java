package com.nomadapp.splash.model.localdatastorage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by David on 10/16/2017.
 */

public class WriteReadDataInFile {

    Context ctx;

    public WriteReadDataInFile(Context ctx) {
        this.ctx = ctx;
    }

    public void writeToFile(String myData, String titleWrite){

        try{

            OutputStreamWriter myOutputStreamWriter = new OutputStreamWriter(ctx.openFileOutput
                    (titleWrite + "text.txt", Context.MODE_PRIVATE));

            myOutputStreamWriter.write(myData);

            myOutputStreamWriter.close();

        }catch(IOException e){

            Log.v("MyActivity", e.toString());

        }

    }

    public String readFromFile(String titleRead){

        String result = "";

        try{

            InputStream myInputStream = ctx.openFileInput(titleRead + "text.txt");

            if (myInputStream != null){

                InputStreamReader myInputStreamReader = new InputStreamReader(myInputStream);

                BufferedReader myBufferedReader = new BufferedReader(myInputStreamReader);

                String tempString = "";

                StringBuilder myStringBuilder = new StringBuilder();

                while ((tempString = myBufferedReader.readLine()) != null){

                    myStringBuilder.append(tempString);

                }

                myInputStream.close();

                result = myStringBuilder.toString();

            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return result;

    }

}
