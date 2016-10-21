package com.capanicus.billing;

import java.io.PrintStream;
import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.AsteriskQueueEntry;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.AsteriskServerListener;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.asteriskjava.live.MeetMeUser;
import org.asteriskjava.live.internal.AsteriskAgentImpl;

public class LiveCallsMonitor
  implements AsteriskServerListener
{
  private AsteriskServer asteriskServer;
  
  public LiveCallsMonitor()
  {
    this.asteriskServer = new DefaultAsteriskServer("localhost", "astmgr01", "FWRWFN0P2H4K2H1UL41I");
  }
  // apache@96.31.89.116 -p 25456 password: passforupwork
  public void run()
    throws Exception
  {
    this.asteriskServer.addAsteriskServerListener(this);
    for (;;)
    {
      Object localObject1 = null;
      try
      {
        Thread.currentThread();Thread.sleep(2000L);
      }
      catch (Exception localException)
      {
        localException = 
          localException;localException.printStackTrace();
      }
      finally {}
    }
  }
  
  public void onNewAsteriskChannel(AsteriskChannel paramAsteriskChannel)
  {
    try
    {
      String str1 = paramAsteriskChannel.getVariable("leg1_callbillingID");
      String str2 = paramAsteriskChannel.getVariable("leg2_callbillingID");
      
      System.out.println("CHANNEL ID: " + paramAsteriskChannel.getId() + " => leg1_callbillingID => " + str1);
      System.out.println("CHANNEL ID: " + paramAsteriskChannel.getId() + " => leg2_callbillingID => " + str2);
      if ((str2 != null) && (Integer.valueOf(str2).intValue() > 0))
      {
        System.out.println("CHANNEL ID: " + paramAsteriskChannel.getId() + " LEG2 CHANNEL TO BE BILLED");
        BillingEngine localBillingEngine = new BillingEngine(paramAsteriskChannel, Integer.valueOf(str2).intValue());
        localBillingEngine.start();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void onNewMeetMeUser(MeetMeUser paramMeetMeUser) {}
  
  public void onNewAgent(AsteriskAgentImpl paramAsteriskAgentImpl) {}
  
  public void onNewQueueEntry(AsteriskQueueEntry paramAsteriskQueueEntry) {}
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    LiveCallsMonitor localLiveCallsMonitor = new LiveCallsMonitor();
    localLiveCallsMonitor.run();
  }
}
