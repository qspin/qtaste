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

package com.qspin.qtaste.testsuite.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.python.core.Py;
import org.python.core.PyArray;
import org.python.core.PyClass;
import org.python.core.PyDictionary;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyInstance;
import org.python.core.PyInteger;
import org.python.core.PyJavaInstance;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PySyntaxError;
import org.python.core.PySystemState;
import org.python.core.PyTuple;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.debug.Breakpoint;
import com.qspin.qtaste.debug.BreakpointEventHandler;
import com.qspin.qtaste.debug.BreakpointManager;
import com.qspin.qtaste.event.DumpPythonResultEventHandler;
import com.qspin.qtaste.event.TestScriptBreakpointEvent;
import com.qspin.qtaste.event.TestScriptBreakpointHandler;
import com.qspin.qtaste.event.TestScriptBreakpointListener;
import com.qspin.qtaste.kernel.engine.TestEngine;
import com.qspin.qtaste.kernel.testapi.Component;
import com.qspin.qtaste.kernel.testapi.ComponentsLoader;
import com.qspin.qtaste.kernel.testapi.TestAPI;
import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.lang.DoubleWithPrecision;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.reporter.testresults.TestResult.Status;
import com.qspin.qtaste.testsuite.Executable;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.QTasteException;
import com.qspin.qtaste.testsuite.QTasteTestFailException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.TestDataSet;
import com.qspin.qtaste.testsuite.TestRequirement;
import com.qspin.qtaste.testsuite.TestScript;
import com.qspin.qtaste.testsuite.TestSuite;
import com.qspin.qtaste.ui.debug.DebugVariable;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.Strings;
import com.qspin.qtaste.util.versioncontrol.VersionControl;

/**
 *
 * @author David Ergo
 */
public class JythonTestScript extends TestScript implements Executable {

    private static Logger logger = Log4jLoggerFactory.getLogger(JythonTestScript.class);
    private static Logger scriptLogger = Logger.getLogger("TestScript");
    private File fileName;
    private ScriptTestData scriptTestData;
    private ScriptBreakpoint scriptBreakpoint;
    private TestData testData;
    public TestResult testResult;
    private Bindings bindings;
    private static ScriptEngineManager engineManager = new ScriptEngineManager();
    private static ScriptEngine engine = engineManager.getEngineByName("python");
    private static List<String> platform;
    private static Bindings globalBindings;
    private static String scriptDebuggerClassCode;
    private TestScriptBreakpointHandler testScriptBreakPointEventHandler = TestScriptBreakpointHandler.getInstance();
    private BreakpointEventHandler breakPointEventHandler = BreakpointEventHandler.getInstance();
    private DumpPythonResultEventHandler pythonResultEventHandler = DumpPythonResultEventHandler.getInstance();

    static {
        initializeEmbeddedJython();
        TestBedConfiguration.registerConfigurationChangeHandler(new TestBedConfiguration.ConfigurationChangeHandler() {

            public void onConfigurationChange() {
                List<String> newPlatform;
                TestBedConfiguration testbedConfig = TestBedConfiguration.getInstance();
                if (testbedConfig != null) {
                    newPlatform = testbedConfig.getList("testapi_implementation.import");
                } else {
                    newPlatform = null;
                }
                if (newPlatform != null && !newPlatform.equals(platform)) {
                    initializeEmbeddedJython();
                }
            }
        });
    }

    public static ScriptEngine getEngine() {
        return engine;
    }

    public static Logger getLogger() {
        return logger;
    }

    private static void initializeEmbeddedJython() {
        TestBedConfiguration testbedConfig = TestBedConfiguration.getInstance();
        if (testbedConfig != null) {
            platform = testbedConfig.getList("testapi_implementation.import");
        } else {
            platform = null;
        }

        // to force loading of components if not loaded
        ComponentsLoader.getInstance();

        // dynamically create VerbsTestAPI class with verbs methods
        TestAPI testAPI = TestAPIImpl.getInstance();
        Collection<String> registeredComponents = testAPI.getRegisteredComponents();

        if (engine != null) {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            if (bindings != null) {
                bindings.clear();
            }
        }

        globalBindings = engine.createBindings();
        globalBindings.put(ScriptEngine.FILENAME, "embedded_jython");
        globalBindings.put("logger", scriptLogger);
        globalBindings.put("Status", ScriptTestResultStatus.class);
        try {
            // Declare __TestAPIWrapper class, of which the testAPI variable will be an instance
            String code =
                    "import sys as __sys\n" +
                    "from sets import Set as __Set\n" +
                    "from com.qspin.qtaste.testsuite import QTasteException, QTasteTestFailException, QTasteDataException\n" +
                    "import com.qspin.qtaste.testsuite.impl.JythonTestScript.ScriptTestResultStatus as Status\n" +
                    "class ComponentNotPresentException(Exception):\n" +
                    "    pass\n" +
                    "class StepsException(Exception):\n" +
                    "    pass\n" +
                    "class ImportTestScriptException(Exception):\n" +
                    "    pass\n" +
                    "class __TestAPIWrapper:\n" +
                    "    def __init__(self, testScript):\n" +
                    "        self.testScript = testScript\n";

            code +=
                    //   new-style test api - direct method call
                    "    def __invoke(self, method, arguments):\n" +
                    "        self.testScript.logInvoke(method.im_self, method.__name__, str(arguments)[1:-1-(len(arguments)==1)])\n" +
                    "        try:\n" +
                    "            return method(*arguments)\n" +
                    "        except TypeError, e:\n" +
                    "            raise QTasteDataException('Invalid argument(s): ' + str(e))\n" +
                    "    def stopTest(self, status, message):\n" +
                    "        if status == Status.FAIL:\n" +
                    "            raise QTasteTestFailException(message)\n" +
                    "        elif status == Status.NOT_AVAILABLE:\n" +
                    "            raise QTasteDataException(message)\n" +
                    "        else:\n" +
                    "            raise SyntaxError('Invalid status argument')\n";

            // add get<Component>() methods to the __TestAPIWrapper class
            for (String component : registeredComponents) {
                code += "    def get" + component + "(self, **keywords):\n" +
                        "        component = self.testScript.getComponent('" + component + "', keywords)\n" +
                        "        return __TestAPIWrapper." + component + "Wrapper(self, component)\n";

                // declare the <Component>Wrapper class, of which the objects returned
                // by get<Component>() methods will be instances
                code += "    class " + component + "Wrapper:\n" +
                        "        def __init__(self, testAPI, component):\n" +
                        "            self.testAPI = testAPI\n" +
                        "            self.component = component\n" +
                        "        def __nonzero__(self):\n" +
                        "            return self.component\n";

                // add verbs methods to the ComponentWrapper class
                Collection<String> verbs = testAPI.getRegisteredVerbs(component);
                for (String verb : verbs) {
                    code += "        def " + verb + "(self, *arguments, **keywords):\n" +
                            "            if self.component:\n";

                    code += "                return self.testAPI._TestAPIWrapper__invoke(self.component." + verb + ", arguments)\n";

                    code +=
                            "            else:\n" +
                            "                raise ComponentNotPresentException('Component " + component + " is not present in testbed')\n";
                }
            }
            engine.eval(code, globalBindings);
        } catch (ScriptException e) {
            logger.fatal("Couldn't create __TestAPIWrapper Python class", e);
            TestEngine.shutdown();
            System.exit(1);
        }

        try {
            String code =
                    "import os as __os\n" +
                    "from com.sun.script.jython import JythonScope as __JythonScope\n" +
                    "from com.qspin.qtaste.testsuite.impl import JythonTestScript as __JythonTestScript\n" +
                    "__isInTestScriptImport = 0\n" +
                    "def isInTestScriptImport():\n" +
                    "    return __isInTestScriptImport != 0\n" +
                    "def importTestScript(testCasePath):\n" +
                    "    global __isInTestScriptImport\n" +
                    "    wasInTestScriptImport = __isInTestScriptImport\n" +
                    "    __isInTestScriptImport = __isInTestScriptImport + 1\n" +
                    "    try:\n" +
                    "        import sys as __sys\n" +
                    "        testCaseName = __os.path.basename(testCasePath)\n" +
                    "        basePath = __os.path.dirname(__sys._getframe(1).f_code.co_filename) + __os.sep + '..'\n" +
                    "        testCasePath = __os.path.realpath(__os.path.join(basePath, testCasePath))\n" +
                    "        __sys.path.insert(0, testCasePath)\n" +
                    "        try:\n" +
                    "            if 'TestScript' in __sys.modules:\n" +
                    "                del __sys.modules['TestScript']\n" +
                    "            try:\n" +
                    "                import TestScript\n" +
                    "            except ImportError:\n" +
                    "                raise ImportError('No test script found in ' + testCasePath)\n" +
                    "            except:\n" +
                    "                raise ImportTestScriptException('Error while importing test script ' + testCasePath)\n" +
                    "            if wasInTestScriptImport:\n" +
                    "                # test script is imported\n" +
                    "                __sys._getframe(1).f_globals[testCaseName] = TestScript\n" +
                    "            else:\n" +
                    "                # test script is not imported\n" +
                    "                __JythonTestScript.addToGlobalJythonScope(testCaseName, TestScript)\n" +
                    "            del __sys.modules['TestScript']\n" +
                    "            del TestScript\n" +
                    "        finally:\n" +
                    "            __sys.path.pop(0)\n" +
                    "    finally:\n" +
                    "        __isInTestScriptImport = __isInTestScriptImport - 1\n";
            engine.eval(code, globalBindings);
        } catch (ScriptException e) {
            logger.fatal("Couldn't create importTestScript or isInTestScriptImport Python function", e);
            TestEngine.shutdown();
            System.exit(1);
        }

        try {
            String code =
                    "import time as __time, sys as __sys\n" +
                    "from java.lang import ThreadDeath as __ThreadDeath\n" +
                    "from java.lang.reflect import UndeclaredThrowableException as __UndeclaredThrowableException\n" +
                    "from com.sun.script.jython import JythonScope as __JythonScope\n" +
                    "from com.qspin.qtaste.testsuite import QTasteTestFailException\n" +
                    "import com.qspin.qtaste.reporter.testresults.TestResult.Status as __TestResultStatus\n" +
                    "def doStep(idOrFunc, func=None):\n" +
                    "    if __isInTestScriptImport:\n" +
                    "        # test script is imported, so don't execute step\n" +
                    "        return\n" +
                    "    doStep.countStack = getattr(doStep, 'countStack', [0])\n" +
                    "    doStep.stepIdStack = getattr(doStep, 'stepIdStack', [])\n" +
                    "    doStep.stepNameStack = getattr(doStep, 'stepNameStack', [])\n" +
                    "    doStep.countStack[-1] = doStep.countStack[-1] + 1\n" +
                    "    if func is None:\n" +
                    "        # idOrFunc is the step function no id is given\n" +
                    "        id = str(doStep.countStack[-1])\n" +
                    "        func = idOrFunc\n" +
                    "    else:\n" +
                    "        # idOrFunc is the step id, args[0] is the step function\n" +
                    "        id = str(idOrFunc)\n" +
                    "    doStep.stepIdStack.append(id)\n" +
                    "    doStep.stepNameStack.append(func.func_name)\n" +
                    "    doStep.countStack.append(0)\n" +
                    "    doStep.stepId = stepId = '.'.join(doStep.stepIdStack)\n" +
                    "    stepName = '.'.join(doStep.stepNameStack)\n" +
                    "    stepDoc = func.func_doc\n" +
                    "    __JythonTestScript.getLogger().info('Begin of step %s (%s)' % (stepId, stepName))\n" +
                    "    status = __TestResultStatus.SUCCESS\n" +
                    "    begin_time = __time.clock()\n" +
                    "    try:\n" +
                    "        try:\n" +
                    "            testScript.addStepResult(stepId, __TestResultStatus.RUNNING, stepName, stepDoc, 0)\n" +
                    "            func()\n" +
                    "        except (QTasteTestFailException, __ThreadDeath, __UndeclaredThrowableException):\n" +
                    "            status = __TestResultStatus.FAIL\n" +
                    "            raise\n" +
                    "        except:\n" +
                    "            status = __TestResultStatus.NOT_AVAILABLE\n" +
                    "            raise\n" +
                    "    finally:\n" +
                    "        end_time = __time.clock()\n" +
                    "        elapsed_time = end_time - begin_time\n" +
                    "        __JythonTestScript.getLogger().info('End of step %s (%s) - status: %s - elapsed time: %.3f seconds' % (stepId, stepName, status, elapsed_time))\n" +
                    "        testScript.addStepResult(stepId, status, stepName, stepDoc, elapsed_time)\n" +
                    "        doStep.countStack.pop()\n" +
                    "        doStep.stepIdStack.pop()\n" +
                    "        doStep.stepNameStack.pop()\n";
            engine.eval(code, globalBindings);
        } catch (ScriptException e) {
            logger.fatal("Couldn't create doStep Python function", e);
            TestEngine.shutdown();
            System.exit(1);
        }

        try {
            String code =
                    "import sys as __sys\n" +
                    "from com.sun.script.jython import JythonScope as __JythonScope\n" +
                    "def __findStepIndex(table, id):\n" +
                    "   for index in range(len(table)):\n" +
                    "       stepId = str(table[index][0])\n" +
                    "       if stepId == id:\n" +
                    "           return index\n" +
                    "   raise StepsException('Step %s does not exist in steps table' % id)\n" +
                    "def doSteps(table, selector = None):\n" +
                    "    if __isInTestScriptImport:\n" +
                    "        # test script is imported, so don't execute steps\n" +
                    "        return\n" +
                    "    # check that steps id are correct identifiers\n" +
                    "    import re\n" +
                    "    idPattern = re.compile(r'^\\w+$')\n" +
                    "    for index in range(len(table)):\n" +
                    "       stepId = str(table[index][0])\n" +
                    "       if not idPattern.match(stepId):\n" +
                    "           raise StepsException('Step id %s is not a valid identifier' % stepId)\n" +
                    "    if selector is None:\n" +
                    "        for (stepId, stepName) in table:\n" +
                    "            doStep(stepId, stepName)\n" +
                    "    else:\n" +
                    "        match = re.match(r'^\\[\\s*(\\w*)(?:\\s*-\\s*(\\w*))?\\s*\\]$', selector)\n" +
                    "        if match:\n" +
                    "            startId = match.group(1)\n" +
                    "            endId = match.group(match.lastindex)\n" +
                    "        else:\n" +
                    "            raise StepsException(arg + ' is not a valid format of selector, should be [id], [id1-id2], [id1-] or [-id2]')\n" +
                    "        if startId == '':\n" +
                    "            startIndex = 0\n" +
                    "        else:" +
                    "            startIndex = __findStepIndex(table, startId)\n" +
                    "        if endId == '':\n" +
                    "            endIndex = len(table)-1\n" +
                    "        else:" +
                    "            endIndex = __findStepIndex(table, endId)\n" +
                    "        if endIndex < startIndex:\n" +
                    "            raise StepsException('Step %s should occur after step %s in steps table' % (startId, endId))\n" +
                    "        for (stepId, stepName) in table[startIndex:endIndex+1]:\n" +
                    "            doStep(stepId, stepName)\n";
            engine.eval(code, globalBindings);
        } catch (ScriptException e) {
            logger.fatal("Couldn't create doSteps Python function", e);
            TestEngine.shutdown();
            System.exit(1);
        }

        // set code to declare __ScriptDebugger class
        // note: it doesn't work if it is evaluated in the global bindings
        scriptDebuggerClassCode =
                "import bdb as __bdb\n" +
                "class __ScriptDebugger(__bdb.Bdb):\n" +
                "  def user_line(self, frame):\n" +
                "    if self.run:\n" +
                "      self.run = 0\n" +
                "      self.set_continue()\n" +
                "    else:\n" +
                "      # arrived at breakpoint\n" +
                "      lineNumber = frame.f_lineno\n" +
                "      fileName = frame.f_code.co_filename\n" +
                "      action = __scriptBreakpoint.breakScript(fileName, lineNumber, frame.f_locals)\n" +
                "      if action == 0:\n" +
                "        testAPI.stopTest(Status.NOT_AVAILABLE, 'Script has been stopped by user')\n" +
                "      elif action == 1:\n" +
                "        self.set_next(frame)\n" +
                "      elif action == 3:\n" +
                "        self.set_step()\n" +
                "      elif action == 2:\n" +
                "        self.set_continue()\n";
    }

    /** Creates a new instance of PythonTestScript 
     * @throws FileNotFoundException 
     * @throws IOException 
     */
    public JythonTestScript(List<LinkedHashMap<String, String>> data, List<TestRequirement> requirements, File fileName, File testSuiteDirectory, TestSuite testSuite) throws IOException {
        super(fileName.getParentFile(), testSuiteDirectory, fileName.getParentFile().getName(), new TestDataSet(data), requirements, testSuite);

        this.fileName = fileName.getCanonicalFile().getAbsoluteFile();

        String version = VersionControl.getInstance().getTestApiVersion(fileName.getParentFile().getPath());
        //String version = parseVersion(fileName.getParentFile().getPath());
        setVersion(version);

        scriptTestData = new ScriptTestData();
        scriptBreakpoint = new ScriptBreakpoint();
    }

    public Component getComponent(String componentName, PyDictionary overriddenData) throws QTasteException {
        PyList oldValues = SaveTestDataValues(overriddenData);
        Component component = null;
        try {
            component = testAPI.getComponent(componentName, testData);
        } finally {
            RestoreTestDataValues(oldValues);
        }
        return component;
    }

    public void addStepResult(String stepId, Status stepStatus, String functionName, String stepDoc, double elapsedTime) {
        double elapsedTimeMs = elapsedTime * 1000;

        String stepDescription = null;
        String expectedResult = null;
        if (stepStatus == Status.RUNNING) {
            // only parse pythondoc and pass step description and expected result the first time
            if (stepDoc != null) {
                HashMap<String, String> stepDocTags = parsePythonDoc(stepDoc);
                stepDescription = stepDocTags.get("step");
                expectedResult = stepDocTags.get("expected");
                if (stepDescription == null) {
                    logger.warn("Function step " + functionName + " documentation contains no @step tag");
                    stepDescription = "";
                } else if (stepDescription.length() == 0) {
                    logger.warn("Function step " + functionName + " documentation contains no description for the @step tag");
                }
                if (expectedResult == null) {
                    expectedResult = "";
                }
            } else {
                logger.warn("Function step " + functionName + " has no docstring documentation");
            }
        }
        testResult.addStepResult(stepId, functionName, stepDescription, expectedResult, stepStatus, elapsedTimeMs);
    }

    /**
     * Parses a PythonDoc comment and returns a map of tag names to descriptions 
     * @param pythonDoc a PythonDoc comment
     * @return a map of tag names to descriptions; the description before tags
     *         is mapped to an empty tag name
     */
    public static HashMap<String, String> parsePythonDoc(String pythonDoc) {
        HashMap<String, String> tagsDoc = new HashMap<String, String>();
        String[] lines = pythonDoc.split("\\s*\n\\s*");
        String tag = "";
        List<String> textLines = new ArrayList<String>();
        for (String line : lines) {
            if (line.startsWith("@")) {
                tagsDoc.put(tag, Strings.join(textLines, "\n"));
                String[] tagAndText = line.substring(1).split("\\s+", 2);
                tag = tagAndText[0];
                textLines.clear();
                if (tagAndText.length == 2) {
                    textLines.add(tagAndText[1]);
                }
            } else {
                textLines.add(line);
            }
        }
        tagsDoc.put(tag, Strings.join(textLines, "\n"));
        return tagsDoc;
    }

    public static void addToGlobalJythonScope(String name, Object value) {
        Bindings globalContext = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        globalContext.put(name, value);
    }

    public PyDictionary getTestDataArgumentsDictionary(String[] dataNames, int numberDataToSkip, Method method) throws QTasteDataException {
        Class<?>[] parametersClasses = method.getParameterTypes();
        if (parametersClasses.length == dataNames.length) {
            PyDictionary dictionary = new PyDictionary();
            for (int i = numberDataToSkip; i < dataNames.length; i++) {
                String dataName = dataNames[i];
                Class<?> parameterClass = parametersClasses[i];
                try {
                    testData.getValue(dataName);
                } catch (QTasteDataException e) {
                    continue;
                }
                Object dataValue;
                if (parameterClass == String.class || parameterClass == File.class) {
                    dataValue = testData.getValue(dataName);
                } else if (parameterClass == int.class || parameterClass == Integer.class) {
                    dataValue = testData.getIntValue(dataName);
                } else if (parameterClass == double.class || parameterClass == Double.class || parameterClass == float.class || parameterClass == Float.class) {
                    dataValue = testData.getDoubleValue(dataName);
                } else if (parameterClass == boolean.class || parameterClass == Boolean.class) {
                    dataValue = testData.getBooleanValue(dataName);
                } else if (parameterClass == DoubleWithPrecision.class) {
                    dataValue = testData.getDoubleWithPrecisionValue(dataName);
                } else {
                    logger.error("Unsupported method argument type " + parameterClass.getName());
                    continue;
                }
                dictionary.__setitem__(dataName, Py.java2py(dataValue));
            }
            return dictionary;
        } else {
            logger.error("parametersClasses.length != dataNames.length");
            return null;
        }
    }

    public Object[] convertStringArguments(Object[] arguments, Method method) throws QTasteDataException {
        Class<?>[] parametersClasses = method.getParameterTypes();
        if (parametersClasses.length >= arguments.length) {
            for (int i = 0; i < arguments.length; i++) {
                Object argument = arguments[i];
                Class<?> parameterClass = parametersClasses[i];
                if (argument instanceof String && !(parameterClass == String.class || parameterClass == File.class)) {
                    final TestData tempTestData = new TestDataImpl(testData.getRowId(), new LinkedHashMap<String, String>());
                    final String tempDataName = "TEMP_DATA";
                    tempTestData.setValue(tempDataName, (String) argument);
                    if (parameterClass == int.class || parameterClass == Integer.class) {
                        arguments[i] = tempTestData.getIntValue(tempDataName);
                    } else if (parameterClass == double.class || parameterClass == Double.class || parameterClass == float.class || parameterClass == Float.class) {
                        arguments[i] = tempTestData.getDoubleValue(tempDataName);
                    } else if (parameterClass == boolean.class || parameterClass == Boolean.class) {
                        arguments[i] = tempTestData.getBooleanValue(tempDataName);
                    } else if (parameterClass == DoubleWithPrecision.class) {
                        arguments[i] = tempTestData.getDoubleWithPrecisionValue(tempDataName);
                    } else {
                        logger.error("Unsupported method argument type " + parameterClass.getName());
                        throw new QTasteDataException("Invalid argument used for method " + method.getName() + " with parameter:" + argument.toString());
                    }
                }
            }
        } else {
            logger.error("parametersClasses.length < arguments.length");
            throw new QTasteDataException("Invalid number of arguments for method " + method.getName());
        }
        return arguments;
    }

    public void logInvoke(Component component, String method, String arguments) {
        logger.info("Invoking " + testAPI.getComponentName(component) + "." + method + "(" + arguments + ")");
    }

    public static List<String> getAdditionalPythonPath(File file) {
        List<String> pythonlibs = new ArrayList<String>();
	//add librairies references by the environment variable
	for ( String additionnalPath : StaticConfiguration.JYTHON_LIB.split(File.pathSeparator) ) 
	{
	    File directory = new File(additionnalPath);
            pythonlibs.add(directory.toString());
	}

	if (!file.getAbsolutePath().contains("TestSuites")) {
            return pythonlibs;
        }
        try {
            File directory = file.getAbsoluteFile().getCanonicalFile();
            while (!directory.getName().equals("TestSuites")) {
                //File testSuitesDirectory = new File("TestSuites").getAbsoluteFile().getCanonicalFile();
                //do {
                directory = directory.getParentFile();
                pythonlibs.add(directory + File.separator + "pythonlib");
            } //while (!directory.equals(testSuitesDirectory));
        } catch (IOException e) {
            logger.error("Error while getting pythonlib directories: " + e.getMessage());
        }
        return pythonlibs;
    }

    public List<String> getAdditionalPythonPath() {
        return getAdditionalPythonPath(fileName);
    }

    public boolean execute(TestData data, TestResult result, final boolean debug) {
        // Interpret the file
        testData = data;
        testResult = result;

        if (debug) {
            testScriptBreakPointEventHandler.addTestScriptBreakpointListener(scriptBreakpoint);
        }

        try {
            bindings = engine.createBindings();
            engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

            // add all global bindinds
            bindings.putAll(globalBindings);

            engine.eval("import sys as __sys");

            if (testSuite != null) {
                // add pythonlib subdirectories of test script directory up to test suites directory, to python path
                List<String> additionalPythonPath = getAdditionalPythonPath();
                String pythonPathScript = "";
                for (String pythonlib : additionalPythonPath) {
                    pythonPathScript += "__sys.path.append(r'" + pythonlib + "')\n";
                }
                engine.eval(pythonPathScript);
            }

            // reset doStep count / step id / step name stacks
            engine.eval("doStep.countStack = [0]\n" +
                    "doStep.stepIdStack = []\n" +
                    "doStep.stepNameStack = []\n");

            // add testAPI, testData and scriptBreakpoint to bindings
            bindings.put("this", this);
            engine.eval("testAPI = __TestAPIWrapper(this)");
            bindings.remove("__TestAPIWrapper");
            bindings.remove("this");
            bindings.put("testData", scriptTestData);
            bindings.put("DoubleWithPrecision", DoubleWithPrecision.class);
            bindings.put("QTasteException", QTasteException.class);
            bindings.put("QTasteDataException", QTasteDataException.class);
            bindings.put("QTasteTestFailException", QTasteTestFailException.class);
            bindings.put("__scriptBreakpoint", scriptBreakpoint);
            globalBindings.put("testScript", this);

            // create QTaste module with testAPI, testData, DoubleWithPrecision, doStep, doSteps, doSubStep, logger and Status
            engine.eval("class __QTaste_Module:\n" +
                    "    def __init__(self):\n" +
                    "        self.importTestScript= importTestScript\n" +
                    "        self.isInTestScriptImport= isInTestScriptImport\n" +
                    "        self.testAPI= testAPI\n" +
                    "        self.testData = testData\n" +
                    "        self.DoubleWithPrecision = DoubleWithPrecision\n" +
                    "        self.doStep = doStep\n" +
                    "        self.doSteps = doSteps\n" +
                    "        self.doSubStep = doStep\n" +
                    "        self.doSubSteps = doSteps\n" +
                    "        self.logger = logger\n" +
                    "        self.Status = Status\n" +
                    "        self.QTasteException = QTasteException\n" +
                    "        self.QTasteDataException = QTasteDataException\n" +
                    "        self.QTasteTestFailException = QTasteTestFailException\n" +
                    "__sys.modules['qtaste'] = __QTaste_Module()");

            // remove testAPI, testData, DoubleWithPrecision, doStep, doSteps, logger and Status from bindinds
            bindings.remove("__QTaste_Module");
            bindings.remove("importTestScript");
            bindings.remove("isCalledFromMainTestScript");
            bindings.remove("testAPI");
            bindings.remove("testData");
            bindings.remove("DoubleWithPrecision");
            bindings.remove("doStep");
            bindings.remove("doSteps");
            bindings.remove("logger");
            bindings.remove("Status");
            bindings.remove("QTasteException");
            bindings.remove("QTasteDataException");
            bindings.remove("QTasteTestFailException");

            if (!debug) {
                engine.eval("execfile(r'" + fileName + "', globals())");
            } else {
                // execute in debugger
                engine.eval(scriptDebuggerClassCode);
                engine.eval("__debugger = __ScriptDebugger()");
                bindings.remove("__bdb");
                bindings.remove("__ScriptDebugger");
                for (Breakpoint b : breakPointEventHandler.getBreakpoints()) {
                    engine.eval("__debugger.set_break(r'" + b.getFileName() + "', " + b.getLineIndex() + ")");
                }
                engine.eval("__debugger.runcall(execfile, r'" + fileName + "', globals())");
            }
        } catch (ScriptException e) {
            handleScriptException(e, result);
        } finally {
            if (debug) {
                testScriptBreakPointEventHandler.removeTestScriptBreakpointListener(scriptBreakpoint);
            }
        }
        return true;
    }

    protected PyList SaveTestDataValues(PyDictionary pythonArguments) throws QTasteDataException {
        PyList oldValues = new PyList();
        Iterator<?> argIterator = pythonArguments.items().iterator();
        while (argIterator.hasNext()) {
            Object oArg = argIterator.next();
            if (oArg instanceof PyTuple) {
                PyTuple argType = (PyTuple) oArg;
                PyObject[] pyObj = argType.getArray();
                assert pyObj[0] instanceof PyString;
                String key = pyObj[0].toString();
                // store the old value of the passed argument
                Object[] oldValue = new Object[2];
                oldValue[0] = key;
                try {
                    oldValue[1] = testData.getValue(key);
                } catch (QTasteDataException e) {
                    // do nothing, no testdata to overwrite
                    oldValue[1] = null;
                }
                oldValues.add(oldValue);
                testData.setValue(key, pyObj[1].toString());
            }
        }
        return oldValues;
    }

    protected void RestoreTestDataValues(PyList oldValues) throws QTasteDataException {
        Iterator<?> oldValuesIterator = oldValues.iterator();
        while (oldValuesIterator.hasNext()) {
            Object[] oldValue = (Object[]) oldValuesIterator.next();
            if (oldValue[1] == null) {
                testData.remove((String) oldValue[0]);
            } else {
                testData.setValue((String) oldValue[0], (String) oldValue[1]);
            }
        }
    }

    private void dumpScriptPythonStackDetails(TestResult result, Throwable error) {
        StackTraceElement stack[] = (error != null ? error.getStackTrace() : Thread.currentThread().getStackTrace());

        // get the stack trace and extract all necessary details
        boolean stackLastDataExtracted = false;
        String stackTrace = new String();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement stackElement = stack[i];
            if (stackElement.getLineNumber() != -1) {
                String className = stackElement.getClassName();
                if (className.startsWith("org.python.pycode.") || className.endsWith("$py") ||
                        className.equals(getClass().getCanonicalName() + "$ScriptTestData")) {
                    String methodName = stackElement.getMethodName();
                    String fileName = stackElement.getFileName();
                    int lineNumber = stackElement.getLineNumber();

                    if ((fileName.equals("embedded_jython") &&
                            (methodName.equals("f$0") || methodName.startsWith("doStep$") || methodName.startsWith("doSteps$") || methodName.startsWith("_TestAPIWrapper__invoke$") || methodName.startsWith("user_line$")))
							|| fileName.endsWith(File.separator + "bdb.py")) {
                        // this is the execfile() call in the embedded jython
                        // or the doStep() or doSteps function
                        // or a private __invokexxx() method of the __TestAPIWrapper class
                        // or the user_line() method of the __ScriptDebugger class
                        // or a function of the debugger
                        // so just skip
                        continue;
                    }

                    if (methodName.equals("f$0")) {
                        stackTrace += "\nat file " + fileName + " line " + lineNumber;
                    } else {
                        // remove $i suffix from method name
                        int dollarIndex = methodName.indexOf("$");
                        if (dollarIndex > 0) {
                            methodName = methodName.substring(0, dollarIndex);
                        }

                        stackTrace += "\n";

                        // check if function is a step, i.e. executed by doStep
                        if ((i + 6 < stack.length) && stack[i + 6].getMethodName().startsWith("doStep$")) {
                            String stepId;
                            Object doStep = engine.getBindings(ScriptContext.ENGINE_SCOPE).get("doStep");
                            if (doStep instanceof PyFunction) {
                                stepId = ((PyFunction) doStep).__getattr__("stepId").toString();
                                stackTrace += "step " + stepId + " ";
                            }
                        }

                        stackTrace += "function " + methodName;
                        if (!fileName.equals("embedded_jython") && !fileName.equals("JythonTestScript.java")) {
                            stackTrace += " at file " + fileName + " line " + lineNumber;
                        }
                    }
                    if (!stackLastDataExtracted && !fileName.equals("embedded_jython") && !fileName.equals("JythonTestScript.java")) {
                        stackLastDataExtracted = true;
                        result.setFailedLineNumber(lineNumber);
                        result.setFailedFunctionId(methodName);
                    }
                    result.addStackTraceElement(stackElement);
                }
            }
        }
        if (stackTrace.isEmpty() && (error != null)) {
            // in some case stack seems corrupted, e.g. timeout while script never returns
            // in this case, try to parse the printed stack trace to get filename and line number
            StringWriter stackTraceWriter = new StringWriter();
            error.printStackTrace(new PrintWriter(stackTraceWriter));
            String printedStackTrace = stackTraceWriter.toString();
            Pattern printedStackTracePattern = Pattern.compile(".*^  File \"(.*?)\", line (\\d+), .*", Pattern.MULTILINE | Pattern.DOTALL);
            Matcher printedStackTraceMatcher = printedStackTracePattern.matcher(printedStackTrace);
            if (printedStackTraceMatcher.matches()) {
                stackTrace = "at file " + printedStackTraceMatcher.group(1) +
                        " line " + printedStackTraceMatcher.group(2);
            } else {
                stackTrace = "?";
            }
        }
        if (stackTrace.startsWith("\n")) {
            stackTrace = stackTrace.substring(1);
        }
        result.setStackTrace(stackTrace);
    }

    private void handleScriptException(ScriptException e, TestResult result) {
        Throwable cause = e.getCause();

        // handle ThreadDeath exception
        if (cause instanceof PyException) {
            PyException pe = (PyException) cause;
            if (pe.value instanceof PyJavaInstance) {
                Object javaError = pe.value.__tojava__(Throwable.class);
                if (javaError != null && javaError != Py.NoConversion) {
                    if (javaError instanceof ThreadDeath) {
                        dumpScriptPythonStackDetails(result, cause);
                        throw (ThreadDeath) javaError;
                    }
                }
            }
        }

        result.setFailedLineNumber(e.getLineNumber());
        result.setStatus(TestResult.Status.NOT_AVAILABLE);
        String message = null;
        boolean dumpStack = true;

        if (cause instanceof PySyntaxError) {
            // set a clear syntax error message
            PySyntaxError syntaxError = (PySyntaxError) cause;
            try {
                PyString fileName, text;
                PyInteger lineNumber, columnNumber;
                if (syntaxError.value instanceof PyTuple) {
                    PyObject[] infos = ((PyTuple) ((PyTuple) syntaxError.value).getArray()[1]).getArray();
                    fileName = (PyString) infos[0];
                    lineNumber = (PyInteger) infos[1];
                    columnNumber = (PyInteger) infos[2];
                    text = (PyString) infos[3];
                } else {
                    fileName = (PyString) syntaxError.value.__getattr__(new PyString("filename"));
                    lineNumber = (PyInteger) syntaxError.value.__getattr__(new PyString("lineno"));
                    columnNumber = (PyInteger) syntaxError.value.__getattr__(new PyString("offset"));
                    text = (PyString) syntaxError.value.__getattr__(new PyString("text"));
                    message = "Python syntax error in file " + fileName + " at line " + lineNumber + ", column " + columnNumber + ":\n" + text;

                }
                message = "Python syntax error in file " + fileName + " at line " + lineNumber + ", column " + columnNumber + ":\n" + text;
                result.addStackTraceElement(new StackTraceElement("", "", fileName.toString(), Integer.parseInt(lineNumber.toString())));
                dumpStack = false;
            } catch (PyException pye) {
                message = "Python syntax error (Couldn't decode localization of error)";
            }
        } else if (cause instanceof PyException) {
            PyException pe = (PyException) cause;
            if (pe.value instanceof PyJavaInstance) {
                // check  if exception is UndeclaredThrowableException
                // in this case status is "failed" and message is taken from cause exception
                Object javaError = pe.value.__tojava__(Throwable.class);
                if (javaError != null && javaError != Py.NoConversion) {
                    if (javaError instanceof QTasteException) {
                        handleQTasteException((QTasteException) javaError, result);
                        message = result.getExtraResultDetails();
                    } else if (javaError instanceof UndeclaredThrowableException) {
                        result.setStatus(TestResult.Status.FAIL);
                        Throwable undeclaredThrowable = ((UndeclaredThrowableException) javaError).getCause();
                        if (undeclaredThrowable instanceof InvocationTargetException) {
                            message = getThrowableDescription(((InvocationTargetException) undeclaredThrowable).getCause());
                        } else {
                            message = getThrowableDescription(undeclaredThrowable);
                        }
                    } else if (javaError instanceof Throwable) {
                        message = getThrowableDescription((Throwable) javaError);
                    }
                }
            }
            if (message == null) {
                if (pe.type instanceof PyClass) {
                    String errorName = null, errorValue;
                    try {
                        PyObject doc = pe.value.__getattr__(new PyString("__doc__"));
                        if (doc != Py.None) {
                            errorName = doc.toString();
                            if (errorName.endsWith(".")) {
                                errorName = errorName.substring(0, errorName.length() - 1);
                            }
                        }
                    } catch (PyException pye) {
                    }
                    if (errorName == null) {
                        if (pe.type instanceof PyClass) {
                            errorName = ((PyClass) pe.type).__name__;
                        } else {
                            errorName = pe.type.toString();
                        }
                    }

                    try {
                        errorValue = pe.value.__str__().toString();
                    } catch (PyException pye) {
                        errorValue = pe.value.toString();
                    }
                    if (errorValue.startsWith(errorName)) {
                        message = errorValue;
                    } else {
                        message = errorName + ": " + errorValue;
                    }
                } else {
                    message = getThrowableDescription(e);
                }
            }
        } else {
            message = getThrowableDescription(e);
        }

        result.setExtraResultDetails(message);
        if (dumpStack) {
            dumpScriptPythonStackDetails(result, cause);
        }

        if (!result.getExtraResultDetails().isEmpty()) {
            logger.error(result.getExtraResultDetails());
        }
        if ((result.getStackTrace() != null) && !result.getStackTrace().isEmpty()) {
            logger.error("Script stack trace: \n" + result.getStackTrace());
        }
    }

    private String getThrowableDescription(Throwable e) {
        StringWriter descriptionWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(descriptionWriter));
        int sunReflectPos = descriptionWriter.getBuffer().indexOf("\tat sun.reflect.");
        if (sunReflectPos != -1) {
            descriptionWriter.getBuffer().setLength(sunReflectPos);
        }
        return descriptionWriter.toString();
    }

    /**
     * ScriptBreakpoint class passed to the script interpreter
     */
    public class ScriptBreakpoint implements TestScriptBreakpointListener {

        private final BreakpointManager breakpointMgr = BreakpointManager.getInstance();
        private TestScriptBreakpointEvent.Action action = null;
        private PyObject mLocals = null;

        /**
         * breakScript method is called from the Python Script in order to stop its execution
         * @param lineNumber script line number
         * @param locals giving the local variables if any
         * @return <ul>
         *             <li>0 if script execution must be stopped</li>
         *             <li>1 if script execution must be stopped at next executed line</li>
         *             <li>2 if script execution must be continued</li>
         *         </ul>
         */
        public int breakScript(String fileName, int lineNumber, PyObject locals) {
            final int ACTION_STOP = 0;
            final int ACTION_STEP = 1;
            final int ACTION_CONTINUE = 2;
            final int ACTION_STEPINTO = 3;

            try {
                mLocals = locals;

                // inform the listeners that breakpoint has been reached
                File file = new File(fileName);
                if (file.exists()) {
                    String filePathName = fileName;
                    try {
                        filePathName = file.getCanonicalPath();
                    } catch (Exception e) {
                        // ????
                    }
                    testScriptBreakPointEventHandler.break_(filePathName, lineNumber);
                    // retrieve the variable
                    pythonResultEventHandler.pythonResult(getPythonVariablesDump());

                    logger.info("Test case stopped at file " + fileName + " line " + lineNumber);

                    // Stop the script executioncanonicalName until the Condition 'startCondition' has been reached
                    //   Actually, this startCondition is set by the GUI 
                    breakpointMgr.stop();
                    logger.debug("beakpoint start is called with action " + action.toString());
                    switch (action) {
                        case CONTINUE:
                            try {
                                // TODO: should be better to only add/remove modified breakpoints
                                engine.eval("__debugger.clear_all_breaks()");
                                for (Breakpoint b : breakPointEventHandler.getBreakpoints()) {
                                    engine.eval("__debugger.set_break(r'" + b.getFileName() + "', " + b.getLineIndex() + ")");
                                }
                            } catch (ScriptException e) {
                                logger.error("Couldn't reset breakpoints !");
                            }

                            logger.debug("Jython test script: CONTINUE");
                            testScriptBreakPointEventHandler.continue_();
                            // Inform the GUI that the script has been continued
                            logger.info("Test case continued at line " + lineNumber);
                            return ACTION_CONTINUE;
                        case STEP:
                            testScriptBreakPointEventHandler.step();
                            // Inform the GUI that the script has been continued
                            logger.info("Test case continued at line " + lineNumber);
                            return ACTION_STEP;
                        case STEPINTO:
                            testScriptBreakPointEventHandler.stepInto();
                            // Inform the GUI that the script has been continued
                            logger.info("Test case continued at line " + lineNumber);
                            return ACTION_STEPINTO;
                        case STOP:
                            testScriptBreakPointEventHandler.stop();
                            // Inform the GUI that the script has been continued
                            logger.info("Test case stopped at line " + lineNumber);
                            return ACTION_STOP;
                        default:
                            logger.error("Implementation error: action should be CONTINUE, STEP or STOP but is " + action);
                            throw new RuntimeException("Implementation error: action should be CONTINUE, STEP or STOP but is " + action);
                    }
                } else {
                    return ACTION_STEPINTO;
                }
            } catch (InterruptedException ex) {
                logger.error("Interrupted Exception occured\n:" + ex.getMessage() + "\nat line  " + lineNumber);
            }

            return ACTION_STOP;
        }

        /**
         * doAction This function is used to perform actions while the script has been stopped
         * @param event 
         */
        // 
        public void doAction(TestScriptBreakpointEvent event) {
            action = event.getAction();
            logger.debug("Received action in Jython testscript: " + event.getAction().toString());
            try {
                switch (action) {
                    case CONTINUE:
                    case STEP:
                    case STEPINTO:
                    case STOP:
                        logger.debug("breakpointMgr.start() called");
                        breakpointMgr.start();
                        break;
                    case DUMP_STACK:
                        // TODO
                        break;
                    case CALL_METHOD:
                        // TODO
                        break;
                }
            } catch (InterruptedException ex) {
                logger.error("Exception raised while handling breakpoint action: " + ex.getMessage());
            }
        }

        private ArrayList<DebugVariable> getPythonVariablesDump() {
            Bindings globalContext = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            ArrayList<DebugVariable> debugVariables = new ArrayList<DebugVariable>();
            for (String variableName : new TreeSet<String>(globalContext.keySet())) {
                Object variableValue = globalContext.get(variableName);
                if (!variableName.startsWith("__") &&
                        !(variableValue instanceof PySystemState) &&
                        !(variableValue instanceof com.sun.script.jython.JythonScriptEngine) &&
                        !(variableValue instanceof javax.script.SimpleScriptContext) &&
                        !variableName.equals("javax.script.filename")
                        //                    (!(variableValue instanceof PyClass)) &&
                        //                  (!(variableValue instanceof PyFunction)) &&
                        //                (!(variableValue instanceof java.lang.Class)) &&
                        //              (!(variableValue instanceof PyModule))
                        ) {
                    //Object variableValue = locals.get(new PyString(localKey));
                    DebugVariable debugVar = new DebugVariable(variableName, variableValue.getClass().toString(), variableValue.toString());
                    debugVar = dumpPythonObject(variableValue, debugVar);
                    debugVariables.add(debugVar);
                }

                /*
                if ((oValue instanceof Integer) ||
                (oValue instanceof String) ||
                (oValue instanceof Double)) {
                DebugVariable debugVar = new DebugVariable(key, oValue.getClass().toString(),
                oValue.toString());
                debugVariables.add(debugVar);
                }*/

            }
            if (mLocals instanceof PyStringMap) {
                PyStringMap locals = (PyStringMap) mLocals;
                PyList keys = locals.keys();
                Iterator<?> localIt = keys.iterator();
                while (localIt.hasNext()) {
                    Object oMap = localIt.next();
                    if (oMap instanceof String) {
                        String localKey = (String) oMap;
                        Object oValue = locals.get(new PyString(localKey));
                        DebugVariable debugVar = new DebugVariable(localKey.toString(),
                                oValue.getClass().toString(), oValue.toString());
                        debugVar = dumpPythonObject(oValue, debugVar);
                        debugVariables.add(debugVar);
                    }
                }
            }

            return debugVariables;
        }
    }

    private DebugVariable dumpJavaObject(Object javaObject, DebugVariable debugVar) {
        if (javaObject.getClass().getName().startsWith("java.lang.")) {
            return debugVar;
        }
        if (javaObject.getClass().equals(ArrayList.class)) {
            ArrayList<?> arrayList = (ArrayList<?>) javaObject;
            Iterator<?> arrayListIt = arrayList.iterator();
            int index = 0;
            while (arrayListIt.hasNext()) {
                Object javaObjectInArray = arrayListIt.next();
                DebugVariable fieldVar = new DebugVariable("[" + index + "]",
                        javaObjectInArray.getClass().toString(), javaObjectInArray.toString());
                fieldVar = dumpJavaObject(javaObjectInArray, fieldVar);
                debugVar.addField(fieldVar);
                index++;
            }
        }
        Field[] fields = javaObject.getClass().getFields();
        for (int fIndex = 0; fIndex < fields.length; fIndex++) {
            Field field = fields[fIndex];
            String fieldName = field.getName();
            try {
                Object fieldObject = field.get(javaObject);

                String fieldValue = fieldObject.toString();
                DebugVariable fieldVar = new DebugVariable(fieldName,
                        field.getClass().toString(), fieldValue.toString());
                fieldVar = dumpJavaObject(fieldValue, fieldVar);
                debugVar.addField(fieldVar);
            } catch (IllegalArgumentException e) {
                debugVar.addField(new DebugVariable(fieldName,
                        field.getClass().toString(), "Illegal argument"));
            } catch (IllegalAccessException e) {
                debugVar.addField(new DebugVariable(fieldName,
                        field.getClass().toString(), "Illegal Access Exception"));
            } catch (NullPointerException e) {
                debugVar.addField(new DebugVariable(fieldName,
                        field.getClass().toString(), "Null Pointer Exception"));
            }
        }
        Method[] methods = javaObject.getClass().getMethods();
        for (int methodIndex = 0; methodIndex < methods.length; methodIndex++) {
            Method method = methods[methodIndex];
            if ((method.getName().startsWith("get")) &&
                    (!(method.getName().equals("getClass"))) &&
                    (!(method.getName().equals("getAccessorKeys")))) {
                try {
                    Object returnValue = method.invoke(javaObject, new Object[]{});
                    DebugVariable fieldVar = new DebugVariable(method.getName(),
                            javaObject.getClass().toString(), returnValue.toString());
                    fieldVar = dumpJavaObject(returnValue, fieldVar);
                    debugVar.addField(fieldVar);
                } catch (Exception e) {
                }

            }
        }

        return debugVar;

    }

    private DebugVariable dumpPythonObject(Object value, DebugVariable debugVar) {
        if (value instanceof PyInstance) {
            PyInstance pyInstance = (PyInstance) value;
            Object javaInstance = pyInstance.__dir__().__tojava__(Object.class);
            debugVar = dumpPythonObject(javaInstance, debugVar);
            return debugVar;
        }
        if (value instanceof PyList) {
            PyList listValue = (PyList) value;
            Object[] dataArray = (Object[]) listValue.getArray();
            for (int i = 0; i < listValue.__len__(); i++) {
                Object o = dataArray[i];
                debugVar.addField(new DebugVariable("[" + i + "]",
                        o.getClass().toString(), o.toString()));
            }
            return debugVar;

        }
        if (value instanceof PyArray) {
            PyArray arrayValue = (PyArray) value;
            Object[] dataArray = (Object[]) arrayValue.getArray();
            for (int i = 0; i < arrayValue.__len__(); i++) {
                Object o = dataArray[i];
                debugVar.addField(new DebugVariable("[" + i + "]",
                        o.getClass().toString(), o.toString()));
                debugVar = dumpJavaObject(o, debugVar);
            }
            return debugVar;
        } else if (value instanceof PyJavaInstance) {
            PyJavaInstance pythonValue = (PyJavaInstance) value;
            Object javaObject = pythonValue.__tojava__(Object.class);
            if (javaObject instanceof ArrayList) {
                ArrayList<?> javaObjectArray = (ArrayList<?>) javaObject;
                Iterator<?> arrayIterator = javaObjectArray.iterator();
                while (arrayIterator.hasNext()) {
                    Object arrayListObject = arrayIterator.next();
                    debugVar = dumpJavaObject(arrayListObject, debugVar);
                }
            } else {
                debugVar = dumpJavaObject(javaObject, debugVar);
            }
        }
        return debugVar;

    }

    /**
     * ScriptTestData class passed to the script interpreter
     */
    public class ScriptTestData {

        public String getValue(String name) throws QTasteDataException {
            return testData.getValue(name);
        }

        public int getIntValue(String name) throws QTasteDataException {
            return testData.getIntValue(name);
        }

        public double getDoubleValue(String name) throws QTasteDataException {
            return testData.getDoubleValue(name);
        }

        public boolean getBooleanValue(String name) throws QTasteDataException {
            return testData.getBooleanValue(name);
        }

        public DoubleWithPrecision getDoubleWithPrecisionValue(String name) throws QTasteDataException {
            return testData.getDoubleWithPrecisionValue(name);
        }

        public byte[] getFileContentAsByteArray(String name) throws QTasteDataException {
            return testData.getFileContentAsByteArray(name);
        }

        public String getFileContentAsString(String name) throws QTasteDataException {
            return testData.getFileContentAsString(name);
        }

        public void setValue(String name, String value) throws QTasteDataException {
            testData.setValue(name, value);
        }

        public void setIntValue(String name, int value) throws QTasteDataException {
            setValue(name, new Integer(value).toString());
        }

        public void setDoubleValue(String name, double value) throws QTasteDataException {
            setValue(name, new Double(value).toString());
        }

        public void setBooleanValue(String name, boolean value) throws QTasteDataException {
            setValue(name, value ? "true" : "false");
        }

        public void setDoubleWithPrecisionValue(String name, DoubleWithPrecision value) throws QTasteDataException {
            setValue(name, value.toString());
        }

        public void remove(String name) {
            testData.remove(name);
        }

        public boolean contains(String name) {
            return testData.contains(name);
        }
    }

    /**
     * ScriptTestResultStatus class passed to the script interpreter.
     * Use this class instead of TestResult.Status directly to only
     * expose FAIL and NOT_AVAILABLE statuses.
     */
    public static class ScriptTestResultStatus {

        private ScriptTestResultStatus() {
        }
        public static final TestResult.Status FAIL = TestResult.Status.FAIL;
        public static final TestResult.Status NOT_AVAILABLE = TestResult.Status.NOT_AVAILABLE;
    }
}


