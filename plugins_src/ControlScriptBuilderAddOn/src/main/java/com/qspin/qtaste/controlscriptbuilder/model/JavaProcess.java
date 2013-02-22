package com.qspin.qtaste.controlscriptbuilder.model;



public class JavaProcess extends ControlAction {

	public JavaProcess()
	{
		super();
//		String[] params = divideParameters(pParameters);
//		boolean unamed = true;
//		for ( int i = 0; i<params.length; ++i)
//		{
//			String parameter = params[i];
//			LOGGER.trace("process parameter : " + parameter);
//			if (parameter.contains("="))
//			{
//				if ( parameter.contains("\"") || parameter.contains("'") )
//				{
//					if (parameter.indexOf('"') >= 0 && parameter.indexOf('"') < parameter.indexOf("=") )
//						unamed = false;
//					else if (parameter.indexOf('\'') >= 0 && parameter.indexOf('\'') < parameter.indexOf("=") )
//						unamed = false;
//				}
//				else
//					unamed = false;
//			}
//			LOGGER.trace("unamed parameter : " + unamed);
//			if ( unamed )
//			{
//				switch (i)
//				{
//				case 0 : //description 
//					setDescription(cleanParameter(parameter));
//					break;
//				case 1 : //mainClassOrJar 
//					setMainClassOrJar(cleanParameter(parameter));
//					break;
//				case 2 : //args
//					setArgs(cleanParameter(parameter));
//					break;
//				case 3 : //workingDir
//					setWorkingDir(cleanParameter(parameter));
//					break;
//				case 4 : //classPath
//					setClassPath(cleanParameter(parameter));
//					break;
//				case 5 : //vmArgs
//					setVmArgs(cleanParameter(parameter));
//					break;
//				case 6 : //jmxPort
//					setJmxPort(cleanParameter(parameter));
//					break;
//				case 7 : //checkAfter
//					setCheckAfter(cleanParameter(parameter));
//					break;
//				case 8 : //priority
//					setPriority(cleanParameter(parameter));
//					break;
//				case 9 : //useJacoco
//					setUseJacoco(cleanParameter(parameter));
//					break;
//				case 10 : //useJavaGui
//					setUseJavaGui(cleanParameter(parameter));
//					break;
//				default:
//					LOGGER.warn("Unknown parameter index " + i);
//				}
//			}
//			else
//			{
//				Entry<String, String> param = getKeyValueFromParameter(parameter);
//				if ( param.getKey().equals(ARGS) )
//					setArgs(param.getValue());
//				else if ( param.getKey().equals(MAIN_CLASS_OR_JAR) )
//					setMainClassOrJar(param.getValue());
//				else if ( param.getKey().equals(CLASSPATH) )
//					setClassPath(param.getValue());
//				else if ( param.getKey().equals(VM_ARGS) )
//					setVmArgs(param.getValue());
//				else if ( param.getKey().equals(JMX_PORT) )
//					setJmxPort(param.getValue());
//				else if ( param.getKey().equals(PRIORITY) )
//					setPriority(param.getValue());
//				else if ( param.getKey().equals(USE_JACOCO) )
//					setUseJacoco(param.getValue());
//				else if ( param.getKey().equals(USE_JAVAGUI) )
//					setUseJavaGui(param.getValue());
//				else if ( param.getKey().equals(WORKING_DIR) )
//					setWorkingDir(param.getValue());
//				else if ( param.getKey().equals(CHECK_AFTER) )
//					setCheckAfter(param.getValue());
//				else
//				{
//					LOGGER.error("Unknown parameter name : " + param.getKey());
//				}
//			}
//		}
	}
	
	@Override
	public ControlActionType getType() {
		return ControlActionType.JAVA_PROCESS;
	}

	public String getMainClassOrJar() {
		return mMainClassOrJar;
	}

	public void setMainClassOrJar(String pMainClassOrJar) {
		this.mMainClassOrJar = pMainClassOrJar;
	}

	public String getArgs() {
		return mArgs;
	}

	public void setArgs(String pArgs) {
		this.mArgs = pArgs;
	}

	public String getWorkingDir() {
		return mWorkingDir;
	}

	public void setWorkingDir(String pWorkingDir) {
		this.mWorkingDir = pWorkingDir;
	}

	public String getClassPath() {
		return mClassPath;
	}

	public void setClassPath(String pClassPath) {
		this.mClassPath = pClassPath;
	}

	public String getVmArgs() {
		return mVmArgs;
	}

	public void setVmArgs(String pVmArgs) {
		this.mVmArgs = pVmArgs;
	}

	public int getmJmxPort() {
		return mJmxPort;
	}

	public void setJmxPort(int pJmxPort) {
		this.mJmxPort = pJmxPort;
	}

	public void setJmxPort(String pJmxPort) {
		setJmxPort(Integer.parseInt(pJmxPort));
	}

	public int getCheckAfter() {
		return mCheckAfter;
	}

	public void setCheckAfter(int pCheckAfter) {
		this.mCheckAfter = pCheckAfter;
	}

	public void setCheckAfter(String pCheckAfter) {
		setCheckAfter(Integer.parseInt(pCheckAfter));
	}

	public String getPriority() {
		return mPriority;
	}

	public void setPriority(String pPriority) {
		this.mPriority = pPriority;
	}

	public boolean isUseJacoco() {
		return mUseJacoco;
	}

	public void setUseJacoco(boolean pUseJacoco) {
		this.mUseJacoco = pUseJacoco;
	}

	public void setUseJacoco(String pUseJacoco) {
		setUseJacoco(Boolean.parseBoolean(pUseJacoco));
	}

	public boolean isUseJavaGui() {
		return mUseJavaGui;
	}

	public void setUseJavaGui(boolean pUseJavaGui) {
		this.mUseJavaGui = pUseJavaGui;
	}

	public void setUseJavaGui(String pUseJavaGui) {
		setUseJavaGui(Boolean.parseBoolean(pUseJavaGui));
	}

	protected String mMainClassOrJar;
	protected String mArgs;
	protected String mWorkingDir;
	protected String mClassPath;
	protected String mVmArgs;
	protected int mJmxPort = -1;
	protected int mCheckAfter = -1;
	protected String mPriority;
	protected boolean mUseJacoco = false;
	protected boolean mUseJavaGui = false;

	protected static final String MAIN_CLASS_OR_JAR = "mainClassOrJar";
	protected static final String ARGS = "args";
	protected static final String WORKING_DIR = "workingDir";
	protected static final String CLASSPATH = "classPath";
	protected static final String VM_ARGS = "vmArgs";
	protected static final String JMX_PORT = "jmxPort";
	protected static final String CHECK_AFTER = "checkAfter";
	protected static final String PRIORITY = "priority";
	protected static final String USE_JACOCO = "useJacoco";
	protected static final String USE_JAVAGUI = "useJavaGui";
}
