100 PRINT TAB(33);"LUNAR"
110 PRINT TAB(15);"CREATIVE COMPUTING MORRISTOWN, NEW JERSEY"
120 PRINT
130 PRINT
140 PRINT "This is a computer simulation of an Apollo Lunar Landing Capsule."
150 PRINT
160 PRINT "The on-board computer has failed (it was made by XEROX) so you have to"
170 PRINT "land the capsule manually."
180 PRINT
190 PRINT "* Set burn rate of retro rockets to any value between"
200 PRINT "  0 (free fall) and 200 (maximum burn) pounds per second."
210 PRINT "* Set new burn rate every 10 seconds."
220 PRINT "* Capsule weight is 32,500 lbs."
230 PRINT "* Fuel weight is 16,500 lbs."
240 PRINT
250 PRINT "GOOD LUCK!"
260 L=0
270 PRINT
280 PRINT "Sec","Mi + Ft","Mph","Lbs Fuel","Burn Rate"
290 A=120 : V=1 : M=33000 : N=16500 : G=1E-03 : Z=1.8
300 PRINT L,INT(A);INT(5280*(A-INT(A))),INT(3600*V*100)/100,INT((M-N)*100)/100, : INPUT K
310 T=10
320 IF M-N<1E-03 THEN 400
330 IF T<1E-03 THEN 300
340 S=T : IF M>=N+S*K THEN 360
350 S=(M-N)/K
360 GOSUB 640 : IF I<=0 THEN 560
370 IF V<=0 THEN 390
380 IF J<0 THEN 590
390 GOSUB 540 : GOTO 320
400 PRINT "Fuel out at";L;"seconds."
410 S=(-V+SQR(V*V+2*A*G))/G
420 V=V+G*S : L=L+S
430 W=3600*V
440 PRINT
441 PRINT "On Moon at";INT(L*10)/10;"seconds - Impact velocity";INT(W*10)/10;"mph."
450 IF W<=1.2 THEN PRINT "PERFECT LANDING!" : GOTO 670
460 IF W<=10 THEN PRINT "Good landing (Could be better)." : GOTO 670
470 IF W>60 THEN 510
480 PRINT "Craft damage... You're stranded here until a rescue party arrives."
490 PRINT "Hope you have enough oxygen!"
500 GOTO 670
510 PRINT "Sorry there were no survivors. You blew it!"
520 PRINT "In fact, you blasted a new lunar crater";INT(W*0.277*10)/10;"feet deep!"
530 GOTO 670
540 L=L+S : T=T-S : M=M-S*K : A=I : V=J
550 RETURN
560 IF S<5E-03 THEN 430
570 D=V+SQR(V*V+2*A*(G-Z*K/M)) : S=2*A/D
580 GOSUB 640 : GOSUB 540 : GOTO 560
590 W=(1-M*G/(Z*K))/2 : S=M*V/(Z*K*(W+SQR(W*W+V/Z)))+0.05 : GOSUB 640
600 IF I<=0 THEN 560
610 GOSUB 540 : IF J>0 THEN 320
620 IF V>0 THEN 590
630 GOTO 320
640 Q=S*K/M : J=V+G*S+Z*-Q*(1+Q*(1/2+Q*(1/3+Q*(1/4+Q/5))))
650 I=A-G*S*S/2-V*S+Z*S*Q*(1/2+Q*(1/6+Q*(1/12+Q*(1/20+Q/30))))
660 RETURN
670 PRINT
680 PRINT "Try again?"
690 GOTO 180