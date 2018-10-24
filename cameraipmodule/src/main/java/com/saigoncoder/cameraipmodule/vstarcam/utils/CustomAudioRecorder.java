package com.saigoncoder.cameraipmodule.vstarcam.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class CustomAudioRecorder {
	
	private AudioRecordResult audioResult = null;
	private Thread recordThread = null;
	private boolean bRecordThreadRuning = false;
	private int m_in_buf_size = 0;
	private AudioRecord m_in_rec = null;
	private byte[] m_in_bytes = null;
	private boolean isInitRecorder = false;


	public final static int ERROR_NOT_INIT = 1;

	public interface AudioRecordResult{
		void AudioRecordData(byte[] data, int len);
		void AudioError(int code);
	}
	
	public CustomAudioRecorder(AudioRecordResult result){
		audioResult = result;
		isInitRecorder = initRecorder();
	}
	
	public void StartRecord(){
		Log.e("listent", "StartRecord-------------");
		synchronized (this) {
			Log.e("listent", "bRecordThreadRuning: " + bRecordThreadRuning);
			if (bRecordThreadRuning) {
				return ;
			}
			
			bRecordThreadRuning = true;
			recordThread = new Thread(new RecordThread());
			recordThread.start();
		}
	}
	
	public void StopRecord(){
		Log.e("listent", "StopRecord bRecordThreadRuning: " + bRecordThreadRuning);
		synchronized (this) {
			if (!bRecordThreadRuning) {
				return;
			}
			Log.e("listent", "StopRecord bRecordThreadRuning: " + bRecordThreadRuning);
			bRecordThreadRuning = false;
			try {
				recordThread.join();
			} catch (Exception e) {
				// TODO: handle exception
			}
			releaseRecord();
		}
	}
	public void releaseRecord(){
		Log.e("listent","releaseRecord");
		m_in_rec.release();
		m_in_rec=null;
	}
	class RecordThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!initRecorder()) {
				Log.e("listent", "!!!!!!!!!!!!!!!!!");
				return;
			}
			Log.e("listent", "m_in_rec: " + m_in_rec);
			Log.e("listent", "startRecording-------------");
			try{
				m_in_rec.startRecording() ;
				while(bRecordThreadRuning){
					int nRet = m_in_rec.read(m_in_bytes, 0, m_in_buf_size) ;
					Log.e("listent", "1111");
					if (nRet == 0) {
						Log.e("listent", "2222");
						return;
					}

					if (audioResult != null) {
						Log.e("listent", "3333");
						audioResult.AudioRecordData(m_in_bytes, nRet);
					}
				}
				m_in_rec.stop();
			}catch (Exception e){
				Log.e("listent", "startRecording Exception: " + e.toString());
				if(audioResult != null){
					audioResult.AudioError(ERROR_NOT_INIT);
				}
			}

		}
		
	}

	public boolean initRecorder() {
		// TODO Auto-generated method stub
		Log.e("listent", "initRecorder()");
		m_in_buf_size =  AudioRecord.getMinBufferSize(8000,  AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,  AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
		m_in_buf_size) ;
		if (m_in_rec == null) {
			Log.e("listent", "444");
			return false;
		}
		Log.e("listent", "555 m_in_buf_size: " + m_in_buf_size);
		m_in_bytes = new byte [m_in_buf_size] ;
		return true;
	}
	
	
}