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

package com.qspin.qtaste.toolbox.doclet;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qspin.qtaste.util.FileUtilities;
import com.qspin.qtaste.util.Strings;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

/**
 * TestAPIDoclet is the Javadoc doclet used to generate HTML documentation
 * for the QTaste Test API Component interfaces.
 * <p>
 * Command-line option: 
 * <dt><b>-d</b> <var>directory</var>
 * <dd> Specifies the destination directory where javadoc saves the 
 * generated HTML files (the "d" means "destination"). Omitting this
 * option causes the files to be saved in the current directory.  
 * The value <var>directory</var> can be 
 * absolute, or relative to the current working directory.  
 * If the destination directory doesn't exist, it is automatically 
 * created when javadoc is run.
 * 
 * @author David Ergo
 */
public class TestAPIDoclet {

    // destination directory, including ending directory separator
    private static String mDestinationDirectory = "";
    private static final String TEST_API_KERNEL_PACKAGE = "com.qspin.qtaste.kernel.testapi";
    private static final Pattern NAME_TYPE_DESCRIPTION_PATTERN = Pattern.compile("^(\\w+)\\s+\\[(.+?)\\]\\s+(.+)$", Pattern.DOTALL); // name/type/description text format: "NAME [Type] Description"
    private static final String TESTAPI_DOC_STATIC_DIRECTORY = "/TestAPI-doc-static";

    /**
     * Returns Java language version.
     * @return LanguageVersion.JAVA_1_5
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * Generates documentation.
     * <p>
     * This method is required for all doclets.
     * 
     * @param root the RootDoc object provided by javadoc
     * @return true if success, false otherwise
     */
    public static boolean start(RootDoc root) {
        parseOptions(root.options());

        if (!makeDirectory(mDestinationDirectory)) {
            return false;
        }
        if (!makeDirectory(mDestinationDirectory + "/QTaste")) {
            return false;
        }
        if (!makeDirectory(mDestinationDirectory + "/components")) {
            return false;
        }

        // copy static files
        try {
            FileUtilities.copyResourceFiles(TestAPIDoclet.class, TESTAPI_DOC_STATIC_DIRECTORY, mDestinationDirectory);
            FileUtilities.copyResourceFiles(TestAPIDoclet.class, TESTAPI_DOC_STATIC_DIRECTORY + "/QTaste", mDestinationDirectory + "/QTaste");
        } catch (Exception e) {
            System.err.println("Error while copying static documentation files: " + e.getMessage());
            return false;
        }

        ClassDoc[] testAPIComponents = filterTestAPIComponents(root);
        if (testAPIComponents.length == 0) {
            System.err.println("No Test API Component interface found.");
            return true;
        }

        try {
            HTMLFileWriter listOut = new HTMLFileWriter(mDestinationDirectory + "allcomponents-frame.html", "all components", true);
            listOut.printHeading("QTaste Test API <A HREF=\"components-summary.html\" TARGET=\"detailsFrame\">components</A>", 3);

            HTMLFileWriter summaryOut = new HTMLFileWriter(mDestinationDirectory + "components-summary.html", "components summary", true);
            summaryOut.printHeading("QTaste Test API components", 2);
            summaryOut.printTestAPIComponentsSummary(testAPIComponents);
            summaryOut.close();
           

            for (int i = 0; i < testAPIComponents.length; i++) {
                ClassDoc classDoc = testAPIComponents[i];
                HTMLFileWriter out = new HTMLFileWriter(mDestinationDirectory + "components/" + classDoc.name() + ".html", classDoc.name(), true);
                out.printTestAPIComponentHeader(classDoc, root);
                out.printMethodsSummary(classDoc);
                out.printMethodsDetails(classDoc);
                out.close();

                listOut.printTestAPIComponentLink(classDoc);
               
            }

            listOut.close();
            
        } catch (java.io.IOException e) {
            System.err.println(e);
            return false;
        }

        return true;
    }

    /**
     * Check for doclet-added options. 
     * <p>
     * Returns the number of arguments you must specify on the command line for 
     * the given option. For example, "-d docs" would return 2.
     * <p>
     * This method is required if the doclet contains any options. 
     * If this method is missing, Javadoc will print an invalid flag error for 
     * every option. 
     * 
     * @param option the option to check
     * @return the number of arguments on the command line for the specified
     *         option including the option name itself.
     *         Zero means option not known. Negative value means error occurred.
     */
    public static int optionLength(String option) {
        // "-d directory" option (destination directory)
        if (option.equals("-d")) {
            return 2;
        }

        return 0;
    }

    /**
     * Checks that options have the correct arguments.
     * <p>
     * This method is not required, but is recommended, as every option will be 
     * considered valid if this method is not present. It will default 
     * gracefully (to true) if absent.
     * <p>
     * Printing option related error messages (using the provided 
     * DocErrorReporter) is the responsibility of this method. 
     * 
     * @param options the array of options to validate
     * @param reporter DocErrorReporter to which to report errors if options are not valid
     * @return true if the options are valid, false otherwise
     */
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        // no specific validation
        return true;
    }

    /** 
     * Returns true if given class is a Test API component interface.
     * 
     * @param classDoc the ClassDoc specifying the class
     * @param root the RootDoc passed to the doclet
     * @return true if specified class is a Test API component interface, false otherwise
     */
    public static boolean isTestAPI(ClassDoc classDoc, RootDoc root) {
        try {
        	ClassDoc TestAPIComponent = root.classNamed(TEST_API_KERNEL_PACKAGE + ".Component");
        	return classDoc.isInterface() && classDoc.subclassOf(TestAPIComponent) && (classDoc != TestAPIComponent);
        }        
        catch (Exception e) {
        	return false;
        }
    }

    /** 
     * Returns true if given class is a Test API instance Id component interface.
     * 
     * @param classDoc the ClassDoc specifying the class
     * @param root the RootDoc passed to the doclet
     * @return true if specified class is a Test API instance Id component interface, false otherwise
     */
    public static boolean isMultipleInstancesComponent(ClassDoc classDoc, RootDoc root) {
        ClassDoc TestAPIMultipleInstancesComponent = root.classNamed(TEST_API_KERNEL_PACKAGE + ".MultipleInstancesComponent");
        return classDoc.isInterface() && classDoc.subclassOf(TestAPIMultipleInstancesComponent) && (classDoc != TestAPIMultipleInstancesComponent);
    }

    /**
     * Returns the factory field doc of a Test API component interface.
     * @param classDoc the ClassDoc specifying the Test API component interface
     * @param root the RootDoc passed to the doclet
     * @return the FieldDoc of the factory field inherited from extended interface
     *         or null if no factory field found
     */
    public static FieldDoc getFactoryField(ClassDoc classDoc, RootDoc root) {
        ClassDoc ComponentFactory = root.classNamed(TEST_API_KERNEL_PACKAGE + ".ComponentFactory");
        for (ClassDoc interface_ : classDoc.interfaces()) {
            if (isTestAPI(interface_, root)) {
                for (FieldDoc field : interface_.fields()) {
                    if (field.name().equals("factory")) {
                        ClassDoc fieldClassDoc = field.type().asClassDoc();
                        if ((fieldClassDoc != null) && fieldClassDoc.subclassOf(ComponentFactory)) {
                            return field;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the methods of a Test API component.
     * @param classDoc the ClassDoc specifying the Test API component
     * @return an array of MethodDoc representing the methods
     */
    public static MethodDoc[] getTestAPIComponentMethods(ClassDoc classDoc) {
        MethodDoc[] methods = new MethodDoc[0];
        List<MethodDoc> methodsList = new ArrayList<MethodDoc>();
        for (MethodDoc methodDoc : classDoc.methods()) {
            methodsList.add(methodDoc);
        }
        // Also add the methods provided by the kernel
        for (ClassDoc interface_ : classDoc.interfaces()) {            
            if (!interface_.qualifiedName().startsWith(TEST_API_KERNEL_PACKAGE)) {
                for (MethodDoc methodDoc_ : interface_.methods()) {
                    methodsList.add(methodDoc_);
                }
            }
        }       
        return methodsList.toArray(methods);
    }

    /**
     * Parses command-line options.
     * The supported option is: -d directory (destination directory).
     * 
     * @param options the options passed to javadoc
     */
    private static void parseOptions(String[][] options) {
        for (int i = 0; i < options.length; i++) {
            String[] opt = options[i];

            if (opt[0].equals("-d")) {
                mDestinationDirectory = opt[1];
                if (!mDestinationDirectory.endsWith(File.separator)) {
                    mDestinationDirectory += File.separator;
                }
            }
        }

    }

    /**
     * Checks that directory exists or create it if necessary.
     * @param directoryPath directory path
     */
    private static boolean makeDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                System.err.println("Error: " + directoryPath + " exists but is not a directory.");
                return false;
            }
        } else if (!directory.mkdirs()) {
            System.err.println("Error while creating " + directoryPath + " directory.");
            return false;
        }
        return true;
    }

    /**
     * Sorts classes array by alphabetic order of the class name.
     * 
     * @param classes array of ClassDoc to sort
     */
    private static void sortClassesAlphabetically(ClassDoc[] classes) {
        class ClassDocComparator implements Comparator<ClassDoc> {

            public int compare(ClassDoc classDoc1, ClassDoc classDoc2) {
                return classDoc1.name().compareTo(classDoc2.name());
            }
        }

        Arrays.sort(classes, new ClassDocComparator());
    }

    /**
     * Returns an array of the classes that are Test API components interfaces,
     * sorted alphabetically.
     * 
     * @param root the RootDoc passed to the doclet
     * @return an array of the classes that are Test API components interfaces,
     *         sorted alphabetically
     */
    private static ClassDoc[] filterTestAPIComponents(RootDoc root) {
        ClassDoc[] classes = root.classes();
        sortClassesAlphabetically(classes);

        ArrayList<ClassDoc> verbsList = new ArrayList<ClassDoc>(classes.length);
        for (int i = 0; i < classes.length; i++) {
            ClassDoc classDoc = classes[i];
            if (isTestAPI(classDoc, root)) {
                // Exclude factory classes (MultipleInstancesComponent, SingletonComponent, ...)
                if (!classDoc.qualifiedTypeName().startsWith(TEST_API_KERNEL_PACKAGE)) {
                    verbsList.add(classDoc);
                }
            }
        }

        return verbsList.toArray(new ClassDoc[verbsList.size()]);
    }

    /**
     * Prints the dummy __TestAPI Python class declaration used for code completion,
     * including the stopTest method.
     * @param out the PrintWriter to print to
     */
    @SuppressWarnings("unused")
    private static void printDummyTestAPIClassDeclaration(PrintWriter out) {
        out.println("import __TestAPIComponents");
        out.println();
        out.println("class __TestAPI:");
        out.println("	\"\"\"Provides access to the Test API components and the stopTest method.\"\"\"");
        out.println("	def stopTest(self, status, message):");
        out.println("		\"\"\"Stops the test execution and set its status to \"failed\" or \"not available\", with an associated message.\"\"\"");
        out.println("		pass");
    }

    /**
     * Prints the dummy __TestAPI Python class declaration used for code completion,
     * including the stopTest method.
     * @param out the PrintWriter to print to
     * @param classDoc the ClassDoc specifying the class
     * @param root the RootDoc passed to the doclet
     */
    @SuppressWarnings("unused")
    private static void printDummyTestAPIClassGetComponentMethod(PrintWriter out, ClassDoc classDoc, RootDoc root) {
        String component = classDoc.name();
        boolean isMultipleInstancesComponent = isMultipleInstancesComponent(classDoc, root);
        out.println("	def get" + component + "(self" + (isMultipleInstancesComponent ? ", INSTANCE_ID=int" : "") + "):");
        out.println("		\"\"\"Gets the instance of the " + component + " Test API component" + (isMultipleInstancesComponent ? " for given instance Id" : "") + ".\"\"\"");
        out.println("		return __TestAPIComponents.__" + component + "()");
    }

    /**
     * Prints the dummy __<i>Component</i> Python class declaration used for code completion.
     * @param out the PrintWriter to print to
     * @param classDoc the ClassDoc specifying the class
     */
    @SuppressWarnings("unused")
    private static void printDummyTestAPIComponentClassDeclaration(PrintWriter out, ClassDoc classDoc) {
        out.println("class __" + classDoc.name() + ":");
        out.println("	\"\"\"" + getFirstSentenceText(classDoc) + "\"\"\"");
        for (MethodDoc methodDoc : classDoc.methods()) {
            List<String> arguments = new ArrayList<String>();
            arguments.add("self");

            for (Parameter parameter : methodDoc.parameters()) {
                arguments.add(parameter.name() + "=" + parameter.type().simpleTypeName());
            }

            out.println("	def " + methodDoc.name() + "(" + Strings.join(arguments, ", ") + "):");
            out.println("		\"\"\"" + getFirstSentenceText(methodDoc) + "\"\"\"");
            String returnType = methodDoc.returnType().qualifiedTypeName().replaceFirst("^java\\.lang\\.", "");
            if (returnType.equals("void")) {
                out.println("		pass");
            } else if (returnType.equals("boolean") || returnType.equals("Boolean")) {
                out.println("		return False");
            } else if (returnType.equals("int") || returnType.equals("Integer")) {
                out.println("		return 0");
            } else if (returnType.equals("double") || returnType.equals("Double") || returnType.equals("float") || returnType.equals("Float")) {
                out.println("		return 0.0");
            } else if (returnType.equals("String")) {
                out.println("		return \"\"");
            } else {
                out.println("		return " + returnType + "()");
            }
        }
        out.println();
    }

    /**
     * Gets the text of the first sentence of a Doc,
     * after removing HTML tags, replacing '\n' and '\t' by whitespaces and 
     * replacing multiple whitespaces by single ones.
     * @param doc the Doc specifying the class or method
     * @return the text of the first sentence
     */
    public static String getFirstSentenceText(Doc doc) {
        StringBuilder firstSentenceBuilder = new StringBuilder();
        for (Tag tag : doc.firstSentenceTags()) {
            firstSentenceBuilder.append(tag.text());
        }
        return firstSentenceBuilder.toString().replaceAll("<.+>", "").replaceAll("[\n\t ]+", " ");
    }

    /**
     * Gets the list of name,type and description parsed from an array of tags using the pattern
     * NAME_TYPE_DESCRIPTION_PATTERN. For non-matching tags, name is set to tag text, and type and description
     * are set to "INVALID_FORMAT".
     * @param tags array of Tag
     * @return List of NameTypeDescription
     */
    public static List<NameTypeDescription> getNameTypeDescriptionList(Tag[] tags) {
        List<NameTypeDescription> nameTypeDescriptionList = new ArrayList<NameTypeDescription>();

        for (Tag tag : tags) {
            String text = tag.text().trim();
            Matcher matcher = NAME_TYPE_DESCRIPTION_PATTERN.matcher(text);
            if (matcher.matches()) {
                nameTypeDescriptionList.add(new NameTypeDescription(matcher.group(1), matcher.group(2), matcher.group(3)));
            } else {
                String error = "Invalid " + tag.name() + " tag format \"" + text + "\" for ";
                if (tag.holder() instanceof MethodDoc) {
                    MethodDoc methodDoc = (MethodDoc) tag.holder();
                    error += "verb " + methodDoc.name() + " of component " + methodDoc.containingClass().simpleTypeName();
                } else if (tag.holder() instanceof ClassDoc) {
                    ClassDoc classDoc = (ClassDoc) tag.holder();
                    error += "component " + classDoc.simpleTypeName();
                } else {
                    error += "unknown Doc type";
                }
                System.err.println(error);
                nameTypeDescriptionList.add(new NameTypeDescription(text, "INVALID_FORMAT", "INVALID_FORMAT"));
            }
        }
        return nameTypeDescriptionList;
    }

    /**
     * Class for containing a name, a type and a description
     */
    public static class NameTypeDescription {

        public String name;
        public String type;
        public String description;

        public NameTypeDescription(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }
}
