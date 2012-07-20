package com.deepshiftlabs.nerrvana;

import hudson.model.AbstractBuild;
import java.io.File;
import java.io.FileInputStream;
import org.w3c.dom.*;

/**
 * <p>Wraps information about {@link <a href="https://nerrvana.com/docs/test-runs">Nerrvana test run</a>}</p>
 * This class expects following XML returned by Nerrvana API call:
 * <pre>
 * {@code
 * <testruns>
 *   <testrun>
 *     <id>1181</id>
 *     <space_id>676</space_id>
 *     <name><![CDATA[First_created_by_API]]></name>
 *     <description><![CDATA[Bla bla bla]]></description>
 *     <executable_file><![CDATA[rebuildAndRun.sh]]></executable_file>
 *     <contexts>
 *       <context>
 *         <code>winxp_sp3_firefox_3</code>
 *         <name>Firefox 3/WinXP SP3</name>
 *       <context>
 *     </contexts>
 *     <start_date>2012-02-21</start_date>
 *     <week_days>Monday, Tuesday</week_days>
 *     <start_time>20:45</start_time>
 *     <period_type>hr</period_type>
 *     <periodicity>10</periodicity>
 *     <on_pause>1</on_pause>
 *     <validation>1</validation>
 *     <ftp_name>First_created_by_API</ftp_name>
 *    
 *     <next_executions>
 *       <next_execution>
 *         <id>1767812</id>
 *         <start_datetime>2012-02-21 20:46:19</start_datetime>
 *       </next_execution>
 *     </next_executions>
 *   </testrun>
 * </testruns>
 * }
 * </pre>
 * 
 * @author <a href="http://www.deepshiftlabs.com/">Deep Shift Labs</a>
 * @author <a href="mailto:wise@deepshiftlabs.com">Victor Orlov</a>
 * @version 1.00
 */
public class Testrun{
  public String id;
  public String exec_id;
  public String name;
  public String description;
  
  public Testrun(){}

  public String getId(){return id;}
  public String getExecid(){return exec_id;}
  public String getName(){return name;}
  public String getDescription(){return description;}
  
  public static String assembleName(String test_run_name,AbstractBuild<?,?> build){
    File froot = build.getRootDir();
    File frev = new File(froot,"revision.txt");
    String revision = null;
    if(frev.exists()){
      try{
        FileInputStream in = new FileInputStream(frev);
        byte[] ab = new byte[(int)frev.length()];
        in.read(ab);
        String s = new String(ab);
        revision = s.substring(s.lastIndexOf(File.separatorChar)+1).trim();
      }
      catch(Exception e){
        Logger.infoln("Cannot determine current revision number");
      }
    }
    if(test_run_name == null || test_run_name.length() == 0)
      test_run_name = build.getProject().getDisplayName();
    return test_run_name+" build #"+build.getNumber()+"[rev."+revision+"]";
  }

  
  
  public static Testrun getTestrun(Node parent) throws Exception{
    Testrun testrun = new Testrun();
    Node root = Utils.getChildNode(parent,"testrun");
    Node node = Utils.getChildNode(root,"id");
    testrun.id = Utils.nodeValue(node);
    node = Utils.getChildNode(root,"name");
    testrun.name = Utils.nodeValue(node);
    node = Utils.getChildNode(root,"description");
    testrun.description = Utils.nodeValue(node);
    
    node = Utils.getChildNode(root,"next_executions");
    node = Utils.getChildNode(node,"next_execution");
    node = Utils.getChildNode(node,"id");
    testrun.exec_id = Utils.nodeValue(node);
    return testrun;
  }
}