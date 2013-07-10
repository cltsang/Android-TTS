package csci3310.assignment3;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.speech.tts.*;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
	
	private final int numResults = 3;
	private final int vrRequestCode = 1234;
	
	private Button speakButton, voiceButton;
	private EditText textField;
	private ListView listView;
	
	private TextToSpeech ttsEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ttsEngine = new TextToSpeech(this, this);
		
		speakButton = (Button)findViewById(R.id.speak_btn);
		voiceButton = (Button)findViewById(R.id.voice_btn);
		textField = (EditText)findViewById(R.id.textfield);
		listView = (ListView)findViewById(R.id.listView);
		
		initVoiceRecognizor();
	}
	
	private void speak(String string){
		if(ttsEngine != null)
			ttsEngine.speak(string, TextToSpeech.QUEUE_ADD, null);
	}
	
	private void listen(){
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, numResults);
        startActivityForResult(listenIntent, vrRequestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == vrRequestCode && resultCode == RESULT_OK){
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, results));
			for(String string : results)
				speak(string);
		}
	}

	private void initVoiceRecognizor(){
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		if(!list.isEmpty()){
			speakButton.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					listen();
				}
			});
		}
		else{
			speakButton.setEnabled(false);
			speakButton.setText("Recognizer not present");
		}
	}

	@Override
	public void onInit(int status) {
		if(status == TextToSpeech.SUCCESS){
			voiceButton.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					speak(textField.getText().toString());				
				}
			});
		}
		else{
			ttsEngine = null;
			voiceButton.setEnabled(false);
			voiceButton.setText("TTS engine not present");
		}
	}

}
