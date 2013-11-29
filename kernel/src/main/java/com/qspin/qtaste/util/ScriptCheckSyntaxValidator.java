/*
    Copyright 2007-2009 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import com.qspin.qtaste.testsuite.impl.JythonTestScript;

public class ScriptCheckSyntaxValidator {

    //static private Logger logger = Log4jLoggerFactory.getLogger(ScriptCheckSyntaxValidator.class);
    private String mScriptContent;
    private String mFileName;
    private static Pattern mDoStepPattern = Pattern.compile("doStep\\(\\s*['\"]?([\\w.]+)['\"]?(?:\\s*,\\s*([\\w.]+))?\\s*\\)");
    //private static Pattern mDoStepsPattern = Pattern.compile("doSteps\\(\\s*([\\w.]+)(?:\\s*,\\s*['\"]\\s*\\[(\\w*)(?:\\s*-\\s*(\\w*))?\\]\\s*['\"]\\s*)?\\s*\\)");
	private static Pattern mStepsTableDefPattern = Pattern.compile("(\\w+)\\s*=\\s*[\\(\\[]\\s*(\\(\\s*['\"]?\\w+['\"]?\\s*,\\s*\\w+\\s*\\)(?:\\s*,\\s*\\(\\s*['\"]?\\w+['\"]?\\s*,\\s*\\w+\\s*\\))*)\\s*[\\)\\]]");
    
    public ScriptCheckSyntaxValidator(String fileName, String scriptContent) {
        mScriptContent = scriptContent;
        mFileName = fileName;
    }
    
    public boolean check() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("python");
        Compilable compilable = (Compilable) engine;

        try {
			compilable.compile(mScriptContent);
	        checkStepsDescriptions();
	        return true;
		} catch (ScriptException e) {
            JOptionPane.showMessageDialog(null, "Syntax error in file '" + mFileName + "':\n" + e.getMessage());
            return false;
		} catch (FunctionDocumentationException e) {
            JOptionPane.showMessageDialog(null, "Step documentation error in file '" + mFileName + "':\n" + e.getMessage());
            return false;
		}
    }
    
    private void checkStepsDescriptions() throws FunctionDocumentationException {
    	for (String stepFunctionName: getStepsFunctionsNames()) {
    		try {
				String stepFunctionDoc = getFunctionDoc(stepFunctionName);
				HashMap<String, String> stepDocTags = JythonTestScript.parsePythonDoc(stepFunctionDoc);
	            String stepDescription = stepDocTags.get("step");
	            if (stepDescription == null) {
	            	throw new FunctionDocumentationException("Function " + stepFunctionName + " documentation contains no @step tag");
	            } else if (stepDescription.length() == 0) {
	            	throw new FunctionDocumentationException("Function " + stepFunctionName + " documentation contains no description for the @step tag");	            	
	            }
			} catch (FunctionDefinitionNotFoundException e) {
				// do nothing because we suppose step definition is imported
			}
    	}
    }
    
    private List<String> getStepsFunctionsNames() {
    	List<String> stepsFunctionsNames = new ArrayList<String>(); 

    	Matcher doStepMatcher = mDoStepPattern.matcher(mScriptContent);
    	while (doStepMatcher.find()) {
    		String stepFunctionName = (doStepMatcher.group(2) != null ? doStepMatcher.group(2) : doStepMatcher.group(1));
    		stepsFunctionsNames.add(stepFunctionName);
    	}
    	
    	Matcher stepsTableDefMatcher = mStepsTableDefPattern.matcher(mScriptContent);
    	while (stepsTableDefMatcher.find()) {
    		String[] stepFunctionNamesAndIds = stepsTableDefMatcher.group(2).split("\\W+");
    		for (int i=2; i < stepFunctionNamesAndIds.length; i+=2) {
    			stepsFunctionsNames.add(stepFunctionNamesAndIds[i]);
    		}
    	}
    	
    	return stepsFunctionsNames;
    }
    
    private String getFunctionDoc(String functionName) throws FunctionDefinitionNotFoundException, FunctionDocumentationException {
    	Pattern functionDefinitionPattern = Pattern.compile("^def\\s+" + functionName + "\\(\\)\\s*:\\s*$", Pattern.MULTILINE);
    	Matcher functionDefinitionMatcher = functionDefinitionPattern.matcher(mScriptContent);
    	if (functionDefinitionMatcher.find()) {
    		Pattern notInFunctionAnymorePattern = Pattern.compile("^\\S", Pattern.MULTILINE);
    		Matcher notInFunctionAnymoreMatcher = notInFunctionAnymorePattern.matcher(mScriptContent);
    		String functionDefinition;
    		if (notInFunctionAnymoreMatcher.find(functionDefinitionMatcher.end())) {
    			functionDefinition = mScriptContent.substring(functionDefinitionMatcher.end() + 1, notInFunctionAnymoreMatcher.start() - 1);
    		} else {
    			functionDefinition = mScriptContent.substring(functionDefinitionMatcher.end() + 1);
    		}
    		
    		Pattern docStringPattern = Pattern.compile("^\\s*('''|\"\"\")(.*)\\1", Pattern.DOTALL);
    		Matcher docStringMatcher = docStringPattern.matcher(functionDefinition);
    		if (docStringMatcher.find()) {
    			return docStringMatcher.group(2);
    		} else {
    			throw new FunctionDocumentationException("Function " + functionName + " is not documented by a docstring");
    		}
    	} else {
    		throw new FunctionDefinitionNotFoundException("Definition of function " + functionName + " not found");
    	}
    }    
    
    @SuppressWarnings("serial")
	private class FunctionDefinitionNotFoundException extends Exception {
    	public FunctionDefinitionNotFoundException(String message) {
    		super(message);
    	}
    }
    
    @SuppressWarnings("serial")
	private class FunctionDocumentationException extends Exception {
    	public FunctionDocumentationException(String message) {
    		super(message);
    	}
    }
}
