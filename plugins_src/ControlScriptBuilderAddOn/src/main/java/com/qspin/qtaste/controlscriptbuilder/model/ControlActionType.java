package com.qspin.qtaste.controlscriptbuilder.model;

public enum ControlActionType {

	JAVA_PROCESS
	{
		public String toString()
		{
			return "Java Process";
		}

		@Override
		public String getPythonName() {
			return "JavaProcess";
		}
	},
	NATIVE_PROCESS
	{
		public String toString()
		{
			return "Native Process";
		}

		@Override
		public String getPythonName() {
			return "NativeProcess";
		}
	},
	UNKNOWN_PROCESS
	{
		public String toString()
		{
			return "Unknown Process";
		}

		@Override
		public String getPythonName() {
			return "";
		}
	};
	
	public abstract String getPythonName();
	
	public static ControlActionType getControlActionType(String pPythonName)
	{
		for ( ControlActionType cat : values() )
		{
			if ( cat == UNKNOWN_PROCESS )
				continue;

			if (cat.getPythonName().equals(pPythonName))
				return cat;
		}
		return UNKNOWN_PROCESS;
	}
}
