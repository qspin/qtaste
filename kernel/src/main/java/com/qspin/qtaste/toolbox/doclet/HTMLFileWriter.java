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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.qspin.qtaste.util.Strings;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

/**
 * An HTMLFileWriter allows to print the different parts of the Test API
 * to a file in HTML format.
 * 
 * @author David Ergo
 */
public class HTMLFileWriter {

    private PrintWriter mOut;
    private boolean mWithBody;    // true to print body starting and ending tags, false otherwise
    private static final String QTaste_JAVADOC_URL_PREFIX = "../simulators/";
    private static final String SUT_JAVADOC_URL_PREFIX = "../../SUT/apidocs/";

    /**
     * Creates file and writes HTML header with optional body starting tag.
     * 
     * @param fileName the name of the HTML file to create
     * @param title the title of the HTML page
     * @param withBody true to print body starting tag, false otherwise
     * @throws java.io.IOException if there was an error while opening the file
     */
    public HTMLFileWriter(String fileName, String title, boolean withBody) throws java.io.IOException {
        mOut = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        mOut.println("<HTML><HEAD><TITLE>" + title + "</TITLE></HEAD>");
        this.mWithBody = withBody;
        if (withBody) {
            mOut.println("<BODY>");
        }
    }

    /**
     * Finalizes object: close file if it is opened.
     */
    @Override
    protected void finalize() {
        if (mOut != null) {
            close();
        }
    }

    /**
     * Writes HTML body ending tag if needed and HTML footer and closes file.
     */
    public void close() {
        if (mWithBody) {
            mOut.println("</BODY>");
        }
        mOut.println("</HTML>");
        mOut.close();
        mOut = null;
    }

    /**
     * Prints string and terminates line.
     * 
     * @param s the string to be written
     */
    public void println(String s) {
        mOut.println(s);
    }

    /**
     * Prints Test API Component header.
     * 
     * @param classDoc the ClassDoc specifying the class
     * @param root the RootDoc passed to the doclet
     */
    public void printTestAPIComponentHeader(ClassDoc classDoc, RootDoc root) {
        mOut.println("<H2>QTaste Test API Component Interface " + classDoc.name() + "</H2>");
        mOut.println(classDoc.commentText());

        // factory
        FieldDoc factoryField = TestAPIDoclet.getFactoryField(classDoc, root);
        if (factoryField != null) {
            mOut.println("<P>");
            mOut.println(factoryField.commentText());
        }

        // config tags
        Tag[] tags = classDoc.tags("@config");
        if (tags.length > 0) {
            mOut.println("<DL>");
            mOut.println("<DT><B>Configuration:</B></DT>");
            List<TestAPIDoclet.NameTypeDescription> configList = TestAPIDoclet.getNameTypeDescriptionList(tags);
            for (TestAPIDoclet.NameTypeDescription config : configList) {
                mOut.println("<DD><CODE>" + config.name + "</CODE> (<CODE>" + config.type + "</CODE>) - " + config.description + "</DD>");
            }
            mOut.println("</DL>");
        }
    }

    /**
     * Prints summary of Test API methods, excluding old-style verbs, in HTML format.
     *
     * @param classDoc the classDoc of the Test API component
     */
    public void printMethodsSummary(ClassDoc classDoc) {
        MethodDoc[] methodDocs = TestAPIDoclet.getTestAPIComponentMethods(classDoc);
        if (methodDocs.length > 0) {
            mOut.println("<P>");
            mOut.println("<TABLE BORDER=\"1\" WIDTH=\"100%\" CELLPADDING=\"3\" CELLSPACING=\"0\" SUMMARY=\"\">");
            mOut.println("<TR BGCOLOR=\"#CCCCFF\"><TH ALIGN=\"left\" COLSPAN=\"2\"><FONT SIZE=\"+2\"><B>Methods Summary</B></FONT></TH></TR>");
            for (MethodDoc methodDoc : methodDocs) {
                String methodName = methodDoc.name();
                mOut.print("<TR><TD WIDTH=\"1%\"><CODE><B><A HREF=\"#" + methodName + methodDoc.flatSignature() + "\">" + methodName + "</A></B></CODE></TD><TD>");
                Tag[] firstSentenceTags = methodDoc.firstSentenceTags();
                if (firstSentenceTags.length == 0) {
                    System.err.println("Warning: method " + methodName + " of " + methodDoc.containingClass().simpleTypeName() + " has no description");
                }
                printInlineTags(firstSentenceTags, classDoc);
                mOut.println("</TD>");
            }

            mOut.println("</TABLE>");
        }
    }


    /**
     * Prints details of Test API methods, excluding old-style verbs, in HTML format.
     *
     * @param classDoc the classDoc of the Test API component
     */
    public void printMethodsDetails(ClassDoc classDoc) {
        MethodDoc[] methodDocs = TestAPIDoclet.getTestAPIComponentMethods(classDoc);
        if (methodDocs.length > 0) {
            mOut.println("&nbsp;<P>");
            mOut.println("<TABLE BORDER=\"1\" WIDTH=\"100%\" CELLPADDING=\"3\" CELLSPACING=\"0\" SUMMARY=\"\">");
            mOut.println("<TR BGCOLOR=\"#CCCCFF\"><TH ALIGN=\"left\" COLSPAN=\"2\"><FONT SIZE=\"+2\"><B>Methods Detail</B></FONT></TH></TR>");
            mOut.println("</TABLE>");
            for (int m = 0; m < methodDocs.length; m++) {
                MethodDoc methodDoc = methodDocs[m];
                String methodName = methodDoc.name();

                // get return type
                String returnTypeString = getTypeString(methodDoc.returnType());
                int returnTypeLength = returnTypeString.replaceAll("<.*?>", "").replace("&lt;", "<").replace("&gt;", ">").length(); // length without HTML tags and &gt;/&lt; entities

                Parameter[] parameters = methodDoc.parameters();
                String[] parameterSignatures = new String[parameters.length];
                for (int i = 0; i < parameterSignatures.length; i++) {
                    Parameter parameter = parameters[i];
                    parameterSignatures[i] = getTypeString(parameter.type()) + " " + parameter.name();
                }
                String parameterSeparator = ",\n";
                for (int i = 0; i < returnTypeLength + methodName.length() + 2; i++) {
                    parameterSeparator += " ";
                }
                
                
                // begin output
                mOut.println("<A NAME=\"" + methodName + "\"><A NAME=\"" + methodName + methodDoc.flatSignature() + "\"> <H3>" + methodName + "</H3></A></A>");
                mOut.print("<PRE>" + returnTypeString + " <B>" + methodName + "</B>(" + Strings.join(parameterSignatures, parameterSeparator) + ")");

                Type[] exceptions = methodDoc.thrownExceptionTypes();
                if (exceptions.length > 0) {
                    String[] exceptionNames = new String[exceptions.length];
                    for (int i = 0; i < exceptions.length; i++) {
                        exceptionNames[i] = getTypeString(exceptions[i]);
                    }
                  mOut.print(parameterSeparator.substring(1, parameterSeparator.length() - 7) + "throws " + Strings.join(exceptionNames, parameterSeparator));
                }
                mOut.println("</PRE>");
                mOut.print("<DL><DD>");
                printInlineTags(methodDoc.inlineTags(), classDoc);

                mOut.println("<P><DD><DL>");

                // param tags
                ParamTag[] paramTags = methodDoc.paramTags();
                if (parameters.length > 0) {
                    mOut.println("<DT><B>Parameters:</B></DT>");
                    for (Parameter parameter : parameters) {
                        ParamTag paramTag = null;
                        for (ParamTag tag : paramTags) {
                            if (tag.parameterName().equals(parameter.name())) {
                                paramTag = tag;
                            }
                        }
                        mOut.println("<DD><CODE>" + parameter.name() + "</CODE> - ");
                        if (paramTag != null) {
                            if (!paramTag.parameterComment().isEmpty()) {
                                printInlineTags(paramTag.inlineTags(), classDoc);
                            } else {
                                System.out.println("No description in @param tag for " + parameter.name() + " in " + classDoc.name() + "." + methodDoc.name());
                            }
                        } else {
                            System.out.println("No @param tag for " + parameter.name() + " in " + classDoc.name() + "." + methodDoc.name());
                        }
                        mOut.println("</DD>");
                    }
                }

                // return tag
                Tag[] returnTags = methodDoc.tags("@return");
                if (returnTags.length > 0) {
                    mOut.println("<DT><B>Returns:</B></DT>");
                    mOut.println("<DD>" + returnTags[0].text() + "</DD>");
                }

                // throws tag
                ThrowsTag[] throwsTags = methodDoc.throwsTags();
                if (throwsTags.length > 0) {
                    mOut.println("<DT><B>Throws:</B></DT>");
                    for (ThrowsTag throwsTag: throwsTags) {
                        String exceptionName = throwsTag.exceptionName();
                        // remove "com.qspin.qtaste.testsuite." prefix if any
                        if (exceptionName.startsWith("com.qspin.qtaste.testsuite."))
                        {
                           exceptionName = exceptionName.substring("com.qspin.qtaste.testsuite.".length());
                        }
                        mOut.println("<DD><CODE>" + exceptionName + "</CODE> - ");
                        printInlineTags(throwsTag.inlineTags(), classDoc);
                        mOut.println("</DD>");
                    }
                }

                mOut.println("</DL></DD></DL>");

                if (m != methodDocs.length-1) {
                    mOut.println("<HR>");
                }
            }
        }
    }

    /**
     * Returns type string.
     * @param type type for which to return type string
     * @return type string, including parametrized types, dimensions and links.
     */
    private String getTypeString(Type type) {
        String typeQualifiedName = type.qualifiedTypeName().replaceFirst("^java\\.lang\\.", "");
        typeQualifiedName = typeQualifiedName.replaceFirst("^com\\.qspin\\.qtaste\\.testsuite\\.(QTaste\\w*Exception)", "$1");
        String typeDocFileName = null;
        if (typeQualifiedName.startsWith("com.qspin.")) {
            String javaDocDir = typeQualifiedName.startsWith("com.qspin.qtaste.") ? QTaste_JAVADOC_URL_PREFIX : SUT_JAVADOC_URL_PREFIX;
            typeDocFileName = javaDocDir + typeQualifiedName.replace('.', '/') + ".html";
        }
        String typeString = typeQualifiedName;
        if (typeDocFileName != null) {
            typeString = "<A HREF=\"" + typeDocFileName + "\">" + typeString + "</A>";
        }
        if (type.asParameterizedType() != null) {
            ParameterizedType parametrizedType = type.asParameterizedType();
            final Type[] parameterTypes = parametrizedType.typeArguments();
            if (parameterTypes.length > 0) {
                String[] parametersTypeStrings = new String[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    parametersTypeStrings[i] = getTypeString(parameterTypes[i]);
                }
                typeString += "&lt;" + Strings.join(parametersTypeStrings, ",") + "&gt;";
            }
        }
        typeString += type.dimension();

        return typeString;
    }

    /**
     * Prints summary of Test API components, in HTML format.
     * 
     * @param testAPIComponents the array of ClassDoc specifying all test API components
     */
    public void printTestAPIComponentsSummary(ClassDoc[] testAPIComponents) {
        mOut.println("<P>");
        mOut.println("<TABLE BORDER=\"1\" WIDTH=\"100%\" CELLPADDING=\"3\" CELLSPACING=\"0\" SUMMARY=\"\">");
        mOut.println("<TR BGCOLOR=\"#CCCCFF\"><TH ALIGN=\"left\" COLSPAN=\"2\"><FONT SIZE=\"+2\"><B>Components Summary</B></FONT></TH></TR>");
        for (int c = 0; c < testAPIComponents.length; c++) {
            ClassDoc classDoc = testAPIComponents[c];
            String componentName = classDoc.name();
            mOut.print("<TR><TD WIDTH=\"1%\"><CODE><B><A HREF=\"components/" + componentName + ".html\">" + componentName + "</A></B></CODE></TD><TD>");
            printInlineTags(classDoc.firstSentenceTags(), classDoc);
            mOut.println("</TD>");
        }
        mOut.println("</TABLE>");
    }

    /**
     * Prints heading, in HTML format.
     * 
     * @param heading the heading to print
     * @param level heading level
     */
    public void printHeading(String heading, int level) {
        mOut.println("<H" + level + ">" + heading + "</H" + level + ">");
    }

    /**
     * Prints link to Test API Component HTML file, in HTML format.
     * 
     * @param classDoc the classDoc of the Test API component
     */
    public void printTestAPIComponentLink(ClassDoc classDoc) {
        String className = classDoc.name();
        mOut.println("<A HREF=\"components/" + className + ".html\" TARGET=\"detailsFrame\"><I>" + className + "</I></A><BR>");
    }

    /**
     * Prints inline tags, in HTML format.
     * 
     * @param tags the array of Tag to print
     */
    private void printInlineTags(Tag[] tags, ClassDoc classDoc) {
        for (int i = 0; i < tags.length; i++) {
            if ((tags[i] instanceof SeeTag) && tags[i].name().equals("@link")) {
            	SeeTag seeTag = (SeeTag) tags[i];
            	boolean sameClass = seeTag.referencedClass() == classDoc;
            	String fullClassName = seeTag.referencedClassName();
            	String memberName = seeTag.referencedMemberName();

            	String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
            	List<String> nameParts = new ArrayList<String>();
            	if (!sameClass) {
            		nameParts.add(className);
            	}
            	if (memberName != null) {
            		nameParts.add(memberName);
            	}
            	String name = Strings.join(nameParts, ".");

            	if (fullClassName.lastIndexOf('.') >= 0) {
	            	String packageName = fullClassName.substring(0, fullClassName.lastIndexOf('.'));
	            	String urlPrefix = "";
	                if (!sameClass && packageName.startsWith("com.qspin.") && !packageName.equals("com.qspin.qtaste.testapi.api")) {
	                    String javaDocDir = packageName.startsWith("com.qspin.qtaste.") ? QTaste_JAVADOC_URL_PREFIX : SUT_JAVADOC_URL_PREFIX;
	                    urlPrefix = javaDocDir + packageName.replace('.', '/') + "/";
	                }
	            	String url = (sameClass ? "" : urlPrefix + className + ".html") + (memberName != null ? "#" + memberName : "") ;
	
	            	mOut.print("<A HREF=\"" + url + "\">" + name + "</A>");
            	} else {
	            	mOut.print(name);            		
            	}
            } else {
                mOut.print(tags[i].text());
            }
        }
    }
}
