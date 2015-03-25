#BASIC Compiler

This project is an open-source BASIC compiler written in Java.

It compiles a BASIC program into Java bytecode, which can be executed with any Java Virtual Machine.

I originally started this project to learn a little about Java bytecodes. First, I hacked together an empty Java class file then began adding bits and pieces. Then, this soon grew into implementing a BASIC compiler complete enough to compile and play classic BASIC games like, for example, "Star Trek". 

The BASIC compiler is self-contained. It uses only a minimum set of Java Virtual Machine methods and no other frameworks as I wanted to write the compiler from scratch. The functionality of the BASIC compiler is backed by about 1400 unit test programs in BASIC. The implemented BASIC language is oriented at Microsoft BASIC. The BASIC Compiler source code is available under the FreeBSD license.

* Find more information about how to compile and run the BASIC compiler in "docs/GettingStarted.pdf".
* Find more information about the implemented BASIC language in "docs/BASICCompilerLanguage.pdf".
* Find sample BASIC programs from David Ahl's classic "BASIC Computer Games" books, original (used with permission) and modified, in folder "samples".

Enjoy! -- Lorenz

## Project content

| File                           | Content                                              |
|:------------------------------ |:-----------------------------------------------------|
| readme.md                      | This file                                            |
| .project                       | Eclipse project file                                 |
| .classpath                     | Eclipse classpath file                               |
| src/                           | Source code folder                                   |
| doc/GettingStarted.pdf         | Getting started infos                                |
| doc/BASICCompilerLanguage.pdf  | Implemented BASIC language infos                     |
| samples.txt                    | Transcripts of compiled sample BASIC programs        |
| samples/                       | Sample BASIC programs folder                         |
| makejar.bat                    | Make batch file (Windows), creates BASICCompiler.jar |
| manifest.txt                   | Manifest file for makejar.bat                        |
| .gitignore                     | gitignore file                                       |
