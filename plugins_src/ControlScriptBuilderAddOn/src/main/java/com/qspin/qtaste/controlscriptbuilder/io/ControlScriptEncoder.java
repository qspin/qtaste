package com.qspin.qtaste.controlscriptbuilder.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.qspin.qtaste.controlscriptbuilder.model.ControlAction;
import com.qspin.qtaste.util.FileUtilities;

public final class ControlScriptEncoder {

	public static void updateAndSaveControlActions(List<ControlAction> pActions)
	{
		ControlScriptEncoder encoder = new ControlScriptEncoder(pActions);
		encoder.updateControlScripts();
	}
	
	private ControlScriptEncoder(List<ControlAction> pActions)
	{
		mActions = pActions;
		mSortActions = new HashMap<String, List<ControlAction>>();
		mContainedControlActions = new ArrayList<String>();
		sortControlAction();
	}
	
	private void updateControlScripts()
	{
		for (String controlScriptName : mSortActions.keySet())
		{
			File original = new File(controlScriptName);
			File backup = new File(original.getParentFile(), original.getName() + ".bak");

			StringBuilder fileContent = new StringBuilder();
			InputStream is = null;
			OutputStream os = null;
			InputStreamReader isr = null;
			OutputStreamWriter osw = null;
			BufferedReader reader = null;
			BufferedWriter writer = null;
			try {
				//create a backup
				FileUtilities.copy(original, backup);
				is = new FileInputStream(original);
				isr = new InputStreamReader(is);
				reader = new BufferedReader(isr);
				//read the controlScriptFile and find a controlAction
				String line;
				mInControlAction = false;
				while ( (line = reader.readLine()) != null)
				{
					if ( line.trim().startsWith("#"))
					{
						fileContent.append(line + LS); 
						continue;
					}
					if ( line.trim().isEmpty() )
					{
						fileContent.append(LS); 
						continue;	
					}
					boolean fileContentChange = false;
					while ( !line.isEmpty() )
					{
						if ( mInControlAction )
						{
							line = processLineInControlAction(line, fileContent);
							if (!mInControlAction)
							{
								//find the concerned controlAction
								ControlAction ca = null;
								for ( ControlAction c : mSortActions.get(controlScriptName) )
								{
									if (mCurrentControlAction.toString().contains(c.getDescription()))
									{
										fileContent.append(c.getScriptCode());
										fileContentChange = true;
										break;
									}
								}
								if ( ca == null )
								{
									LOGGER.error("Cannot find the control action associated with : " + mCurrentControlAction);
									continue;
								}
							}
						}
						else
						{
							line = processLineOutControlAction(line, fileContent);
							fileContentChange = true;
						}
					}
					if (fileContentChange)
					{
						fileContent.append(LS);
					}
				}
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(isr);
				IOUtils.closeQuietly(is);
				os = new FileOutputStream(original);
				osw = new OutputStreamWriter(os);
				writer = new BufferedWriter(osw);
				writer.write(fileContent.toString());
				backup.delete();
				backup = null;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(isr);
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(writer);
				IOUtils.closeQuietly(osw);
				IOUtils.closeQuietly(os);
				if (backup != null)
					backup.renameTo(original);
			}
		}
	}
	
	private String processLineInControlAction(String pLine, StringBuilder pOutput)
	{
		for ( int i=0; i<pLine.length() && mInControlAction ; ++i)
		{
			mCurrentControlAction.append(pLine.charAt(i));
			if ( pLine.charAt(i) == '(' )
			{
				mParCounter ++;
			}
			else if ( pLine.charAt(i) == ')' )
			{
				mParCounter --;
				if ( mParCounter == 0 )
				{
					mInControlAction = false;
					return pLine.substring(i+1);
				}
			}
		}
		return "";
	}
	
	private String processLineOutControlAction(String pLine, StringBuilder pOutput)
	{
		String containControlAction = null;
		for ( String controlActionType : mContainedControlActions )
		{
			if (pLine.contains(controlActionType))
			{
				if (containControlAction != null)
				{
					if (pLine.indexOf(containControlAction) < pLine.indexOf(controlActionType))
					{
						containControlAction = controlActionType;										
					}
				}
				else
					containControlAction = controlActionType;
			}
		}
		if ( containControlAction != null )
		{
			mInControlAction = true;
			mCurrentControlAction = new StringBuilder();
			pOutput.append(pLine.substring(0, pLine.indexOf(containControlAction)));
			return pLine.substring(pLine.indexOf(containControlAction)+containControlAction.length()).trim();
		}
		else
		{
			pOutput.append(pLine);
			return "";
		}
	}
	
	private void sortControlAction()
	{
		for ( ControlAction ca : mActions )
		{
			String script = ca.getControlScript();
			if ( !mSortActions.containsKey(script) )
			{
				mSortActions.put(script, new ArrayList<ControlAction>());
			}
			mSortActions.get(script).add(ca);

			if (!mContainedControlActions.contains(ca.getRawType()))
			{
				mContainedControlActions.add(ca.getRawType());
			}
		}
	}
	
	private List<ControlAction> mActions;
	private Map<String, List<ControlAction>> mSortActions;
	private List<String> mContainedControlActions;
	private boolean mInControlAction;
	private StringBuilder mCurrentControlAction;
	private int mParCounter;
	
	private static final String LS = System.getProperty("line.separator");
	private static final Logger LOGGER = Logger.getLogger(ControlScriptEncoder.class);
}
