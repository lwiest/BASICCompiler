# BASIC Compiler

BASIC Compiler is an open-source BASIC compiler written in Java.

It compiles a BASIC program into Java bytecode, which can be executed with any Java Virtual Machine 1.5 and higher.

Originally, I started this project to learn about Java bytecodes, hacking together an empty Java class file, adding bits and pieces. Soon this grew into implementing a BASIC compiler complete enough to compile and play classic BASIC games like, for example, "StarTrek". 

BASIC Compiler is self-contained. It uses only a minimum set of Java Virtual Machine methods and by intention no other frameworks as I wanted to write the compiler from scratch. The functionality of BASIC Compiler is backed by over 1500 unit test programs in BASIC. The implemented BASIC language is oriented at Microsoft BASIC.

The BASIC Compiler source code is available under the MIT license.

Enjoy! -- Lorenz

## Table of Contents

* [Getting Started](#getting-started)
* [BASIC Compiler Language Specification](#basic-compiler-language-specification)
* [Samples](#samples)
* [Build Instructions](#build-instructions)

## Getting Started

I have prepared for you a [release](https://github.com/lwiest/BASICCompiler/releases/latest) of BASICCompiler.

### Prerequisites
* You have installed a Java SDK 5 (or higher) on your system.

### Instructions
1. Download [BASICCompiler.jar](https://github.com/lwiest/BASICCompiler/releases/download/v1.5/BASICCompiler.jar) to a folder.
2. Open a command prompt in that folder and enter:
   ``` 
   java -jar BASICCompiler.jar
   ```
This runs BASIC Compiler and lists command-line options:
```
 ___   _   ___  _  ___    ___                _ _
| _ ) /_\ / __|| |/ __|  / __|___ _ __  _ __(_) |___ _ _
| _ \/ _ \\__ \| | (__  | (__/ _ \ '  \| '_ \ | | -_) '_|
|___/_/ \_\___/|_|\___|  \___\___/_|_|_| .__/_|_|___|_|
                                       |_|
Version 1.6 (22-DEC-2019) by Lorenz Wiest

Usage: java BASICCompiler <BASIC source filename> <Java class filename> [<options>]
Options: -formatted=<filename> | Writes a formatted BASIC source file
         -optimize             | Applies compiler optimizations
```

Option `-formatted=<filename>` writes a pretty-printed version of the BASIC program to `<filename>`. The line numbers of the BASIC program are renumbered from 1000 on in increments of 10.

Option `-optimize` applies compiler optimizations. As of now, the only implemented optimization is the folding of constant string arguments of one or more `PRINT` statements.

**To compile a BASIC program**, for example [STARTREK.BAS](samples/STARTREK.BAS) in folder [samples](samples), enter

```
java -jar BASICCompiler.jar samples/STARTREK.BAS StarTrek
```

**To run the compiled program**, enter

```
java StarTrek
```

## BASIC Compiler Language Specification

Find more information about the implemented BASIC language in [doc/BASICCompilerLanguage.pdf](doc/BASICCompilerLanguage.pdf).

## Samples

Find sample BASIC programs from David Ahl's classic _BASIC Computer Games_ books, original (used with permission) and modified, in folder [samples](samples).
Below are transcripts of runs from some compiled sample BASIC programs:

```
F:\BASICCompiler>java 3dplot
                             3D-PLOT
              CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY




                        *
                    *   *  *
                 *  *   *  *   *
             *   *  *   *  *   *  *
             *   *  *   *  *   *  *
             *   *  *   *  *   *  *
          *  *   *  *   *  *   *  *   *
          *  *   *   *  *   *  *  *   *
          *  *   *   *   *  *  *  *   *
      *   *  *   *    *  *   * *  *   *  *
      *   *  *    *   *    * *  * *   *  *
      *   *   *   *     *   *  **  *  *  *
      *   *   *    *      *    * * *  *  *
      *   *   *     *       *    * *  *  *
      *   *   *      *        *      **  *
      *   *    *       *         *       *
      *   *    *        *           *       *
      *   *     *         *            *        *
      *   *     *          *             *         *
      *   *     *          *               *         *
   *  *   *     *           *              *          *
      *   *     *          *               *         *
      *   *     *          *             *         *
      *   *     *         *            *        *
      *   *    *        *           *       *
      *   *    *       *         *       *
      *   *   *      *        *      **  *
      *   *   *     *       *    * *  *  *
      *   *   *    *      *    * * *  *  *
      *   *   *   *     *   *  **  *  *  *
      *   *  *    *   *    * *  * *   *  *
      *   *  *   *    *  *   * *  *   *  *
          *  *   *   *   *  *  *  *   *
          *  *   *   *  *   *  *  *   *
          *  *   *  *   *  *   *  *   *
             *   *  *   *  *   *  *
             *   *  *   *  *   *  *
             *   *  *   *  *   *  *
                 *  *   *  *   *
                    *   *  *
                        *
```
```
F:\BASICCompiler>java Amazing
                           AMAZING PROGRAM
              CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY


WHAT ARE YOUR WIDTH AND LENGTH?22,15

+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+  +--+--+--+--+--+
|                       |     |     |           |        |        |
|  +--+--+--+--+--|  |  |  |  |  |  +--+--+--|  |  |  |  |  +--|  +
|        |           |     |     |           |  |  |  |  |     |  |
+--+--+--|  |  +--+--+--+--+--+--+--+--+--|  |  |  |  +--|  +--|  +
|           |                 |  |     |     |     |     |  |     |
+--+--|  +--+--+--+--|  +--|  |  |  |  |  +--|  +--+--|  |  |  +--+
|        |           |     |     |  |  |        |     |  |  |     |
|  +--+--|  +--+--|  +--+--|  +--|  |  +--+--+--|  |  |  +--+--|  +
|  |     |  |     |  |     |  |     |              |  |           |
|  |  |  |  |  |  |  |  |  +--|  +--+--|  +--+--+--+--|  +--+--|  +
|     |  |  |  |  |     |        |        |  |        |  |     |  |
+--+--|  |  |  |  +--+--+--+--+--|  +--+--|  |  +--|  |  |  |  |  +
|     |  |  |  |                    |        |     |     |  |  |  |
|  |  |  +--|  |  +--+--+--+--+--+--|  +--+--|  |  +--+--|  |  +--+
|  |     |     |           |     |           |  |  |        |     |
|  +--+--|  +--+--|  +--+--|  |  |  +--+--|  +--|  |  +--+--+--|  +
|           |     |  |        |  |        |        |  |           |
+--+--+--+--|  |  |  |  +--+--|  +--+--|  +--+--+--+--|  |  +--|  +
|  |     |     |     |        |        |        |        |     |  |
|  |  |  |  |  +--|  +--+--|  +--+--+--+--|  |  |  +--+--+--|  |  +
|     |     |     |     |  |  |           |  |              |  |  |
+--+--+--+--+--|  +--|  |  |  |  +--+--|  |  +--+--+--+--+--|  |  +
|  |           |     |     |  |  |     |  |     |              |  |
+--+--|  +--|  +--|  +--+--|  |  |  |  |  +--|  |  +--+--+--+--+--+
|  |     |     |     |        |     |  |     |  |  |        |     |
+--|  |  +--+--|  |  |  +--+--+--+--|  +--|  |  +--|  |  |  |  |  +
|     |           |     |           |  |  |  |        |  |  |  |  |
|  +--+--+--+--+--+--+--|  +--+--|  |  |  |  |  +--+--|  |  +--|  +
|                       |        |        |  |        |  |        |
+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|  +--+--+--+
```

```
F:\BASICCompiler>java Camel
                         CAMEL
                   CREATIVE COMPUTING
                 MORRISTOWN, NEW JERSEY



WOULD YOU LIKE INSTRUCTIONS?Y

  WELCOME TO CAMEL. THE OBJECT IS TO TRAVEL
200 MILES ACROSS THE GREAT GOBI DESERT.
A TRIBE OF KNOCKED-KNEED PYGMIES WILL BE CHASING YOU.
YOU WILL BE ASKED FOR COMMANDS EVERY SO OFTEN.



C O M M A N D S :
#1 DRINK FROM YOUR CANTEEN
#2 AHEAD MODERATE SPEED
#3 AHEAD FULL SPEED
#4 STOP FOR THE NIGHT
#5 STATUS CHECK
#6 HOPE FOR HELP





YOU HAVE ONE QUART OF WATER WHICH WILL LAST YOU SIX DRINKS.
YOU MAY RENEW YOUR WATER SUPPLY COMPLETELY AT AN OASIS.
YOU GET A HALF A QUART IF FOUND BY HELP.
IF HELP DOES NOT FIND YOU AFTER COMMAND SIX, YOU LOSE.
GOOD LUCK AND GOOD CAMELING!
YOU ARE IN THE MIDDLE OF THE DESERT AT AN OASIS.
YOU HAVE TRAVELLED 0 MILES ALLTOGETHER.
WHAT IS YOUR COMMAND?3
YOUR CAMEL IS BURNING ACROSS THE DESERT SANDS.

YOU HAVE TRAVELLED 18 MILES ALLTOGETHER.
WHAT IS YOUR COMMAND?3
YOUR CAMEL IS BURNING ACROSS THE DESERT SANDS.

----------W A R N I N G---------- GET A DRINK
YOU HAVE TRAVELLED 18 MILES ALLTOGETHER.
WHAT IS YOUR COMMAND?4
YOUR CAMEL THANKS YOU!
THE PYGMIES ARE 6 MILES BEHIND YOU.
YOU HAVE TRAVELLED 18 MILES ALLTOGETHER.
WHAT IS YOUR COMMAND?1
BETTER WATCH FOR AN OASIS!
WHAT IS YOUR COMMAND?3
YOUR CAMEL IS BURNING ACROSS THE DESERT SANDS.

THE PYGMIES HAVE CAPTURED YOU. CAMEL AND PEOPLE SOUP IS
THEIR FAVORITE DISH!!!


WANT A NEW CAMEL AND A NEW GAME?
-----------------
     CHICKEN
-----------------
```
```
F:\BASICCompiler>java Chase
                         CHASE
                   CREATIVE COMPUTING
                 MORISTOWN, NEW JERSEY



YOU ARE WITHIN THE WALLS OF A HIGH VOLTAGE MAZE.
THERE ARE FIVE SECURITY MACHINES TRYING TO DESTROY YOU.
YOU ARE THE '*', THE INTERCEPTORS ARE THE '+'.
THE AREAS MARKED 'X' ARE HIGH VOLTAGE.
YOUR ONLY CHANCE FOR SURVIVAL IS TO MANEUVER EACH
INTERCEPTOR INTO AN 'X'. GOOD LUCK!
MOVES ARE   7.8.9
            4.*.6
            1.2.3

10 = NO MOVE FOR THE REST OF THE GAME
-1 = GAVE UP, SITUATION HOPELESS
 0 = A TREMENDOUS (BUT UNFORTUNATELY RANDOM) LEAP

XXXXXXXXXXXXXXXXXXXX
X  +              XX
X         X+  +    X
X     X         +  X
X   *     XX       X
X                XXX
X X            X+ XX
X        X    X    X
X    X   X         X
XXXXXXXXXXXXXXXXXXXX
?6
XXXXXXXXXXXXXXXXXXXX
X                 XX
X   +     X        X
X     X   +  +     X
X    *    XX   +   X
X              + XXX
X X            X  XX
X        X    X    X
X    X   X         X
XXXXXXXXXXXXXXXXXXXX
?6
XXXXXXXXXXXXXXXXXXXX
X                 XX
X         X        X
X    +X            X
X     *  +XX+ +    X
X                XXX
X X            X  XX
X        X    X    X
X    X   X         X
XXXXXXXXXXXXXXXXXXXX
?2
XXXXXXXXXXXXXXXXXXXX
X                 XX
X         X        X
X     X            X
X     +   XX       X
X     * +  + +   XXX
X X            X  XX
X        X    X    X
X    X   X         X
XXXXXXXXXXXXXXXXXXXX
?0
$6,000,000 JUMP!!!
XXXXXXXXXXXXXXXXXXXX
X                 XX
X         X        X
X   *+X            X
X      +  XX+      X
X                XXX
X X            X  XX
X        X    X    X
X    X   X         X
XXXXXXXXXXXXXXXXXXXX
?8
*** YOU HAVE BEEN DESTROYED BY A LUCKY COMPUTER ***
ANOTHER GAME (Y/N)?
```

```
F:\BASICCompiler>java Eliza
                         ELIZA
                   CREATIVE COMPUTING
                 MORRISTOWN, NEW JERSEY



HI! I'M ELIZA. WHAT'S YOUR PROBLEM?
?THE OTHER DAY I HAD A DREAM ABOUT COMPUTERS
WHAT DOES THAT DREAM SUGGEST TO YOU?
?I AM CODING TOO MUCH
DID YOU COME TO ME BECAUSE YOU ARE CODING TOO MUCH
?PERHAPS
SAY, DO YOU HAVE ANY PSYCHOLOGICAL PROBLEMS?
?I AM WONDERING ABOUT MY DREAMS
HOW LONG HAVE YOU BEEN WONDERING ABOUT YOUR DREAMS
?FOR SOME WEEKS
WHAT DOES THAT SUGGEST TO YOU?
?IT IS SOMETHING THAT MAKES ME PONDER
I SEE.
?
```

```
F:\BASICCompiler>java Inkblot
                         INKBLOT
                   CREATIVE CMOPUTING
                 MORRISTOWN, NEW JERSEY


$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$   $$$$$$$$$$$$$                                    $$$$$$$$$$$$$   $$
$$$$$$$$$$$$$$$$$$$$$                              $$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$       $$$$$$$$$$$$       $$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$  $$$$$$$$$$$$$$$$$$$$  $$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$ $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ $$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$      $$$$$$$$$$$$$$
$$                    $$$$$$$$$$$$$$$$$$$$$$$$$$$$                    $$
$$       $$$$$$$$$$$$$$$  $$$$$$$$    $$$$$$$$  $$$$$$$$$$$$$$$       $$
$$   $$$$$$$$$$$$$$$$$$$$$$                  $$$$$$$$$$$$$$$$$$$$$$   $$
$$ $$$$$$$$$$$$$$$$$$$$$$$$$                $$$$$$$$$$$$$$$$$$$$$$$$$ $$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$                $$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$                  $$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$                    $$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$                          $$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$     $$$$$$                $$$$$$     $$$$$$$$$$$$$$$$$
$$$$$$$$$$         $$$$$$$$$$$$          $$$$$$$$$$$$         $$$$$$$$$$
$$$$$$$$         $$$$$$$$$$$$$$$        $$$$$$$$$$$$$$$         $$$$$$$$
$$               $$$$$$$$$$$$$$$$      $$$$$$$$$$$$$$$$               $$
$$               $$$$$$$$$$$$$$$$      $$$$$$$$$$$$$$$$               $$
$$               $$$$$$$$$$$$$$$        $$$$$$$$$$$$$$$               $$
$$$$$$$$$$$        $$$$$$$$$$$  $$$$$$$$  $$$$$$$$$$$        $$$$$$$$$$$
$$$$$$$$$$$$$          $$$   $$$$$$$$$$$$$$   $$$          $$$$$$$$$$$$$
$$$$$$$$$$$$$$             $$$$$$$$$$$$$$$$$$             $$$$$$$$$$$$$$
$$$$$$$$$$$$$$      $$    $$$$$$$$$$$$$$$$$$$$    $$      $$$$$$$$$$$$$$
$$$$$$$$$$$$$     $$$$$$ $$$$$$$$$$$$$$$$$$$$$$ $$$$$$     $$$$$$$$$$$$$
$$$$$$$$$$$$    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$    $$$$$$$$$$$$
$$$$$$$$$$$     $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$     $$$$$$$$$$$
$$$$$$$$$      $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$      $$$$$$$$$
$$$$$          $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$          $$$$$
$$         $$$ $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ $$$         $$
$$     $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$     $$
$$   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$   $$
$$ $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ $$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$  $$$$$$$$$    $$$$$$$$$  $$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$                            $$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$  $$$$$$$$$$$$$    $$$$$$$$$$$$$  $$$$$$$$$$$$$$$$$$$
$$ $$$$$$$$$$$$$$$                                    $$$$$$$$$$$$$$$ $$
$$   $$$$$$$$$$$                                        $$$$$$$$$$$   $$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
```

```
F:\BASICCompiler>java Lunar
                                LUNAR
              CREATIVE COMPUTING MORRISTOWN, NEW JERSEY



THIS IS A COMPUTER SIMULATION OF AN APOLLO LUNAR
LANDING CAPSULE.


THE ON-BOARD COMPUTER HAS FAILED (IT WAS MADE BY
XEROX) SO YOU HAVE TO LAND THE CAPSULE MANUALLY.

SET BURN RATE OF RETRO ROCKETS TO ANY VALUE BETWEEN
0 (FREE FALL) AND 200 (MAXIMUM BURN) POUNDS PER SECOND.
SET NEW BURN RATE EVERY 10 SECONDS.

CAPSULE WEIGHT 32,500 LBS; FUEL WEIGHT 16,500 LBS.



GOOD LUCK

SEC           MI + FT       MPH           LB FUEL       BURN RATE

 0             120  0        3600          16500        ?0
 10            109  5015     3636          16500        ?0
 20            99  4223      3672          16500        ?0
 30            89  2903      3708          16500        ?0
 40            79  1055      3743.999      16500        ?0
 50            68  3959      3779.999      16500        ?0
 60            58  1055      3815.999      16500        ?0
 70            47  2903      3851.999      16500        ?200
 80            37  1883      3482.868      14500        ?200
 90            28  1191      3086.708      12500        ?200
 100           20  1251      2659.654      10500        ?200
 110           13  2549      2196.946      8500         ?200
 120           8  370        1692.634      6500         ?200
 130           4  658        1139.137      4500         ?200
 140           1  4204       526.5975      2500         ?200
 150           1  1404      -158.1441      500          ?200
FUEL OUT AT 152.5 SECONDS
ON MOON AT 356.9128 SECONDS - IMPACT VELOCITY 393.2947 MPH
SORRY THERE NERE NO SURVIVORS. YOU BLOW IT!
IN FACT, YOU BLASTED A NEW LUNAR CRATER 89.27791 FEET DEEP!



TRY AGAIN??

SET BURN RATE OF RETRO ROCKETS TO ANY VALUE BETWEEN
0 (FREE FALL) AND 200 (MAXIMUM BURN) POUNDS PER SECOND.
SET NEW BURN RATE EVERY 10 SECONDS.

CAPSULE WEIGHT 32,500 LBS; FUEL WEIGHT 16,500 LBS.



GOOD LUCK

SEC           MI + FT       MPH           LB FUEL       BURN RATE

 0             120  0        3600          16500        ?
```

```
F:\BASICCompiler>java StarTrek











                                    ,------*------,
                    ,-------------   '---  ------'
                     '-------- --'      / /
                         ,---' '-------/ /--,
                          '----------------'

                    The USS ENTERPRISE --- NCC-1701





Your orders are as follows:
   Destroy the 17 Klingon warships which have invaded the Galaxy before
   they can attack Federation Headquarters on Stardate 2432.
   This gives you 32 days.
   There is 1 Starbase in the Galaxy for resupplying your ship.

Hit RETURN when ready to accept command.


Your mission begins with your Starship located in the Galactic quadrant
SPICA III.

---------------------------------
                                        Stardate           2400
                                        Condition          Green
                                        Quadrant           7 , 8
     <*>                                Sector             2 , 4
                  *                     Photon Torpedoes   10
                                        Total Energy       3000
                                        Shields            0
                                        Klingons Remaining 17
---------------------------------
Command ?HLP

You are the Captain of the Starship ENTERPRISE. Navigate the universe and
destroy all Klingon cruisers. Dock at a Starbase to refuel and repair the
ENTERPRISE.

The universe consists of 8 x 8 quadrants, each quadrant of 8 x 8 sectors.

Available commands are:

  NAV  Set the course of the ENTERPRISE. Directions are given as
          4 3 2
           \|/
          5-+-1
           /|\
          6 7 8
  SRS  Display a short-range-sensor scan, a map of the sector you are in.
       It displays the following objects:
          <*> - ENTERPRISE
          +K+ - Klingon cruiser
          >!< - Starbase
           *  - Star
  LRS  Display a long-range-sensor scan, a map of the surrounding
       quadrants. They are encoded as ABC with
          A   - Number of Klingon cruisers
          B   - Number of Starbases
          C   - Number of Stars
  PHA  Fire phasers
  TOR  Fire photon torpedoes
  SHE  Raise or lower shields
  DAM  Display Damage-Control Report
  COM  Call on Library-Computer. Available computer commands are:
          0   - Cumulative Galactic Record
          1   - Status Report
          2   - Photon Torpedo Data
          3   - Starbase Nav Data
          4   - Direction/Distance Calculator
          5   - Galaxy 'Region Name' Map
  XXX  Resign your command
  HLP  Display help

Command ?
```

```
F:\BASICCompiler>java Wumpus
                                WUMPUS
              CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY



INSTRUCTIONS (Y-N)?Y
WELCOME TO 'HUNT THE WUMPUS'

  THE WUMPUS LIVES IN A CAVE OF 20 ROOMS. EACH ROOM
HAS 3 TUNNELS LEADING TO OTHER ROOMS. (LOOK AT A
DODECAHEDRON TO SEE HOW THIS WORKS - IF YOU DON'T KNOW
WHAT A DODECAHEDRON IS, ASK SOMEONE)

HAZARDS:
  BOTTOMLESS PITS - TWO ROOMS HAVE BOTTOMLESS PITS IN THEM
     IF YOU GO THERE, YOU FALL INTO THE PIT (& LOSE!)
  SUPERBATS - TWO OTHER ROOMS HAVE SUPER BATS. IF YOU
     GO THERE, A BAT GRABS YOU AND TAKES YOU TO SOME OTHER
     ROOM AT RANDOM (WHICH MIGHT BE TROUBLESOME).

WUMPUS:
  THE WUMPUS IS NOT BOTHERED BY THE HAZARDS (HE HAS SUCKER
  FEET AND IS TOO BIG FOR A BAT TO LIFT). USUALLY
  HE IS ASLEEP. TWO THINGS THAT WAKE HIM UP: YOUR ENTERING
  HIS ROOM OR YOUR SHOOTING AN ARROW.
    IF THE WUMPUS WAKES, HE MOVES (P=.75) ONE ROOM
  OR STAYS STILL (P=.25). AFTER THAT, IF HE IS WHERE YOU
  ARE, HE EATS YOU UP (& YOU LOSE!)

YOU:
  EACH TURN YOU MAY MOVE OR SHOOT A CROOKED ARROW:
  MOVING - YOU CAN GO ONE ROOM (THRU ONE TUNNEL).
  ARROWS - YOU HAVE 5 ARROWS. YOU LOSE WHEN YOU RUN OUT.
    EACH ARROW CAN GO FROM 1 TO 5 ROOMS. YOU AIM BY TELLING
    THE COMPUTER THE ROOMS YOU WANT THE ARROW TO GO TO.
    IF THE ARROW CAN'T GO THAT WAY (IE NO TUNNEL) IT MOVES
    AT RANDOM TO THE NEXT ROOM.
    IF THE ARROW HITS THE WUMPUS, YOU WIN.
    IF THE ARROW HITS YOU, YOU LOSE.

WARNINGS:
  WHEN YOU ARE ONE ROOM AWAY FROM WUMPUS OR HAZARD,
  THE COMPUTER SAYS:
    WUMPUS - 'I SMELL A WUMPUS'
    BAT    - 'BATS NEARBY'
    PIT    - 'I FEEL A DRAFT'

HUNT THE WUMPUS

YOU ARE in ROOM 7
TUNNELS LEAD TO 6  8  17

SHOOT TO MOVE (S-M)?M
WHERE TO?6

YOU ARE in ROOM 6
TUNNELS LEAD TO 5  7  15

SHOOT TO MOVE (S-M)?M
WHERE TO?5

I FEEL A DRAFT!
YOU ARE in ROOM 5
TUNNELS LEAD TO 1  4  6

SHOOT TO MOVE (S-M)?S
NO. OF ROOMS (1-5)?1
ROOM NO.?1
MISSED

I FEEL A DRAFT!
YOU ARE in ROOM 5
TUNNELS LEAD TO 1  4  6

SHOOT TO MOVE (S-M)?M
WHERE TO?4
YYYIIIIEEEE ... FELL IN PIT!
HA HA HA - YOU LOSE!
SAME SET-UP (Y-N)?
```

## Build Instructions

**Prerequisites:** You have Java SDK 5 (or higher) installed on your system.

Download this project's ZIP file from GitHub and unzip it to a temporary folder ("root" folder).

**To work with the BASIC Compiler source code in your Eclipse IDE**, import the `BASICCompiler` project in your Eclipse IDE from the root folder as an import source _General > Existing Projects into Workspace_.

**To compile BASIC Compiler into a convenient JAR file** (Windows only), open a command prompt in the root folder and enter (`%JAVA_HOME%` must point to the installation folder of your Java SDK):

```
makejar
```
This produces the `BASICCompiler.jar` file, containing the compiled BASIC Compiler.
