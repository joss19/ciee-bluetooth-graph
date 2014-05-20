package com.demo.androtest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.demo.androtest.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class MainActivity extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter;
	TextView text;
	ScrollView scroll;
	String xData;
	String xxxData;
	float aValue;
	
	int numberOfPoint = 100;
	
	ArrayList<Integer> xArray = new ArrayList<Integer>();
	ArrayList<Float> yArray = new ArrayList<Float>();
	
    private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;
    
    Timer timer;
    Timer timer2;
    int iii = 0;
	
	private static int countLines(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length;
		}

	
	protected void updateConsole(String newLine) {
		String aText = (String) text.getText();
    	
    	int lineNumber = countLines(aText);
    	if(lineNumber >= 20)
    		aText = aText.substring(aText.indexOf('\n')+1);
    	
    	aText = aText + "\n" + newLine;
        text.setText(aText);
		scroll.fullScroll(View.FOCUS_DOWN);  
	}
	
    private void initChart() {
        mCurrentSeries = new XYSeries("Sample Data");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }

    private void addSampleData() {
        
    }
	
    class RemindTask extends TimerTask {
        @Override
        public void run() {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            float zzz = yArray.remove(0);
            yArray.add((float) (zzz + Math.random() * 5 - 2.5));
            
            mCurrentSeries.clear();
            
            for(int i = 0; i < numberOfPoint; i++)
            {
            	mCurrentSeries.add(xArray.get(i), yArray.get(i));  
            }
            
            mChart.repaint();
           
        }
    }
    
    class RemindTask2 extends TimerTask {
        @Override
        public void run() {
        	
            runOnUiThread(new Runnable() {
                public void run() {
                	updateConsole(Integer.toString(iii++));
                }
            });
        
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		scroll = (ScrollView) findViewById(R.id.scroll1);
		text = (TextView) findViewById(R.id.textView1);

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        
        if (mChart == null) {
        	
        	for(int i = 0; i < numberOfPoint; i++)
        	{
        		xArray.add(i + 1);
        		yArray.add((float) i);
        	}
            initChart();
            addSampleData();
            mChart = ChartFactory.getLineChartView(this, mDataset, mRenderer);
                    
            layout.addView(mChart, new LayoutParams
            		(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        } else {
            mChart.repaint();
        }  	
        
        Thread one = new Thread() {
            public void run() {
            	
                try {
                	
                	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                	
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    
                    if (mBluetoothAdapter == null) {
                    	
                        runOnUiThread(new Runnable() {
                            public void run() {
                            	updateConsole("NO BLUETOOTH DEVICE ON-BOARD");
                            }
                        });
                        
                        return;
                    }
                    else
                    {
                    	Log.e("STATE", "Bluetooth initiated");
                    	
                    	runOnUiThread(new Runnable() {
                            public void run() {
                            	updateConsole("Bluetooth initiated");
                            }
                        });
                        
                    	
                    	if (!mBluetoothAdapter.isEnabled())
                    	{
                    		Log.e("STATE", "Bluetooth not enabled");
                    		    	runOnUiThread(new Runnable() {
                                public void run() {
                                	updateConsole("Bluetooth not enabled");
                                }
                            });

                    		
                    	    if(!mBluetoothAdapter.isEnabled())
                    	    {
                    	        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    	        startActivity(i);
                    	    }
                    	}
                    	
                    	{

                    		Log.e("STATE", "Bluetooth enabled");
                    		runOnUiThread(new Runnable() {
                                public void run() {
                                	updateConsole("Bluetooth enabled");
                                }
                            });
                    		       		
                    	    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    	        		
                    		// If there are paired devices
                    		if (pairedDevices.size() > 0) {
                    	
                    			Log.e("STATE", "Paired device(s) found");
                    			
                    			runOnUiThread(new Runnable() {
                                    public void run() {
                                    	updateConsole("Paired device(s) found");
                                    }
                                });
                    			
                    			BluetoothDevice xDevice = null;
                    			
                    		    for (BluetoothDevice device : pairedDevices) {
                    		        
                    		        setTitle(device.getName() + " | " + device.getAddress());
                    		        xDevice = device;
                    		    }
                    		    
                    		    BluetoothSocket btSocket = null;
            					try {
            						btSocket = xDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            					} catch (IOException e) {
            						e.printStackTrace();
            					}

            					Log.e("STATE", "Socket created");
            					
                    			runOnUiThread(new Runnable() {
                                    public void run() {
                                    	updateConsole("Socket created");
                                    }
                                });
                    		    
                    		    try {
            						btSocket.connect();
            					} catch (IOException e) {
            						e.printStackTrace();
            					}
                    		    
                    		    Log.e("STATE", "Socket connected");
                    		    
                    			runOnUiThread(new Runnable() {
                                    public void run() {
                                    	updateConsole("Socket connected");
                                    }
                                });
                    		    
                    		    InputStream input = null;
            					try {
            						input = btSocket.getInputStream();
            					} catch (IOException e) {
            						e.printStackTrace();
            					}
            					
            					Log.e("STATE", "InputStream acquired");
            					
                    			runOnUiThread(new Runnable() {
                                    public void run() {
                                    	updateConsole("InputStream acquired");
                                    }
                                });
            					
            		            int read = 0;
            		            byte[] singleChar = new byte[1];
            		            
            		            
            		            do
            		            {
            		                try
            		                {
            		                	read = input.read(singleChar, 0, 1);
            		                	String data = new String(singleChar, 0, read);
            		                	
            		                	Log.e("METADATA", data);
            		                	           		                	
            		                	if(data.equals("~"))
            		                	{
            		                		xData = "";
            		                		
            		                		Log.e("XXX", "**************BEGIN****************");
            		                		
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            		                		
            			                	read = input.read(singleChar, 0, 1);
            			                	data = new String(singleChar, 0, read);
            			                	xData += data;
            			                	
            			                	xxxData = xData;
            			                	
            					            //send data to database
            					            Thread thread = new Thread()
            					            {
            					                @Override
            					                public void run() {
            					                    
            							            Log.e("MSG", "Start HTTP GET in a new thread");

            							            runOnUiThread(new Runnable() {
            		                                    public void run() {
            		                                    	updateConsole("Sending to Cloud Server...");
            		                                    }
            		                                });
            					                	
            										HttpClient client = new DefaultHttpClient();  
            										String getURL = "http://10.3.0.1/newevent.php?deviceid=6635&value=" + xxxData;
            										HttpGet get = new HttpGet(getURL);
            										try {
            											HttpResponse responseGet = client.execute(get);
            										    HttpEntity resEntityGet = responseGet.getEntity();  
            										    if (resEntityGet != null) {  
            										        String response = EntityUtils.toString(resEntityGet);
            										        Log.e("GET RESPONSE", response);
            										    }
            										} catch (ClientProtocolException e) {
            											e.printStackTrace();
            										} catch (IOException e) {
            											e.printStackTrace();
            										}
            					                }
            					            };
            					            
            					            //thread.start();
            			                	
            					            //GUI update
            			                	Log.e("DATA", xData +  "<--------------- Data"); //sample output: 01810xxxx
            			                	
            			                	/*
    							            runOnUiThread(new Runnable() {
    		                                    public void run() {
    		                                    	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    		                                    	updateConsole(xxxData + "<--------------- Data");
    		                                    }
    		                                });
    		                                */
            			                	
            			                	           			                	
            			                    yArray.remove(0);
            			                    //if(xData.length()==9) 
            			                    {
            			                    	xData = xData.substring(0, 5);
                			                    aValue = (Float.parseFloat(xData))/100.0f;
                			                    yArray.add( aValue );
            			                    }
            			                    //else
            			                    //	Log.e("XXX", "xxxxxxxxx WRONG xxxxxxxxx" );
            			                    	
            			                    
	                                    	Log.e("XXX", Float.toString(aValue ) + "<--------------- Value" );
	                                    	/*
    							            runOnUiThread(new Runnable() {
    		                                    public void run() {
    		                                    	updateConsole(Float.toString(aValue) + "<--------------- Value");

    		                                    }
    		                                });
    		                                */
            			                    
    							            Log.e("XXX", "**************END******************");
    							            
    							            
            			                    
            			                    mCurrentSeries.clear();
            			                    
            			                    for(int i = 0; i < numberOfPoint; i++)
            			                    {
            			                    	mCurrentSeries.add(xArray.get(i), yArray.get(i));  
            			                    }
            			                    
            			                    mChart.repaint();
            			                    
            			                				                	

            		                	}

            		                }
            		                catch(Exception ex)
            		                {
            		                    read = -1;
            		                }
            		            }
            		            while (read > 0);
            		         
                    		}
                    		else
                    		{
                    			Log.e("STATE", "No paired device(s)");
                    			
					            runOnUiThread(new Runnable() {
                                    public void run() {
                                    	updateConsole("No paired device(s)");
                                    }
                                });
                    		}
                    		
                    		
                    	}
                    	
                    	
                    	
                    	
                    }
                    
                	
                	

                } catch(Exception v) {
                    System.out.println(v);
                }
            }  
        };
        
        one.start();
        

       
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

