/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Lorenz Wiest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package de.lorenzwiest.basiccompiler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.CompileException;
import de.lorenzwiest.basiccompiler.parser.Parser;
import de.lorenzwiest.basiccompiler.parser.statements.Statement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.LineNumberStatement;

public class BASICCompiler {
	private static final String CR = System.getProperty("line.separator");
	private static final String HELP = "" //
			+ " ___   _   ___  _  ___    ___                _ _" + CR //
			+ "| _ ) /_\\ / __|| |/ __|  / __|___ _ __  _ __(_) |___ _ _" + CR //
			+ "| _ \\/ _ \\\\__ \\| | (__  | (__/ _ \\ '  \\| '_ \\ | | -_) '_|" + CR //
			+ "|___/_/ \\_\\___/|_|\\___|  \\___\\___/_|_|_| .__/_|_|___|_|" + CR //
			+ "                                       |_|" + CR //
			+ "Version 1.6 (22-DEC-2019) by Lorenz Wiest" + CR //
			+ CR //
			+ "Usage:   java BASICCompiler <BASIC source filename> <Java class filename> [<options>]" + CR //
			+ "Options: -formatted=<filename> | Writes a formatted BASIC source file" + CR //
			+ "         -optimize             | Applies compiler optimizations";

	private static final String OPT_FORMATTED_OUTPUT = "-formatted=";
	private static final String OPT_OPTIMIZE = "-optimize";

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println(HELP);
		} else {
			String inFilename = args[0];
			String outFilename = args[1];

			Properties properties = getProperties(Arrays.asList(args).subList(2, args.length));
			if (properties != null) {
				BufferedReader inReader = null;
				BufferedOutputStream outStream = null;
				try {
					inReader = new BufferedReader(new FileReader(inFilename));
					if (outFilename.endsWith(".class") == false) {
						outFilename += ".class";
					}
					outStream = new BufferedOutputStream(new FileOutputStream(outFilename));
					String className = getClassName(outFilename);
					BASICCompiler.exec(inReader, outStream, className, properties);
				} catch (FileNotFoundException e) {
					if (inReader == null) {
						System.out.println("ERROR: Cannot find file \"" + inFilename + "\".");
					} else {
						System.out.println("ERROR: Cannot open file \"" + outFilename + "\".");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CompileException ex) {
					System.out.println(ex.getFullMessage());
				} finally {
					closeGracefully(inReader);
					closeGracefully(outStream);
				}
			}
		}
	}

	private static void closeGracefully(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private static Properties getProperties(List<String> propertyArgs) {
		Properties properties = new Properties();
		List<String> argList = new ArrayList<String>(propertyArgs);
		for (String propertyArg : propertyArgs) {
			if (propertyArg.startsWith(OPT_FORMATTED_OUTPUT)) {
				if (properties.containsKey(OPT_FORMATTED_OUTPUT) == false) {
					properties.setProperty(OPT_FORMATTED_OUTPUT, propertyArg.substring(OPT_FORMATTED_OUTPUT.length()));
					argList.remove(propertyArg);
				} else {
					System.out.println("ERROR: Option \"" + OPT_FORMATTED_OUTPUT + "\" used twice.");
					return null;
				}
			}

			if (propertyArg.equals(OPT_OPTIMIZE)) {
				if (properties.containsKey(OPT_OPTIMIZE) == false) {
					properties.setProperty(OPT_OPTIMIZE, "");
					argList.remove(propertyArg);
				} else {
					System.out.println("ERROR: Option \"" + OPT_OPTIMIZE + "\" used twice.");
					return null;
				}
			}
		}

		if (argList.isEmpty() == false) {
			System.out.println("WARNING: Unknown option \"" + argList.get(0) + "\" ignored.");
		}

		return properties;
	}

	private static String getClassName(String fullFilePath) {
		String className = fullFilePath;
		int lastIndexOf = className.lastIndexOf(File.separatorChar);
		if (lastIndexOf > 0) {
			className = className.substring(lastIndexOf + 1);
		}
		className = className.substring(0, className.length() - ".class".length());
		return className;
	}

	public static void exec(BufferedReader inReader, OutputStream outStream, String className) throws IOException {
		exec(inReader, outStream, className, new Properties());
	}

	public static void exec(BufferedReader inReader, OutputStream outStream, String className, Properties properties) throws IOException {
		int lineNr = 0;

		List<Statement> statements = new ArrayList<Statement>();
		try {
			Map<Integer /* lineNumber */, List<Statement> /* statements of line */> sortedLinesOfStatements = new TreeMap<Integer, List<Statement>>();

			Parser parser = new Parser();
			while (true) {
				String line = inReader.readLine();
				if (line == null) {
					break;
				}

				lineNr++;
				List<Statement> statementsOfLine = parser.parseLine(line);
				if (statementsOfLine.size() > 0) {
					int lineNumber = Integer.parseInt(((LineNumberStatement) statementsOfLine.get(0)).getLineNumber());
					sortedLinesOfStatements.put(lineNumber, statementsOfLine);
				}
			}
			parser.flush();

			for (int lineNumber : sortedLinesOfStatements.keySet()) {
				List<Statement> statementsOfLine = sortedLinesOfStatements.get(lineNumber);
				statements.addAll(statementsOfLine);
			}

			if (properties.containsKey(OPT_FORMATTED_OUTPUT)) {
				String formattedOutputFilename = properties.getProperty(OPT_FORMATTED_OUTPUT);
				new CodeFormatter().formatToFile(statements, formattedOutputFilename);
			}

			if (properties.containsKey(OPT_OPTIMIZE)) {
				new CodeOptimizer().optimize(statements);
			}

			Compiler compiler = new Compiler(className);
			for (Statement statement : statements) {
				compiler.compile(statement);
			}
			compiler.flush();
			compiler.getClassModel().write(outStream);
		} catch (CompileException ex) {
			throw new CompileException(lineNr, ex.getMessage());
		}
	}
}
